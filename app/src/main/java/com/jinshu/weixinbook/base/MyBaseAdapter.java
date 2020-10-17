package com.jinshu.weixinbook.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 周见阳 2017/8/11.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter  {
    protected List<T> myList;
    protected Context context;
    public MyBaseAdapter(Context context){
       this. context=context;
    }
    @Override
    public int getCount() {
        if (myList!=null){
            return myList.size();
        }else{
            return 0;
        }

    }

    public void setMyList(List<T> myList) {
        this.myList = myList;
    }

    public List<T> getMyList() {
        return myList;
    }

    @Override
    public T getItem(int position) {
        if (position<getCount())return  myList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public abstract int setLatoutId();
    public abstract AdapterHolder setAdapterHolder(View convertView,int position);
    public abstract void initViews(AdapterHolder holder, T item, int position );
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdapterHolder holder = null;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(setLatoutId(), null);
            holder = setAdapterHolder(view,position);
            view.setTag(holder);
        } else {
            view=convertView;
            holder = (AdapterHolder) view.getTag();
        }
        initViews(holder, getItem(position), position );
        return view;
    }
}
