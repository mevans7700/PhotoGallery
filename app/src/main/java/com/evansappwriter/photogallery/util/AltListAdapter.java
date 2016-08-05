package com.evansappwriter.photogallery.util;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by markevans on 7/25/16.
 */
public abstract class AltListAdapter<T extends Comparable<? super T>> extends RecyclerView.Adapter<Holder<T>> {
    List<T> mList = new ArrayList<>();

    @Override
    public void onBindViewHolder(Holder<T> holder, int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public T getItem(int adapterPosition){
        return mList.get(adapterPosition);
    }

    public List<T> getList() {
        return mList;
    }

    public void add(T object) {
        mList.add(object);
    }

    public List<T> swapItems(List<T> objects) {
        List<T> oldObjects = mList;

        mList = objects;

        // notify the observers about the new objects
        notifyDataSetChanged();

        return oldObjects;
    }

    public void addAll(Collection<? extends T> collection) {
        mList.addAll(collection);

        notifyDataSetChanged();
    }

    public void addAll(T... items) {
        Collections.addAll(mList, items);
    }

    public void insert(T object, int index) {
        mList.add(index, object);
    }

    public void remove(T object) {
        mList.remove(object);
    }

    public void clear() {
        mList.clear();
    }

    public void sort(Comparator<? super T> comparator) {
        Collections.sort(mList, comparator);
    }

    public void sort() {
        Collections.sort(mList);
    }
}
