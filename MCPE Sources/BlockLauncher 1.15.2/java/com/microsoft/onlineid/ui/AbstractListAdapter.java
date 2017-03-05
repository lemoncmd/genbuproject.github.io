package com.microsoft.onlineid.ui;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractListAdapter<T> implements ListAdapter {
    protected List<T> _items = new ArrayList();
    protected Set<DataSetObserver> _observers = new HashSet();

    private void fireChanged() {
        onChanged();
        for (DataSetObserver onChanged : this._observers) {
            onChanged.onChanged();
        }
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public int getCount() {
        return this._items.size();
    }

    public T getItem(int i) {
        return this._items.get(i);
    }

    public abstract View getView(int i, View view, ViewGroup viewGroup);

    public boolean hasStableIds() {
        return true;
    }

    public boolean isEmpty() {
        return this._items.isEmpty();
    }

    public boolean isEnabled(int i) {
        return true;
    }

    protected void onChanged() {
    }

    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        this._observers.add(dataSetObserver);
    }

    public void setContent(Collection<? extends T> collection) {
        this._items.clear();
        this._items.addAll(collection);
        fireChanged();
    }

    public void setContent(T... tArr) {
        this._items.clear();
        Collections.addAll(this._items, tArr);
        fireChanged();
    }

    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        this._observers.remove(dataSetObserver);
    }
}
