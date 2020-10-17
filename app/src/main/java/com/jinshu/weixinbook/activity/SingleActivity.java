package com.jinshu.weixinbook.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jinshu.weixinbook.MainActivity;
import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.base.BaseActivity;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.db.DatabaseManager;
import com.jinshu.weixinbook.utils.GlobeData;
import com.jinshu.weixinbook.utils.SPUtils;
import com.jinshu.weixinbook.utils.StringUtils;
import com.jinshu.weixinbook.utils.Utils;
import com.jinshu.weixinbook.utils.sns.MomentModel;
import com.jinshu.weixinbook.utils.sns.SnsHelper;
import com.jinshu.weixinbook.utils.sns.UserModel;
import com.jinshu.weixinbook.widget.AbstractAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SingleActivity extends BaseActivity {

    TextView item_name;
    TextView item_log;
    TextView et_remark;
    Button but_submit;
    String siteUrl;
    Handler mHandler = new Handler();
    public List<UserModel> followList = new ArrayList<>();
    public List<UserModel> userModels = new ArrayList<>();
    public List<UserModel> userModelsWeixin = new ArrayList<>();
    DatabaseManager dm;
    /**
     * 是否不验证图片
     */
    private boolean isNotVerifyImage = false;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_single;
    }

    @Override
    protected void initViews() {
        setTitle("个人");
        setBackAction();
        getMyActionBar().addAction(new AbstractAction("上传") {
            @Override
            public int getBackgroundResource() {
                return R.color.theme_color;
            }

            @Override
            public void performAction(View view) {
                isNotVerifyImage = false;
                upload();
            }
        });
        item_name = (TextView) findViewById(R.id.item_name);
        item_log = (TextView) findViewById(R.id.item_log);
        et_remark = (TextView) findViewById(R.id.et_remark);
        but_submit = (Button) findViewById(R.id.but_submit);
        siteUrl = (String) SPUtils.get(baseActivity, Constant.siteUrl, "");
        but_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String beizhu = et_remark.getText().toString();
                if (StringUtils.isNotEmpty(beizhu)) {
                    isNotVerifyImage = true;
                    upload();
                } else {
                    Toast.makeText(baseActivity, "请输入唯一的备注名", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dm = new DatabaseManager(this);
        UserModel model = getIntent().getParcelableExtra("UserModel");
        item_name.setText(model.nickname);
        userModels.clear();
        userModels.add(model);

    }

    private void upload() {
        item_log.setText("");
        followList.clear();
        userModelsWeixin.clear();
        showProgress("正在上传...");
        Date date = new Date();//创建现在的日期
        long time = date.getTime() / 1000;//获得毫秒数
        for (int i = 0; i < userModels.size(); i++) {
            UserModel model = userModels.get(i);
            if (StringUtils.isNotEmpty(model.weixinId)) {
                userModelsWeixin.add(model);
            } else {
                followList.add(model);
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadMomentList();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        stopProgress();
                        Toast.makeText(baseActivity, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }


    private void loadMomentList() {
        try {
            SnsHelper.loadAllUser(baseActivity, SnsHelper.findUserFolder("") + "/");
        } catch (final Exception e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    item_log.setText("错误" + e.toString());
                }
            });
        }
        for (int i = 0; i < userModelsWeixin.size(); i++) {
            UserModel model = userModelsWeixin.get(i);
            readSnsInfo(model.weixinId, model);
        }
        if (followList.size() > 0) {
            String beizhu = et_remark.getText().toString();
            if (StringUtils.isNotEmpty(beizhu)) {
                remarkName_chazhao(beizhu);
            } else {
                name_select();
            }
        }
    }

    private void name_select() {
        String Folder = SnsHelper.findUserFolder("") + "/";
        for (int j = 0; j < GlobeData.listRcontact.size(); j++) {
            String userNameMap_nickname = GlobeData.listRcontact.get(j).nickname;
            String username = GlobeData.listRcontact.get(j).username;
            for (int i = 0; i < followList.size(); i++) {
                UserModel model = followList.get(i);
                if (model.nickname.equals(userNameMap_nickname)) {

                    String avatar = SnsHelper.findAvatar(baseActivity, username, Folder);
                    String sha1 = null;
                    if (!TextUtils.isEmpty(avatar)) {//图片的处理
                        avatar = avatar.replace("/96", "/0");
                        avatar = avatar.replace("/0", "/46");
                        sha1 = Utils.getFileSha1byte(avatar);
                    }
                    if (sha1 != null && sha1.equals(model.headurlsha)) {
                        dm.insertMembers(username, model, siteUrl);
                        model.weixinId=username;
                        readSnsInfo(username, model);
                        break;
                    }
                }
            }
        }
    }

    private void remarkName_chazhao(String remark) {
        String Folder = SnsHelper.findUserFolder("") + "/";
        UserModel model = followList.get(0);
        for (int j = 0; j < GlobeData.listRcontact.size(); j++) {
            String conRemark = GlobeData.listRcontact.get(j).conRemark;
            String username = GlobeData.listRcontact.get(j).username;
            if (remark.equals(conRemark)) {
                if (isNotVerifyImage) {
                    dm.insertMembers(username, model, siteUrl);
                    readSnsInfo(username, model);
                    break;
                }
                String avatar = SnsHelper.findAvatar(baseActivity, username, Folder);
                String sha1 = null;
                if (!TextUtils.isEmpty(avatar)) {//图片的处理
                    avatar = avatar.replace("/96", "/0");
                    avatar = avatar.replace("/0", "/46");
                    sha1 = Utils.getFileSha1byte(avatar);
                }
                if (sha1 != null && sha1.equals(model.headurlsha)) {
                    dm.insertMembers(username, model, siteUrl);
                    readSnsInfo(username, model);
                    break;
                }
            }
        }
    }

    public void readSnsInfo(String alias, UserModel model) {
        Log.i("alias===", alias);
        alias = alias.trim();
        String Folder = SnsHelper.findUserFolder("") + "/";

        String paramString = Folder;
        //周改
        String userName = alias;
        final ArrayList<MomentModel> momentList = SnsHelper.getSnsRecordsByUserName(this, paramString, userName, model);
        //Log.e("readSnsInfo", "userName = " + userName + ", alias = " + alias + ", momentList.size()  = " + momentList.size() );
        if (momentList.size() > 0) {
            final String name = userName;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    String text = item_log.getText().toString();
                    item_log.setText(text + "\n" + name + "开始上传" + momentList.size() + "个朋友圈");
                }
            });
        }
        for (int i = 0; i < momentList.size(); i++) {
            Log.i("momentList=====", momentList.get(i).toString());
        }
        model.uploadNumStame = momentList.size();
        model.uploadNumEnd = 0;
        if (momentList.size() > 20) {
            int nCount = momentList.size() / 20;
            for (int i = 0; i < nCount; i++) {
                ArrayList<MomentModel> list = new ArrayList<MomentModel>();
                list.addAll(momentList.subList(i * 20, (i + 1) * 20));

                uploadMoments(JSON.toJSONString(list), momentList.get(0), model, 20);
            }

            int nLeft = momentList.size() % 20;
            if (nLeft > 0) {
                ArrayList<MomentModel> list = new ArrayList<MomentModel>();
                list.addAll(momentList.subList(nCount * 20, momentList.size()));
                uploadMoments(JSON.toJSONString(list), momentList.get(0), model, list.size());
            }
        } else {
            if (momentList.size() > 0) {
                uploadMoments(JSON.toJSONString(momentList), momentList.get(0), model, momentList.size());
            }
        }

    }

    private void uploadMoments(final String data, final MomentModel model, final UserModel userModel, final int count) {

        //Log.e("uploadMoments",  "data: " + data);
        //Utils.HOST + "/public/index.php/admin/Wechat/uploadWechatContent"
        String url = siteUrl + "/admin/Wechat/uploadWechatContent";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                try {
                    JSONObject jsonObj = (JSONObject) JSON.parse(result);
                    String text = item_log.getText().toString();
                    item_log.setText(text + "\n" + userModel.nickname + "上传成功" + count + "个朋友圈");
                    userModel.uploadNumEnd += count;
                    if (userModel.uploadNumEnd == userModel.uploadNumStame) {
                        if (StringUtils.isNotEmpty(userModel.headimgurl)) {
                            appSendSms(userModel);
                        } else {
                            uploadCreatebook(userModel);
                        }

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                Log.e("", "onErrorResponse = " + arg0.toString(), arg0);
//                byte[] htmlBodyBytes = arg0.networkResponse.data;
//                Log.e("LOGIN-ERROR", new String(htmlBodyBytes), arg0);
                String text = item_log.getText().toString();
                item_log.setText(text + "\n" + userModel.nickname + "上传失败" + count + "个朋友圈");

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("openid", userModel.openid);
                map.put("uid", userModel.uid);
                map.put("booktype", "1");


                map.put("data", data);
                //String json = map.toString().replace("=", ":");//JSON.toJSONString(map.toString());
                Log.e("", "getParams = " + map.toString());

                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000, 5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queues.add(stringRequest);
    }

    private void appSendSms(final UserModel model) {
        String url = siteUrl + "/admin/Wechat/appSendSms";
        showProgress("正在通知...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                Date date = new Date();//创建现在的日期
                long time = date.getTime() / 1000;//获得毫秒数
                dm.updateTime(model, time + "");
                stopProgress();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
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

    private void uploadCreatebook(final UserModel model) {
        String url = siteUrl + Constant.uploadCreatebook;
        showProgress("正在创建微信书...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                Date date = new Date();//创建现在的日期
                long time = date.getTime() / 1000;//获得毫秒数
                dm.updateTime(model, time + "");
                stopProgress();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    ProgressDialog progressDialog;

    private void showProgress(String text) {
        progressDialog = new ProgressDialog(this);//设置进度条风格，风格为圆形，旋转的

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//设置ProgressDialog 标题
//设置ProgressDialog 提示信息
        //progressDialog.setTitle("提示");
        progressDialog.setMessage(text);

//设置ProgressDialog 的进度条是否不明确
        progressDialog.setIndeterminate(false);
//设置ProgressDialog 是否可以按退回按键取消
        progressDialog.setCancelable(true);
//显示
        progressDialog.show();

    }

    private void stopProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onPause() {
        if (progressDialog != null && !this.isFinishing()) {
            progressDialog.dismiss();
        }
        super.onPause();
    }
}
