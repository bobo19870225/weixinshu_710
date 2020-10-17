package com.jinshu.weixinbook.mvp;

import android.content.Context;

import com.android.volley.RequestQueue;

/**
 * Created by jinshu on 2017/8/24.
 */

public class BaseModel {
    public RequestQueue queues;
    public BaseModel(RequestQueue queues){
        this.queues=queues;
    }
}
