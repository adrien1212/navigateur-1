package org.javastreet.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryEntry {

    //private int id;
    private String link;
    private String name;
    private Date date;

    SimpleDateFormat formatter;

    public HistoryEntry(String link, String name, Date date) {
        //this.id = id;
        this.link = link;
        this.name = name;
        this.date = date;
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
        return "HistoryEntry{" +
                ", link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", date=" + date +
                '}';
    }

    public String getStringForHistory() {

        return ""+formatter.format(date)+ " - " + name + " | " + link;
    }
}
