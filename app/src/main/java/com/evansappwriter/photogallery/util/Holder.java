package com.evansappwriter.photogallery.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by markevans on 7/25/16.
 */
public abstract class Holder<T> extends RecyclerView.ViewHolder {
    T data;

    public Holder(View itemView) {
        super(itemView);
    }

    public void bindData(T data){
        this.data = data;
        bindViews(data);
    }

    abstract protected void bindViews(T data);
}
