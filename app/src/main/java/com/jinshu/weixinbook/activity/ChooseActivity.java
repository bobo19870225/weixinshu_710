package com.jinshu.weixinbook.activity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jinshu.weixinbook.LoginToSiteModel;
import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.base.BaseActivity;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.jsonParsing.ParseHelper;
import com.jinshu.weixinbook.utils.SPUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChooseActivity extends BaseActivity {


    EditText editText;

    Button button;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_choose;
    }

    @Override
    protected void initViews() {
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
    }


    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.editText:
                break;
            case R.id.button: {
                //Log.e("uploadMoments",  "data: " + data);
                showWaitDialog();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.loginToSite, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        Log.e("", "result = " + result);
                        List<LoginToSiteModel> iqm = ParseHelper.getModelList(result, "body.data.rows", LoginToSiteModel.class);
                        if (iqm != null && iqm.size() > 0) {
//                            Constant.siteUrl = iqm.get(0).masterURL;
                            SPUtils.put(baseActivity,Constant.siteUrl ,iqm.get(0).masterURL);
//                            Constant.siteID = iqm.get(0).siteID;
                            SPUtils.put(baseActivity,Constant.siteID ,iqm.get(0).siteID);
                            SPUtils.put(baseActivity,Constant.listImage ,iqm.get(0).listImage);
                            SPUtils.put(baseActivity,Constant.faceImage ,iqm.get(0).faceImage);
                            jump(UsrloginActivity.class, null);
                            finish();
                            return;
                        }
                        hideWaitDialog();
                        showErrorDialog("编号错误，请重试");
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        Log.e("", "onErrorResponse = " + arg0.toString());
                        hideWaitDialog();
                        showErrorDialog("网络错误，请重试");
                    }

                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("applicationID", "8a2f462a5ba31ecb015ba80098f0319f");
                        map.put("loginName", editText.getText().toString());
                        return map;
                    }
                };

//                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                        30000,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queues.add(stringRequest);
            }

            break;
        }
    }


}