package com.evansappwriter.photogallery.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.evansappwriter.photogallery.R;
import com.evansappwriter.photogallery.model.Photo;
import com.evansappwriter.photogallery.util.Keys;
import com.evansappwriter.photogallery.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by markevans on 8/4/16.
 */
public class PhotoViewerDialogFragment extends DialogFragment {
    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.count) TextView mCountTV;
    @BindView(R.id.title) TextView mTitleTV;
    @BindView(R.id.date) TextView mDateTV;

    private String TAG = "PhotoViewerDialogFragment";
    private static final String SERVER_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DISPLAY_DATE_PATTERN = "MM-dd-yyyy";
    private ArrayList<Photo> mPhotos;

    private int mSelectedPosition = 0;

    private Unbinder unbinder;

    public static PhotoViewerDialogFragment newInstance() {
        return new PhotoViewerDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.photo_viewer_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);

        mPhotos = (ArrayList<Photo>) getArguments().getSerializable(Keys.KEY_PHOTOS);
        mSelectedPosition = getArguments().getInt(Keys.KEY_SELECTED);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        mViewPager.setAdapter(myViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                displayMetaInfo(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setCurrentItem(mSelectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position, false);
        displayMetaInfo(mSelectedPosition);
    }

    private void displayMetaInfo(int position) {
        mCountTV.setText(String.format(getResources().getString(R.string.photo_count),
                String.valueOf((position + 1)), String.valueOf( mPhotos.size())));

        Photo photo = mPhotos.get(position);
        mTitleTV.setText(photo.getTitle());

        // Format date for viewing
        /*********************************************************/
        SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(photo.getDateTaken());
        } catch (ParseException e) {
            Utils.printStackTrace(e);
        }
        Calendar serverCalendar = Calendar.getInstance(TimeZone.getDefault());
        serverCalendar.setTime(date);
        sdf.applyPattern(DISPLAY_DATE_PATTERN);
        /*********************************************************/

        mDateTV.setText(sdf.format(serverCalendar.getTime()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        @BindView(R.id.fullscreen) ImageView imageViewPreview;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.photo_fullscreen, container, false);
            ButterKnife.bind(this, view);

            Photo photo = mPhotos.get(position);

            String url = "";
            if (!TextUtils.isEmpty(photo.getUrlLarge())) {
                url = photo.getUrlLarge();
            } else if (!TextUtils.isEmpty(photo.getUrlOriginal())) {
                url = photo.getUrlOriginal();
            } else if (!TextUtils.isEmpty(photo.getUrlMedium())) {
                url = photo.getUrlMedium();
            } else if ((!TextUtils.isEmpty(photo.getUrlThumb()))) {
                url = photo.getUrlThumb();
            }

            Glide.with(getActivity()).load(url)
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.default_photo)
                    .error(R.drawable.default_photo)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return mPhotos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
