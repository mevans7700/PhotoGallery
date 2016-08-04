package com.evansappwriter.photogallery.model;

import java.io.Serializable;

/**
 * Created by markevans on 8/4/16.
 */
public class Photo implements Serializable, Comparable<Photo> {
    private String mId;
    private String mTitle;
    private String mUrlThumb;
    private String mUrlMedium;
    private String mUrlLarge;
    private String mUrlOriginal;
    private String mDateTaken;

    public Photo() {

    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrlThumb() {
        return mUrlThumb;
    }

    public void setUrlThumb(String urlThumb) {
        mUrlThumb = urlThumb;
    }

    public String getUrlMedium() {
        return mUrlMedium;
    }

    public void setUrlMedium(String urlMedium) {
        mUrlMedium = urlMedium;
    }

    public String getUrlLarge() {
        return mUrlLarge;
    }

    public void setUrlLarge(String urlLarge) {
        mUrlLarge = urlLarge;
    }

    public String getUrlOriginal() {
        return mUrlOriginal;
    }

    public void setUrlOriginal(String urlOriginal) {
        mUrlOriginal = urlOriginal;
    }

    public String getDateTaken() {
        return mDateTaken;
    }

    public void setDateTaken(String dateTaken) {
        mDateTaken = dateTaken;
    }

    @Override
    public int compareTo(Photo another) {
        return 0;
    }
}
