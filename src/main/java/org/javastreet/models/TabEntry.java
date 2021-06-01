package org.javastreet.models;

import javafx.scene.control.Tab;
import javafx.scene.web.WebView;

public class TabEntry {
	private WebView webView;
	private Tab tab;
	private boolean privateTab;
	
	public TabEntry(WebView webView, Tab tab, boolean privateTab) {
		this.webView = webView;
		this.tab = tab;
		this.privateTab = privateTab;
	}

	public WebView getWebView() {
		return webView;
	}
	
	public void setWebView(WebView webView) {
		this.webView = webView;
	}
	
	public Tab getTab() {
		return tab;
	}
	
	public void setTab(Tab tab) {
		this.tab = tab;
	}
	
	public boolean isPrivate() {
		return this.privateTab;
	}
	
	public void setPrivate(boolean privateTab) {
		this.privateTab = privateTab;
	}
	
}
