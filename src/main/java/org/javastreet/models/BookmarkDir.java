package org.javastreet.models;

import java.util.ArrayList;

public class BookmarkDir {

    private int id;
    private String name;
    private ArrayList<Bookmark> bookmarks;

    public BookmarkDir(String name) {
        this.name = name;
        bookmarks = new ArrayList<>();
    }

    public BookmarkDir(int id,String name) {
        this.id = id;
        this.name = name;
        bookmarks = new ArrayList<>();
    }

    public BookmarkDir(int id, String name, ArrayList<Bookmark> bookmarks) {
        this.id = id;
        this.name = name;
        this.bookmarks = bookmarks;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public boolean addBookmark (Bookmark b) {
        return bookmarks.add(b);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
