package com.jinshu.weixinbook;


import android.app.Application;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.common.QueuedWork;

/**
 * Created by jinshu on 2017/7/6.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wxc702007962714b59","be2d883ca9ebf01bbb27dfbe0ff784df");
        Config.DEBUG = true;
    }
}
