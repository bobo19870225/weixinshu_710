package com.jinshu.weixinbook.activity;

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
import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.base.BaseActivity;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.jsonParsing.ParseHelper;
import com.jinshu.weixinbook.mvp.Host.HostActivity;
import com.jinshu.weixinbook.utils.SPUtils;
import com.jinshu.weixinbook.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UsrloginActivity extends BaseActivity {


    EditText editText;

    Button button;
    private ImageView iv_logo;
    @Override
    protected int setContentViewId() {
        return R.layout.activity_uerlogin;
    }

    @Override
    protected void initViews() {
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        String str=  (String) SPUtils.get(baseActivity, Constant.listImage, "");
        iv_logo= (ImageView) findViewById(R.id.iv_logo);
        if(StringUtils.isNotEmpty(str)){
            Glide.with(baseActivity).load(str).into(iv_logo);
        }else{
            iv_logo.setImageResource(R.drawable.logo_gui);
//            android:src="@drawable/logo_gui"
        }
    }


    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.editText:
                break;
            case R.id.button: {
                //Log.e("uploadMoments",  "data: " + data);
                showWaitDialog();
                wechatRegisterAndLogin();
            }

            break;
        }
    }
    private void wechatRegisterAndLogin( ) {
        showWaitDialog("登录中。。。");
        String siteUrl = (String) SPUtils.get(baseActivity, Constant.siteUrl, "");
        if (!StringUtils.isNotEmpty( siteUrl)) {
            hideWaitDialog();
            showErrorDialog("请重新选择站点");
            return;
        }
        if(StringUtils.isEmpty(editText.getText().toString())){
            showErrorDialog("请输入编号");
            return;
        }
//        {
//            SPUtils.put(baseActivity, Constant.uid,editText.getText().toString());
////                Constant.uid = ParseHelper.getString(result, "data.uid");
//            hideWaitDialog();
////                jump(MainActivity.class, null);
//            jump(HostActivity.class, null);
//            finish();
//            return;
//        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, siteUrl+ Constant.appRegisterAndLogin, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                String code = ParseHelper.getString(result, "code");
                if (code==null||"0".equals(code)) {
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
                map.put("id",  editText.getText().toString());
                map.put("phpsessionid", "1");
                return map;
            }
        };
        queues.add(stringRequest);
    }

}