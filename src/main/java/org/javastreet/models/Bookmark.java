package org.javastreet.models;

public class Bookmark {

    private int id;
    private String name;
    private String link;
    private int dirId;

    public Bookmark(String name, String link, int dirId) {
        this.name = name;
        this.link = link;
        this.dirId= dirId;
    }
    public Bookmark(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public Bookmark(int id, String name, String link, int dirId) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.dirId = dirId;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDirId() {
        return dirId;
    }

    public void setDirId(int dirId) {
        this.dirId = dirId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", dirId=" + dirId +
                '}';
    }

    public String toBookmarkString() {
        return "" + this.name + " | " + this.link;
    }
}
