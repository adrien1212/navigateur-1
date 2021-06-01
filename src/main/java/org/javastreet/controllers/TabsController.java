package org.javastreet.controllers;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.javastreet.models.HistoryEntry;
import org.javastreet.models.TabEntry;
import org.javastreet.utils.Configuration;
import org.javastreet.utils.DBBookmarks;
import org.javastreet.utils.DBCookies;
import org.javastreet.utils.DBHistory;
import org.javastreet.utils.NavigationUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class TabsController {
	
	// TabPane JavaFX component
	@FXML
	private TabPane tabPane;

	// Tab list
	private List<TabEntry> tabs;
	
	// Current Tab
	private TabEntry currentTab;

	// Instance of the window controller
	private WebViewController controlsController;

	// Instance of the address bar JavaFX component
	private TextField addressBar;
	
	// Instance of the progress bar JavaFX component
	private ProgressBar progressBar; 
	
	// Database instance of the history
	private DBHistory history;

	// Database instance of the cookie store
	private DBCookies myCookies;
	
	// Instance of non-private tabs Cookie Manager
	private CookieManager cookieManager;
	
	// Instance of the configuration
	private Configuration config;

	// Default directory for the localStorage
	private static final File LOCAL_STORAGE_FILE = new File("src/main/resources/localStorage");

	@FXML
	public void initialize() {
		tabs = new ArrayList<>();

		// Initialize TabPane
		tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
		tabPane.setTabMaxWidth(100D);
		tabPane.setTabMinWidth(100D);
		tabPane.setTabMinHeight(25D);

		// Add event on tab click
		tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			for (TabEntry tabEntry : tabs) {
				if (tabEntry.getTab().equals(newTab)) {
					this.updateCurrentTab(tabEntry);	
				}
			}
		});

		// Initialize storage / cookies
		this.history = new DBHistory();

		myCookies = new DBCookies();
		cookieManager = new CookieManager();

        // Cookie Manager
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

		// Load cookies into webview when starting the app
		myCookies.getCookiesList().forEach(cookie -> {
			cookieManager.getCookieStore().add(URI.create(cookie.getDomain()), cookie);
		});
	}

	/**
	 * Get the browser's current active tab.
	 * @return the browser's current active tab.
	 */
	public TabEntry getCurrentTab() {
		return this.currentTab;
	}

	/**
	 * Add a new tab to the tab list.
	 * @param privateTab whether the new tab is private or not.
	 */
	public void addNewTab(boolean privateTab) {
		WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine();
		Worker<Void> worker = webEngine.getLoadWorker();

		config = Configuration.getInstance();

		Tab tab = new Tab("Loading ...", webView);
		TabEntry newTab = new TabEntry(webView, tab, privateTab);
		
		// Set the tab's background to black and the foreground.
		if (privateTab) {
			tab.setStyle("-fx-background-color: black; -fx-text-base-color: white");
		} else {
			// If the tab isn't private, we do store localStorage.
			webEngine.setUserDataDirectory(LOCAL_STORAGE_FILE);
		}
		
		// Attach a handler when the tab is closed.
		tab.setOnClosed(e -> {
			if (this.getCurrentTab().equals(newTab) && tabs.size() > 1) {
				this.updateCurrentTab(tabs.get(tabs.size() - 1));
			} else if (tabs.size() == 2) {
				// Prevent having 0 tabs.
				tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
			}

			// We don't save cookies inside private tab.
			if (!newTab.isPrivate()) {
				this.saveCookies();
			}

			tabs.remove(newTab);
		});

		// Assign the newly created tab to the current one.
		this.updateCurrentTab(newTab);

		// Listening to the status of worker.
		worker.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				updateCurrentTab(getCurrentTab());
				if (newValue == Worker.State.SUCCEEDED) {
					progressBar.setOpacity(0);
					try {
						if (!privateTab) {
							history.insert(new HistoryEntry(webEngine.getTitle(), webEngine.getLocation(), new java.util.Date()));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					progressBar.setOpacity(1);
				}
			}
		});
		
        webEngine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
            addressBar.setText(newLoc);
        });

		NavigationUtils.search(config.getEngineURL(), newTab.getWebView().getEngine());

		// Add the created tab to the window, and focus it.
		this.tabs.add(newTab);
		this.tabPane.getTabs().add(newTab.getTab());
		this.tabPane.getSelectionModel().select(newTab.getTab());

		if (tabs.size() > 1 && tabPane.getTabClosingPolicy() == TabClosingPolicy.UNAVAILABLE) {
			tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		}
	}

	/**
	 * Make tabEntry the current and selected tab.
	 * @param tabEntry the tab to select.
	 */
	private void updateCurrentTab(TabEntry tabEntry) {
		if (this.currentTab != tabEntry) {
			this.currentTab = tabEntry;	
		}

		// We create a new CookieManager for each private tab, such as
		// each tab has an isolated context. If the tab isn't private,
		// we use the "main" cookie manager.
		if (tabEntry.isPrivate()) {
			CookieHandler.setDefault(new CookieManager());
		} else if (CookieHandler.getDefault() != cookieManager) {
			CookieHandler.setDefault(cookieManager);
		}

		// Modify the view's informations.
		if (this.addressBar != null) {
			this.addressBar.setText(tabEntry.getWebView().getEngine().getLocation());
			String title = NavigationUtils.getTitle(tabEntry.getWebView().getEngine());
			if (!title.equals("")) {
				this.currentTab.getTab().setText(title);
			}

			// Bind progress bar to loading status of the worker
			progressBar.progressProperty().bind(tabEntry.getWebView().getEngine().getLoadWorker().progressProperty());
		}
	}

	/**
	 * Assign the window controller instance.
	 * @param controlsController the window controller.
	 */
	public void setControlsController(WebViewController controlsController) {
		this.controlsController = controlsController;
		if (this.controlsController != null) {
			this.addressBar = this.controlsController.getAddressBar();
			this.progressBar = this.controlsController.getProgressBar();
		}
	}

	/**
	 * Save in-memory cookies to file system.
	 */
	public void saveCookies() {
		// Delete all cookies from the DB so that we don't store duplicates
		// and only store cookies from the webview that are not stale
		// (the webview automatically delete)
		myCookies.deleteAll();
		cookieManager.getCookieStore().getCookies().forEach(cookie -> {
			myCookies.insert(cookie);
		});
	}
}
