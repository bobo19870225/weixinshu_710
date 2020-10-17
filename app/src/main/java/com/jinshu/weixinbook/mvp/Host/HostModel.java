package com.jinshu.weixinbook.mvp.Host;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.jsonParsing.ParseHelper;
import com.jinshu.weixinbook.mvp.BaseModel;
import com.jinshu.weixinbook.mvp.IBaseModel;
import com.jinshu.weixinbook.utils.SPUtils;
import com.jinshu.weixinbook.utils.StringUtils;
import com.jinshu.weixinbook.utils.sns.UserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jinshu on 2017/8/24.
 */

public class HostModel extends BaseModel implements IBaseModel {

    String siteUrl = "";

    public HostModel(RequestQueue queues) {
        super(queues);
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    /**
     * 刷新用户
     */
    public void getLatestUsers(final IBaseModel.RequestListening requestListening) {
//        String url = siteUrl + "/admin/Wechat/check_60_mins_subscribe_time";
        String url = siteUrl + Constant.refreshNew;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.e("onResponse", "result = " + result);
                List<UserModel> userModels = new ArrayList<>();
                try {
                    JSONObject jsonObj = (JSONObject) JSON.parse(result);
                    if (jsonObj != null && jsonObj.containsKey("status")) {
                        JSONArray data = jsonObj.getJSONArray("data");
//                        StringBuilder sb = new StringBuilder("");
                        for (int i = 0; i < data.size(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            UserModel model = new UserModel();
                            model.uid = obj.getString("uid");
                            model.nickname = obj.getString("nickname");
                            model.openid = obj.getString("openid");
                            model.headimgurl = obj.getString("headimgurl");
                            model.headurlsha = obj.getString("headurlsha");
                            try{
                                long rt=obj.getLong("refresh_time");
                                if(rt!=0){
                                    model.refreshTime=obj.getLong("refresh_time");
                                }else{
                                    model.refreshTime=obj.getLong("subscribe_time");
                                }
                            }catch (Exception e){
                                model.refreshTime=obj.getLong("subscribe_time");
                            }
//                            sb.append(model.nickname + ",");
                            userModels.add(model);
                        }
                        Collections.sort(userModels, new Comparator() {
                            @Override
                            public int compare(Object lhs, Object rhs) {
                                UserModel um1 = (UserModel) lhs;
                                UserModel um2 = (UserModel) rhs;
                                return um1.openid.compareTo(um2.openid);
                            }
                        });
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                requestListening.onResponse(userModels);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                requestListening.onErrorResponse(arg0);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queues.add(stringRequest);
    }

    /**
     * 上传
     *
     * @param data
     * @param userModel
     */
    public void uploadMoments(final String data, final UserModel userModel, final IBaseModel.RequestListening requestListening) {

        String url = siteUrl + "/admin/Wechat/uploadWechatContent";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                requestListening.onResponse("");
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                requestListening.onErrorResponse(arg0);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("openid", userModel.openid);
                map.put("uid", userModel.uid);
                map.put("booktype", "1");
                map.put("data", data);
                Log.e("", "getParams = " + map.toString());
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000, 5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queues.add(stringRequest);
    }

    /**
     * 通知
     * @param model
     */
    public void appSendSms(final UserModel model, final IBaseModel.RequestListening requestListening) {
        String url = siteUrl + Constant.appSendSms;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                requestListening.onResponse("");

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                requestListening.onErrorResponse(arg0);
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                map.put("openid", model.openid);
                Log.e("", "getParams = " + map.toString());
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queues.add(stringRequest);
    }


    /**
     * 上下班
     *
     * @param map
     * @param requestListening
     */
    @Override
    public void appAttendancerun(final Map<String, String> map, final IBaseModel.RequestListening requestListening) {
        Log.i("Constant.siteUrl", Constant.siteUrl + Constant.appAttendancerun + "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, siteUrl + Constant.appAttendancerun, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                String message = ParseHelper.getString(result, "message");
                requestListening.onResponse(message);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                Log.e("", "onErrorResponse = " + arg0.toString());
                requestListening.onErrorResponse(arg0);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };
        queues.add(stringRequest);
    }
    /**
     * 注册
     *
     * @param requestListening
     */
    @Override
    public void otherMemberRegister(final  UserModel um, final IBaseModel.RequestListening requestListening) {
        Log.i("Constant.siteUrl", Constant.siteUrl + Constant.appAttendancerun + "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, siteUrl + Constant.otherMemberRegister, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                String message = ParseHelper.getString(result, "message");
                um.openid= ParseHelper.getString(result, "data.openid");
                um.uid= ParseHelper.getString(result, "data.uid");
                requestListening.onResponse(message);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                Log.e("", "onErrorResponse = " + arg0.toString(), arg0);
                byte[] htmlBodyBytes = arg0.networkResponse.data;
                Log.e("LOGIN-ERROR", new String(htmlBodyBytes), arg0);
                requestListening.onErrorResponse(arg0);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map=new HashMap<>();
                map.put("nickname",um.nickname);
                return map;
            }
        };
        queues.add(stringRequest);
    }

}
