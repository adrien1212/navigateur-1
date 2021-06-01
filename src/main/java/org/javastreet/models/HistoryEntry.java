package org.javastreet.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryEntry {

    private int id;
    private String link;
    private String name;
    private Date date;

    SimpleDateFormat formatter;

    /**
     * @param link lien url du site 
     * @param name du site
     * @param date de consultation du site
     */
    public HistoryEntry(String link, String name, Date date) {
        this.link = link;
        this.name = name;
        this.date = date;
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    public HistoryEntry(int id, String link, String name, Date date) {
    	this(link, name, date);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return ""+formatter.format(date)+ " - " + name + " | " + link;
    }

}
