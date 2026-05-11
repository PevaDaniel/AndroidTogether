package com.example.myapplication;

public class Music {
    public long id;
    public String title;
    public String artist;
    public String coverUri;
    public boolean isFavourite = false;

    public Music(long id, String title, String artist, String coverUri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.coverUri = coverUri;
    }
}
