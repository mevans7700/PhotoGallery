package com.evansappwriter.photogallery;

import android.app.Application;
import android.content.Context;

/**
 * Created by markevans on 8/4/16.
 */
public class PhotoGalleryApplication extends Application {
    private static Context mContext;
    private static PhotoGalleryApplication sInstance;

    public static Context getContext(){
        return mContext;
    }
    public static PhotoGalleryApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;

        sInstance = this;
    }
}
