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
	@FXML
	private TabPane tabPane;

	private List<TabEntry> tabs;
	private TabEntry currentTab;

	private WebViewController controlsController;

	private TextField addressBar;
	private ProgressBar progressBar; 
	private DBHistory history;
	private DBCookies myCookies;
	private CookieManager cookieManager;
	private Configuration config;
	
	private static File localStorageFile = new File("src/main/resources/localStorage");

	@FXML
	public void initialize() {
		tabs = new ArrayList<>();

		tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
		tabPane.setTabMaxWidth(100D);
		tabPane.setTabMinWidth(100D);
		tabPane.setTabMinHeight(25D);
		
		tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			for (TabEntry tabEntry : tabs) {
				if (tabEntry.getTab().equals(newTab)) {
					this.updateCurrentTab(tabEntry);	
				}
			}
		});

		this.history = new DBHistory();
		
        myCookies = new DBCookies();

        // Cookie Manager
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        // Load cookies into webview when starting the app
        myCookies.getCookiesList().forEach(cookie -> {
            cookieManager.getCookieStore().add(URI.create(cookie.getDomain()), cookie);
        });
	}

	public TabEntry getCurrentTab() {
		return this.currentTab;
	}

	public void addNewTab() {
		WebView webView = new WebView();
		config = Configuration.getInstance();
		
		Tab tab = new Tab("Loading ...", webView);
		
		TabEntry newTab = new TabEntry(webView, tab);
		tab.setOnClosed(e -> {
			if (this.getCurrentTab().equals(newTab) && tabs.size() > 1) {
				this.updateCurrentTab(tabs.get(tabs.size() - 1));
			} else if (tabs.size() == 2) {
				tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
			}
			
			tabs.remove(newTab);
		});

		this.updateCurrentTab(newTab);

		WebEngine webEngine = webView.getEngine();
		webEngine.setUserDataDirectory(localStorageFile);
		
		Worker<Void> worker = webEngine.getLoadWorker();

		// Listening to the status of worker
		worker.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				updateCurrentTab(getCurrentTab());
				if (newValue == Worker.State.SUCCEEDED) {
					progressBar.setOpacity(0);
				} else {
					progressBar.setOpacity(1);
				}
			}
		});
		
        webEngine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
            addressBar.setText(newLoc);
            try {
                history.insert(new HistoryEntry(webEngine.getTitle(), webEngine.getLocation(), new java.util.Date()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

		NavigationUtils.search(config.getEngineURL(), newTab.getWebView().getEngine());

		this.tabs.add(newTab);
		this.tabPane.getTabs().add(newTab.getTab());
		this.tabPane.getSelectionModel().select(newTab.getTab());
		
		if (tabs.size() > 1 && tabPane.getTabClosingPolicy() == TabClosingPolicy.UNAVAILABLE) {
			tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		}
	}

	private void updateCurrentTab(TabEntry tabEntry) {
		if (this.currentTab != tabEntry) {
			this.currentTab = tabEntry;	
		}

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

	public void setControlsController(WebViewController controlsController) {
		this.controlsController = controlsController;
		if (this.controlsController != null) {
			this.addressBar = this.controlsController.getAddressBar();
			this.progressBar = this.controlsController.getProgressBar();
		}
	}
	
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
