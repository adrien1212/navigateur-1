package org.javastreet.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {

    public static String request(String engine, String args[]){
        String url = "";
        switch(engine.toLowerCase()){
            case "duckduckgo":
                url = Query.webQuery("https://duckduckgo.com/?q=", args);
                break;
            case "ecosia":
                url = Query.webQuery("https://www.ecosia.org/search?q=", args);
                break;
            case "bing":
                url = Query.webQuery("https://www.bing.com/search?q=%s", args);
                break;
            case "youtube":
                url = Query.webQuery("https://www.youtube.com/results?search_query=", args);
                break;
            default:
                // par defaut avec google
                url = Query.webQuery("https://www.google.com/search?q=", args);
        }
        return url;
    }

    private static String webQuery(String engineUrl, String args[]){
        String keywords;
        String url;

        keywords = String.join("+", args);
        url = engineUrl + keywords;

        // check if well formed
        if(isUrl(url)){
            return url;
        }
        return "";
    }

    private static Boolean isUrl(String url){
        Pattern urlPattern;
        Matcher urlMatcher;

        urlPattern = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        urlMatcher = urlPattern.matcher(url);

        return urlMatcher.find();
    }

    private Boolean isHost(String host){
        Pattern urlPattern;
        Matcher urlMatcher;

        urlPattern = Pattern.compile("(\\w+\\.\\w+)+");
        urlMatcher = urlPattern.matcher(host);

        return urlMatcher.find();
    }
}
