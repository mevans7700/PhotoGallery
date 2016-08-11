package com.evansappwriter.photogallery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evansappwriter.photogallery.R;
import com.evansappwriter.photogallery.util.AltListAdapter;
import com.evansappwriter.photogallery.util.Utils;

import java.util.List;

/**
 * Created by markevans on 7/25/16.
 */
public abstract class BaseListFragment<T extends Comparable<? super T>> extends Fragment {
    private static final String TAG = "BASE.LIST.FRAGMENT";

    protected int crtPage = 1;

    private RecyclerView mRecyclerView;
    private AltListAdapter<T> mAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView.LayoutManager mLayoutManager;
    /**
     * Empty public constructor. Read here why this is needed:
     * http://developer.android.com/reference/android/app/Fragment.html
     */
    @SuppressWarnings("unused")
    public BaseListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.printLogInfo(TAG, "onCreateView(): ", toString());
        return inflater.inflate(R.layout.basic_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(getRetainInstance() ? null : savedInstanceState);

        Utils.printLogInfo(TAG, "onActivityCreated(): ", toString());

        if (mAdapter == null) {
            mAdapter = onCreateEmptyAdapter();
        }
        mRecyclerView.setAdapter(mAdapter);

        // Get type of layoutmanager
        mLayoutManager = onCreateLayoutManager();
        mRecyclerView.setLayoutManager(mLayoutManager);

        // turn on or off Swipe Container
        mSwipeContainer.setEnabled( onSetupSwipeContainer() );
    }

    protected abstract AltListAdapter<T> onCreateEmptyAdapter();

    protected abstract boolean onSetupSwipeContainer();

    protected abstract RecyclerView.LayoutManager onCreateLayoutManager();

    public SwipeRefreshLayout getSwipeContainer() {
        return mSwipeContainer;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    protected Bundle onPrepareGetNew() {
        return new Bundle();
    }

    public void getNew() {
        crtPage = 1;
        Utils.printLogInfo(TAG, "crtPage: ", crtPage);

        makeAPICall(onPrepareGetNew());
    }

    public void showNew(List<T> objects) {
        mAdapter.swapItems(objects);
    }

    protected Bundle onPrepareGetOlder() {
        return new Bundle();
    }

    protected void makeAPICall(Bundle args) {

    }

    protected void getOlder(int nextPage) {
        crtPage = nextPage;
        Utils.printLogInfo(TAG, "crtPage: ", crtPage);

        makeAPICall(onPrepareGetOlder());
    }

    protected void showOlder(List<T> objects) {
        if (objects == null) {
            return;
        }

        mAdapter.addAll(objects);

        //mAdapter.sort();
    }
}
