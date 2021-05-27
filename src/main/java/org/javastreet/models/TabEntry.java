package org.javastreet.models;

import javafx.scene.control.Tab;
import javafx.scene.web.WebView;

public class TabEntry {
	private WebView webView;
	private Tab tab;
	
	public TabEntry(WebView webView, Tab tab) {
		this.webView = webView;
		this.tab = tab;
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
	
}
