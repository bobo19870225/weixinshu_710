package com.jinshu.weixinbook.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.base.BaseActivity;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.utils.SPUtils;
import com.jinshu.weixinbook.utils.StringUtils;


public class FirstActivity extends BaseActivity {
    private ImageView iv_logo;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_first;
    }

    @Override
    protected void initViews() {
        String str=  (String) SPUtils.get(baseActivity, Constant.faceImage, "");
        iv_logo= (ImageView) findViewById(R.id.iv_logo);
        if(StringUtils.isNotEmpty(str)){
            Glide.with(baseActivity).load(str).into(iv_logo);
        }else{
            iv_logo.setImageResource(R.mipmap.bg_guide);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jump(ChooseActivity.class,null);
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation

    }


}
