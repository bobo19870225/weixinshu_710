package com.jinshu.weixinbook.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.jinshu.weixinbook.LoginToSiteModel;
import com.jinshu.weixinbook.MainActivity;
import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.base.BaseActivity;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.jsonParsing.ParseHelper;
import com.jinshu.weixinbook.mvp.Host.HostActivity;
import com.jinshu.weixinbook.utils.SPUtils;
import com.jinshu.weixinbook.utils.StringUtils;
import com.jinshu.weixinbook.widget.ActionBar;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZloginActivity extends BaseActivity {

    private Button button;
    private ImageView iv_logo;
    @Override
    protected int setContentViewId() {
        return R.layout.activity_zlogin;
    }

    @Override
    protected void initViews() {
//        ActionBar.setTitle("登录");
//        setBackAction();
        button = (Button) findViewById(R.id.btn_log);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickbutton(v);
            }
        });
        String str=  (String) SPUtils.get(baseActivity, Constant.listImage, "");
        iv_logo= (ImageView) findViewById(R.id.iv_logo);
        if(StringUtils.isNotEmpty(str)){
            Glide.with(baseActivity).load(str).into(iv_logo);
        }else{
            iv_logo.setImageResource(R.drawable.logo_gui);
//            android:src="@drawable/logo_gui"
        }
    }


    public void onclickbutton(View view) {
//        UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.WEIXIN, null);
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(this).setShareConfig(config);
        getPlatformInfo();
//        showWaitDialog("登录中。。。");
//        Log.i("asdasdasdasda", "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
//        UMShareAPI.get(getApplicationContext()).doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
//            @Override
//            public void onStart(SHARE_MEDIA platform) {
//                //授权开始的回调
//            }
//
//            @Override
//            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
////            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
////                hideWaitDialog();
//                getPlatformInfo();
//
//            }
//
//            @Override
//            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
////            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();     Log.i("s", code);
//                Log.i("s", "  ");
//                hideWaitDialog();
//                showToast("Authorize fail");
//
//            }
//
//            @Override
//            public void onCancel(SHARE_MEDIA platform, int action) {
////            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
//                Log.i("s", "  ");
//                hideWaitDialog();
//                showToast("Authorize fail");
//
//            }
//        });//弹出登录
//        UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, umAuthListener);//获取全部信息

    }

    private void getPlatformInfo() {
        showWaitDialog("登录中。。。");
        UMShareAPI.get(getApplicationContext()).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                //授权开始的回调
                showWaitDialog("登录中。。。");
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
//            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();

                wechatRegisterAndLogin(data);
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();     Log.i("s", code);
                Log.i("s", "  ");
                hideWaitDialog();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
//            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
                Log.i("s", "  ");
                hideWaitDialog();
            }
        });//拿信息
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    private void wechatRegisterAndLogin(final Map<String, String> data) {
        showWaitDialog("登录中。。。");
        String siteUrl = (String) SPUtils.get(baseActivity, Constant.siteUrl, "");
        if (!StringUtils.isNotEmpty( siteUrl)) {
            hideWaitDialog();
            showErrorDialog("请重新选择站点");
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, siteUrl+ Constant.appRegisterAndLogin, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                String code = ParseHelper.getString(result, "code");
                if ("0".equals(code)) {
                    Toast.makeText(baseActivity, "不好意思，您还不是小编", Toast.LENGTH_SHORT).show();
                    hideWaitDialog();
                    return;
                }
                SPUtils.put(baseActivity, Constant.uid, ParseHelper.getString(result, "data.uid"));
//                Constant.uid = ParseHelper.getString(result, "data.uid");
                hideWaitDialog();
//                jump(MainActivity.class, null);
                jump(HostActivity.class, null);
                finish();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                Log.e("", "onErrorResponse = " + arg0.toString(), arg0);
                byte[] htmlBodyBytes = arg0.networkResponse.data;
                Log.e("LOGIN-ERROR", new String(htmlBodyBytes), arg0);
                hideWaitDialog();
                showErrorDialog("网络错误，请重试");
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String siteID = (String) SPUtils.get(baseActivity, Constant.siteID, "");
                Map<String, String> map = new HashMap<String, String>();
                map.put("site_id",  siteID);
                map.put("openid", data.get("openid"));
                map.put("phpsessionid", "1");
                map.put("nickname", data.get("screen_name"));
//                map.put("nickname", "23232323");
                map.put("sex", data.get("gender"));
                map.put("city", data.get("city"));
                map.put("country", data.get("country"));
                map.put("province", data.get("province"));
                map.put("language", data.get("language"));
                map.put("headimgurl", data.get("profile_image_url"));

                return map;
            }
        };

        queues.add(stringRequest);
    }

}