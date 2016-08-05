package com.evansappwriter.photogallery.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.evansappwriter.photogallery.PhotoGalleryApplication;
import com.evansappwriter.photogallery.R;
import com.evansappwriter.photogallery.model.Photo;
import com.evansappwriter.photogallery.util.Holder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by markevans on 8/4/16.
 */
public class PhotoHolder extends Holder<Photo> {
    @BindView(R.id.thumbnail) ImageView mImageView;

    public PhotoHolder(View v){
        super(v);
        ButterKnife.bind(this, v);
    }

    @Override
    protected void bindViews(Photo photo) {
        String url = "";
        if (!TextUtils.isEmpty(photo.getUrlThumb())) {
            url = photo.getUrlThumb();
        } else if (!TextUtils.isEmpty(photo.getUrlMedium())) {
            url = photo.getUrlMedium();
        } else if (!TextUtils.isEmpty(photo.getUrlLarge())) {
            url = photo.getUrlLarge();
        } else if ((!TextUtils.isEmpty(photo.getUrlOriginal()))) {
            url = photo.getUrlOriginal();
        }

        Glide.with(PhotoGalleryApplication.getContext())
                .load(url)
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.default_photo)
                .error(R.drawable.default_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);
    }
}
