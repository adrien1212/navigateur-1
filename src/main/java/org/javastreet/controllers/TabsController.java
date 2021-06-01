package org.javastreet.controllers;

import java.util.ArrayList;
import java.util.List;

import org.javastreet.models.HistoryEntry;
import org.javastreet.models.TabEntry;
import org.javastreet.utils.DBHistory;
import org.javastreet.utils.NavigationUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
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
	}

	public TabEntry getCurrentTab() {
		return this.currentTab;
	}

	public void addNewTab() {
		WebView webView = new WebView();
		
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
		Worker<Void> worker = webEngine.getLoadWorker();

		// Listening to the status of worker
		worker.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				updateCurrentTab(getCurrentTab());
				if (newValue == Worker.State.SUCCEEDED) {
					history.insert(new HistoryEntry(NavigationUtils.getTitle(getCurrentTab().getWebView().getEngine()), getCurrentTab().getWebView().getEngine().getLocation(), new java.util.Date()));
					progressBar.setOpacity(0);
				} else {
					progressBar.setOpacity(1);
				}
			}
		});

		NavigationUtils.search("https://google.com", newTab.getWebView().getEngine());

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
}
