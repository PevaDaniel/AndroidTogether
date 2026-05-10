package com.example.myapplication;

public class Music {
    public long id;
    public String title;
    public String artist;
    public boolean isFavourite = false;

    public Music(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }
}