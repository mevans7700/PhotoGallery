package com.evansappwriter.photogallery.ui;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.evansappwriter.photogallery.R;
import com.evansappwriter.photogallery.core.APIParser;
import com.evansappwriter.photogallery.core.APIService;
import com.evansappwriter.photogallery.core.BundledData;
import com.evansappwriter.photogallery.model.Photo;
import com.evansappwriter.photogallery.util.AltListAdapter;
import com.evansappwriter.photogallery.util.EndlessRecyclerViewScrollListener;
import com.evansappwriter.photogallery.util.Keys;
import com.evansappwriter.photogallery.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by markevans on 8/4/16.
 */
public class GalleryFragment extends BaseListFragment<Photo> {
    private static final String TAG = "GalleryFragment";

    private BaseActivity mActivity;

    private boolean mFirstAPI;

    // empty public constructor
    // read here why this is needed:
    // http://developer.android.com/reference/android/app/Fragment.html
    @SuppressWarnings("unused")
    public GalleryFragment() {

    }

    public static GalleryFragment newInstance(Bundle b) {
        GalleryFragment f = new GalleryFragment();
        if (b != null) {
            f.setArguments(b);
        }
        f.setHasOptionsMenu(true);
        return f;
    }

    @Override
    protected AltListAdapter<Photo> onCreateEmptyAdapter() {
        return new AltListAdapter<Photo>(){
            @Override
            public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photo_thumbnail, parent, false);

                final PhotoHolder holder = new PhotoHolder(v);
                v.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Keys.KEY_PHOTOS, (Serializable) getList());
                        bundle.putInt(Keys.KEY_SELECTED, holder.getAdapterPosition());

                        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                        PhotoViewerDialogFragment newFragment = PhotoViewerDialogFragment.newInstance();
                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "photoviewer");
                    }
                });

                return holder;
            }
        };
    }

    @Override
    protected boolean onSetupSwipeContainer() {
        return true;
    }

    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new GridLayoutManager(mActivity,3);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = (BaseActivity) context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mActivity = (BaseActivity) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirstAPI = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // item views are the same height and width
        getRecyclerView().setHasFixedSize(true);

        // Setup refresh listener which triggers new data loading
        getSwipeContainer().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {
                    Toast.makeText(mActivity, getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show();
                    getSwipeContainer().setRefreshing(false);
                    return;
                }
                getNew();
            }
        });

        // scroll listener
        getRecyclerView().addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(mActivity, getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                getOlder(page);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mFirstAPI) {
            mActivity.showProgress(getString(R.string.progress_photos));
            getNew();
        }
        mFirstAPI = true;
    }

    @Override
    protected Bundle onPrepareGetNew() {
        Bundle params = new Bundle();
        params.putString(APIService.PARAM_PAGE, "1");
        return params;
    }

    @Override
    protected Bundle onPrepareGetOlder() {
        Bundle params = new Bundle();
        params.putString(APIService.PARAM_PAGE, String.valueOf(crtPage));
        return params;
    }

    protected void makeAPICall(final Bundle params) {
        APIService.getInstance().get(APIService.ENDPOINT_PHOTOS_RECENT, params, new APIService.OnUIResponseHandler() {

            @Override
            public void onSuccess(String payload) {
                if (mActivity == null || mActivity.isFinishing()) {
                    return;
                }

                if (payload != null) {
                    BundledData data = new BundledData(APIParser.TYPE_PARSER_PHOTOS);
                    data.setHttpData(payload);
                    APIParser.parseResponse(data);
                    if (data.getAuxData() == null) {
                        Utils.printLogInfo(TAG, "Parsing error: ", data.toString());
                        mActivity.showError(getString(R.string.error_title), getString(R.string.photo_get_error), null);
                    } else {
                        if (data.getAuxData().length == 1) {
                            ArrayList<Photo> photos = (ArrayList<Photo>) data.getAuxData()[0];
                            if (getSwipeContainer().isRefreshing()) {
                                getSwipeContainer().setRefreshing(false);
                            }
                            if (params.getString(APIService.PARAM_PAGE, "1").equals("1")) {
                                showNew(photos);
                            } else {
                                showOlder(photos);
                            }
                        } else {
                            Utils.printLogInfo(TAG, "Parsing error: ", data.getAuxData()[1]);
                            mActivity.showError(getString(R.string.error_title), getString(R.string.photo_get_error), null);
                        }
                    }
                } else {
                    Utils.printLogInfo(TAG, "Payload error: ", "No Payload but a status code of 200");
                    mActivity.showError(getString(R.string.error_title), getString(R.string.photo_get_error), null);
                }
                mActivity.dismissProgress();
            }

            @Override
            public void onFailure(String errorTitle, String errorText, int dialogId) {
                if (mActivity == null || mActivity.isFinishing()) {
                    return;
                }

                mActivity.dismissProgress();
                mActivity.showError(getString(R.string.error_title), getString(R.string.photo_get_error), null);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

}
