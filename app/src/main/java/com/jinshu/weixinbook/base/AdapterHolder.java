package com.jinshu.weixinbook.base;

import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinshu on 2017/8/11.
 */

public class AdapterHolder {
    private Map<Integer, View> viewMap = new HashMap<Integer, View>();
    private View parentView;
    public AdapterHolder(View parentView){
        this.parentView=parentView;
    }
    public void setText(int id,String text)
    {
        TextView view =   getView(id);
        view.setText(text);
    }
    public void setVisibility(int resId,int visibility)
    {
        View view = getView(resId);
        view.setVisibility(visibility);
    }
    public <T extends View> T getView(int resId)
    {

        T t = (T)viewMap.get(resId);

        if(t == null)
        {
            t = (T) parentView.findViewById(resId);
            if(t == null)
            {
                throw new NullPointerException("can not find id:"+resId);
            }
            else
            {
                viewMap.put(resId, t);
            }
        }
        return t;
    }
}
