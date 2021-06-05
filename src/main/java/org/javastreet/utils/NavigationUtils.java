package org.javastreet.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javastreet.utils.configurationHandle.ConfigurationCreator;
import org.javastreet.utils.configurationHandle.ConfigurationFileEngineSearch;
import org.javastreet.utils.configurationHandle.ConfigurationFileNavigator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.scene.web.WebEngine;

public class NavigationUtils {

	public static void search(String url, WebEngine webEngine){
		// Check if it's an url
		if(!isUrl(url)) {
			// check if it's an host
			if(isHost(url)){
				url = "https://" + url;
			} else {
				String keywords[] = url.split(" ");
				if(keywords[0].startsWith("@")){
					String engine = keywords[0].split("@")[1];
					System.out.println(engine);
					ConfigurationFileEngineSearch cfe = (ConfigurationFileEngineSearch) ConfigurationCreator.getInstance().getConfigurationFile("configurationFileEngineSearch");
					if(cfe.isAvailable(engine)){
						keywords[0] = "";
						url = Query.request(engine, keywords);
					}
				} else {
					ConfigurationCreator cc = ConfigurationCreator.getInstance();
					ConfigurationFileNavigator cfn = (ConfigurationFileNavigator) cc.getConfigurationFile("configurationFileNavigator");

					url = Query.request(cfn.getEngine(), keywords);
				}
			}
		}
		webEngine.load(url);
	}

	public static Boolean isUrl(String url){
        Pattern urlPattern;
        Matcher urlMatcher;

        urlPattern = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        urlMatcher = urlPattern.matcher(url);

        return urlMatcher.find();
    }

	public static Boolean isHost(String host){
		Pattern urlPattern;
		Matcher urlMatcher;

		urlPattern = Pattern.compile("(\\w+\\.\\w+)+");
		urlMatcher = urlPattern.matcher(host);

		return urlMatcher.find();
	}

	public static String googleQuery(String args[]){
		String keywords;
		String url;

		keywords = String.join("+", args);
		url = "https://www.google.com/search?q=" + keywords;

		// check if well formed
		if(isUrl(url)){
			return url;
		}
		return "";
	}

	public static String getTitle(WebEngine webEngine) {
		if (webEngine.getDocument() != null) {
			Document doc = webEngine.getDocument();
			NodeList heads = doc.getElementsByTagName("head");
			String titleText = webEngine.getLocation() ; // use location if page does not define a title
			if (heads.getLength() > 0) {
				Element head = (Element)heads.item(0);
				NodeList titles = head.getElementsByTagName("title");
				if (titles.getLength() > 0) {
					Node title = titles.item(0);
					titleText = title.getTextContent();
				}
			}
			return titleText ;
		}
		
		return "";
	}
}
