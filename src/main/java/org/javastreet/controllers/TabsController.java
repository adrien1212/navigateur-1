package org.javastreet.controllers;

import java.util.ArrayList;
import java.util.List;

import org.javastreet.models.TabEntry;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;

public class TabsController {
	@FXML
	private TabPane tabPane;
		
    private List<TabEntry> tabs;
	private TabEntry currentTab;
    
	@FXML
	public void initialize() {
		tabs = new ArrayList<>();
		this.addNewTab();
	}
	
	public TabEntry getCurrentTab() {
		return this.currentTab;
	}
	
	public void addNewTab() {
		WebView webView = new WebView();
		Tab tab = new Tab("google.com", webView);
		TabEntry newTab = new TabEntry(webView, tab);
		this.currentTab = newTab;
		this.tabs.add(newTab);
		this.tabPane.getTabs().add(tab);
	}
}
