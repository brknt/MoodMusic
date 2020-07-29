package com.example.moodmusic.Model;

import java.util.HashMap;

public class UploadSong {


    public String songsMood,songTitle,artist,songDuration,songLink;


    public UploadSong(String songsMood, String songTitle, String artist,String songDuration, String songLink) {

        if (songTitle.trim().equals("")){
            songTitle = "No title";
        }

        this.songsMood = songsMood;
        this.songTitle = songTitle;
        this.artist = artist;
        this.songDuration = songDuration;
        this.songLink = songLink;

    }

    public UploadSong() {
    }

    public String getSongsMood() {
        return songsMood;
    }

    public void setSongsMood(String songsMood) {
        this.songsMood = songsMood;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

}

