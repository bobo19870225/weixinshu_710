package com.jinshu.weixinbook;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.alibaba.fastjson.*;
import android.app.ProgressDialog;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jinshu.weixinbook.activity.HistoyActivity;
import com.jinshu.weixinbook.base.BaseActivity;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.db.DatabaseManager;
import com.jinshu.weixinbook.jsonParsing.ParseHelper;
import com.jinshu.weixinbook.utils.sns.*;
import com.jinshu.weixinbook.utils.*;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    TextView item_name;
    TextView item_log;
    TextView item_refresh;
    Button btn_history;
    Button btn_after_work;
    String siteUrl = "";
    Handler mHandler = new Handler();
    public List<UserModel> followList = new ArrayList<>();
    public List<UserModel> userModels = new ArrayList<>();
    public List<UserModel> userModelsWeixin = new ArrayList<>();
     DatabaseManager dm;
    private long time;
    private boolean isAsynchronous = false;//上传的时候为true，不上传的时候为false，
    private Runnable refreshRole = new Runnable() {
        public void run() {
            Log.e("refreshRole", "refreshRole");
            if (!isAsynchronous){
                getLatestUsers();
            }else{
                mHandler.postDelayed(refreshRole, 60 * 1000);
            }
        }
    };

    @Override
    protected int setContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {

        siteUrl = (String) SPUtils.get(baseActivity, Constant.siteUrl, "");
        item_name = (TextView) findViewById(R.id.item_name);
        item_log = (TextView) findViewById(R.id.item_log);
        item_log.setMovementMethod(new ScrollingMovementMethod());
        item_refresh = (TextView) findViewById(R.id.item_refresh);
        btn_history = (Button) findViewById(R.id.btn_history);
        btn_after_work = (Button) findViewById(R.id.btn_after_work);
        item_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHandler != null && refreshRole != null) {
                    mHandler.removeCallbacks(refreshRole);
                }
                getLatestUsers();
            }
        });
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump(HistoyActivity.class, null);
            }
        });
        btn_after_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appAttendancerun();
            }
        });
        dm = new DatabaseManager(this);
        getLatestUsers();
        this.findViewById(R.id.item_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "开始上传，请稍后", Toast.LENGTH_SHORT).show();
                item_log.setText("");
                followList.clear();
                userModelsWeixin.clear();
                isAsynchronous=true;
                showProgress("正在上传...");
                Date date = new Date();//创建现在的日期
                time = date.getTime();//获得毫秒数
                Observable.create(new ObservableOnSubscribe<UserModel>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<UserModel> e) throws Exception {
                        if(userModels.size()>0&&userModels.get(0).uploadTime<10){
                            dm.queryUserList(userModels, siteUrl);
                        }
                        for (int i = 0; i < userModels.size(); i++) {
                            e.onNext(userModels.get(i));
                        }
                        e.onComplete();
                    }
                }).subscribeOn(Schedulers.io())//把工作线程指定为了IO线程
                        .filter(new Predicate<UserModel>() {
                            @Override
                            public boolean test(@NonNull UserModel model) throws Exception {
                                if (time > (model.uploadTime + 3600000)) {
                                    if (StringUtils.isNotEmpty(model.weixinId)) {
                                        userModelsWeixin.add(model);
                                    } else {
                                        followList.add(model);
                                    }
                                    return false;
                                }
                                return true;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())//把回调线程指定为了UI线程
                        .subscribe(new Observer<UserModel>() {
                            @Override
                            public void onNext(UserModel model) {
                                item_log.setText(item_log.getText().toString() + "\n" + model.nickname + "刚刚上传过了");
                            }
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                            }
                            @Override
                            public void onError(Throwable t) {

                            }
                            @Override
                            public void onComplete() {
                                int size = followList.size() + userModelsWeixin.size();
                                if (size <= 0) {
                                    Toast.makeText(baseActivity, "已经全部上传过了", Toast.LENGTH_SHORT).show();
                                    isAsynchronous=false;
                                    stopProgress();
                                    return;
                                }

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadMomentList();
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                isAsynchronous=false;
                                                stopProgress();
                                                Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });



            }
        });

        String Folder = SnsHelper.findUserFolder("");
        String dbk = EncryptedDbHelper.getDBKey();
        Log.e("dbk================ ", dbk + ", Folder = " + Folder);
        if (!Utils.isLogin(MainActivity.this)) {
            //startActivity(new Intent(this, LoginActivity.class));
        }

    }


    private void getLatestUsers() {
//        String url= Constant.siteUrl+ Utils.HOST + "/public/index.php/admin/Wechat/check_60_mins_subscribe_time";
        showWaitDialog("刷新中");
        String url = siteUrl + "/admin/Wechat/check_60_mins_subscribe_time";

        StringRequest stringRequest = new StringRequest(Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("onResponse", "result = " + result);
                try {
                    JSONObject jsonObj = (JSONObject) JSON.parse(result);
                    if (jsonObj != null && jsonObj.containsKey("status")) {
                        userModels.clear();

                        JSONArray data = jsonObj.getJSONArray("data");

                        StringBuilder sb = new StringBuilder("");
                        for (int i = 0; i < data.size(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            UserModel model = new UserModel();
                            model.uid = obj.getString("uid");
                            model.nickname = obj.getString("nickname");
                            model.openid = obj.getString("openid");
                            model.headimgurl = obj.getString("headimgurl");
                            model.headurlsha = obj.getString("headurlsha");
                            //Log.e("onResponse", "model.headurlsha = " + model.headurlsha + ", model.nickname = " + model.nickname);
                            sb.append(model.nickname + ",");
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
                        item_name.setText(sb.toString());
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Observable.just("")
                        .map(new Function<String, String>() {
                            @Override
                            public String apply(@NonNull String s) throws Exception {
//                                dm.queryUserList(userModels, siteUrl);
//                                dm.queryRefreshList(userModels, siteUrl);
//                                Date date = new Date();//创建现在的日期
//                                long time = date.getTime();//获得毫秒数
//                                for (int i = 0; i < userModels.size(); i++) {
//                                    if (userModels.get(i).isRefresh) {
//                                        dm.insertRefresh(userModels.get(i), siteUrl, time + "");
//                                    } else {
//                                        dm.updateRefresh(userModels.get(i), siteUrl, time + "");
//                                    }
//                                }
                                return "";
                            }
                        }).subscribeOn(Schedulers.io())//把工作线程指定为了IO线程
                        .observeOn(AndroidSchedulers.mainThread())//把回调线程指定为了UI线程
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                hideWaitDialog();

                                mHandler.postDelayed(refreshRole, 60 * 1000);
                            }
                        });
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                Log.e("", "onErrorResponse = " + arg0.toString());
                hideWaitDialog();
            }

        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queues.add(stringRequest);
    }

    private void loadMomentList() {

        try {
            String string="";
            SnsHelper.loadAllUser(MainActivity.this, SnsHelper.findUserFolder(string) + "/");
            if (StringUtils.isNotEmpty(string)){
                item_log.setText("错误1"+  string  );
            }
        } catch (final Exception e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    String s=e.toString() ;
                    for (StackTraceElement se : e.getStackTrace()) {
                        s += "\tat " + se + "\r\n";
                    }
                    item_log.setText("错误2"+  s );
                }
            });
        }

        for (int i = 0; i < userModelsWeixin.size(); i++) {
            UserModel model = userModelsWeixin.get(i);
            readSnsInfo(model.weixinId, model);
        }
        if (followList.size() > 0) {
            String Folder = SnsHelper.findUserFolder("") + "/";
            for (int j=0;j<GlobeData.listRcontact.size();j++) {
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
                            readSnsInfo(username, model);
                            break;
                        }
                    }
                }
            }
        }
//        if (GlobeData.userNameMap.size() > 0) {
//            Set<String> setUsername = GlobeData.userNameMap.keySet();
//            String Folder = SnsHelper.findUserFolder("") + "/";
//            for (String username : setUsername) {
//                String userNameMap_nickname = GlobeData.userNameMap.get(username);
//                for (int i = 0; i < followList.size(); i++) {
//                    UserModel model = followList.get(i);
//                    if (model.nickname.equals(userNameMap_nickname)) {
//                        String avatar = SnsHelper.findAvatar(MainActivity.this, username, Folder);
//                        String sha1 = null;
//                        if (!TextUtils.isEmpty(avatar)) {//图片的处理
//                            avatar = avatar.replace("/96", "/0");
//                            avatar = avatar.replace("/0", "/46");
//                            sha1 = Utils.getFileSha1byte(avatar);
//                        }
//                        if (sha1 != null && sha1.equals(model.headurlsha)) {
//                            dm.insertMembers(username, model, siteUrl);
//                            readSnsInfo(username, model);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    ProgressDialog progressDialog;

    private void showProgress(String text) {
        progressDialog = new ProgressDialog(this);
//设置进度条风格，风格为圆形，旋转的
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
            ;
            progressDialog = null;
        }
    }

    @Override
    protected void onPause() {


        if (progressDialog != null && !this.isFinishing()) {
            progressDialog.dismiss();
        }
        if (mHandler != null && refreshRole != null) {
            mHandler.removeCallbacks(refreshRole);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHandler != null && refreshRole != null) {
            mHandler.removeCallbacks(refreshRole);
        }
        mHandler.postDelayed(refreshRole, 60 * 1000);
    }

    public void readSnsInfo(String alias, UserModel model) {
        Log.i("alias===", alias);
        alias = alias.trim();
        String Folder = SnsHelper.findUserFolder("") + "/";

        String paramString = Folder;//Utils.getExtDbPath("EnMicroMsg.db");//Utils.dbPathForWeChat(Folder) + "EnMicroMsg.db";
//        String userName = SnsHelper.findUserName(this, alias, paramString);
//
//
//        if (TextUtils.isEmpty(userName)) {
//            userName = alias;
//        }
        //周改
        String userName = alias;
        final ArrayList<MomentModel> momentList = SnsHelper.getSnsRecordsByUserName(this, paramString, userName, model);
        //Log.e("readSnsInfo", "userName = " + userName + ", alias = " + alias + ", momentList.size()  = " + momentList.size() );
        if (momentList.size() > 0) {
            final String name = userName;
//            if (!TextUtils.isEmpty(model.headurlsha)) {
//                 followUserMap.put(model.headurlsha, model);
//            } else if (!TextUtils.isEmpty(model.nickname)) {
//                 followUserMap.put(model.nickname, model);
//            }
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


    public String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e("", e.toString());
            return null;
        }
    }

    private void uploadMoments(final String data, final MomentModel model, final UserModel userModel, final int count) {

        //Log.e("uploadMoments",  "data: " + data);
        //Utils.HOST + "/public/index.php/admin/Wechat/uploadWechatContent"
        String url = siteUrl + "/admin/Wechat/uploadWechatContent";
        StringRequest stringRequest = new StringRequest(Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                try {
                    JSONObject jsonObj = (JSONObject) JSON.parse(result);
                    String text = item_log.getText().toString();
                    item_log.setText(text + "\n" + userModel.nickname + "上传成功" + count + "个朋友圈");
                    userModel.uploadNumEnd += count;
                    if (userModel.uploadNumEnd == userModel.uploadNumStame) {
                        appSendSms(userModel);
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
                byte[] htmlBodyBytes = arg0.networkResponse.data;
                Log.e("LOGIN-ERROR", new String(htmlBodyBytes), arg0);
                String text = item_log.getText().toString();
                text +="\n" + userModel.nickname + "上传失败" + count + "个朋友圈" ;
                String s=arg0.toString() ;
                for (StackTraceElement se : arg0.getStackTrace()) {
                    s += "\tat " + se + "\r\n";
                }
                item_log.setText(text + "\n" + s);
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                if (userModel != null) {
                    map.put("openid", userModel.openid);
                    map.put("uid", userModel.uid);
                    map.put("booktype", "1");
                } else {
                    map.put("openid", "openid");
                    map.put("uid", "uid");
                    map.put("booktype", "1");
                }

                map.put("data", data);
                //String json = map.toString().replace("=", ":");//JSON.toJSONString(map.toString());
                Log.e("", "getParams = " + map.toString());

                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queues.add(stringRequest);
    }

    private void appSendSms(final UserModel model) {
        String url = siteUrl + "/admin/Wechat/appSendSms";
        StringRequest stringRequest = new StringRequest(Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                Date date = new Date();//创建现在的日期
                long time = date.getTime();//获得毫秒数
                dm.updateTime(model, time + "");
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

    private void appAttendancerun() {
        if (!StringUtils.isNotEmpty(siteUrl)) {
            hideWaitDialog();
            showErrorDialog("请重新选择站点");
            return;
        }
        Log.i("Constant.siteUrl", Constant.siteUrl + Constant.appAttendancerun + "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, siteUrl + Constant.appAttendancerun, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("", "result = " + result);
                String message = ParseHelper.getString(result, "message");
                hideWaitDialog();
                Toast.makeText(baseActivity, message, Toast.LENGTH_SHORT).show();
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
                String uid = (String) SPUtils.get(baseActivity, Constant.uid, "");
                Map<String, String> map = new HashMap<String, String>();
                map.put("wx_userID", uid);


                return map;
            }
        };

        queues.add(stringRequest);
    }

//    private void uploadMoments1(final ArrayList<MomentModel> list, final MomentModel model, final String nickName, final int count) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                postMoments(list, model, nickName, count);
//            }
//        }).start();
//        ;
//    }

//    private void postMoments(final ArrayList<MomentModel> list, final MomentModel model, final String nickName, final int count) {
//
//        try {
////            String baseUrl = Utils.HOST + "/public/index.php/admin/Wechat/uploadWechatContent";
//            String baseUrl = siteUrl + " /admin/Wechat/uploadWechatContent";
//
//            //合成参数
//            StringBuilder tempParams = new StringBuilder();
//            int pos = 0;
//            UserModel uModel = null;
//            if (!TextUtils.isEmpty(model.sha1))
//                uModel =  followUserMap.get(model.sha1);
//            if (uModel == null && !TextUtils.isEmpty(model.nickName)) {
//                uModel =  followUserMap.get(model.nickName);
//            }
//
//            PostModel postModel = new PostModel();
//            postModel.data = list;
//            postModel.openid = uModel.openid;
//            postModel.uid = uModel.uid;
//            String json = JSON.toJSONString(postModel);
//
//            String params = tempParams.toString();
//            Log.e("uploadMoments", "params: " + json);
//            // 请求的参数转换为byte数组
//            byte[] postData = params.getBytes();
//            // 新建一个URL对象
//            URL url = new URL(baseUrl);
//            // 打开一个HttpURLConnection连接
//            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//            // 设置连接超时时间
//            urlConn.setConnectTimeout(5 * 1000);
//            //设置从主机读取数据超时
//            urlConn.setReadTimeout(5 * 1000);
//            // Post请求必须设置允许输出 默认false
//            urlConn.setDoOutput(true);
//            //设置请求允许输入 默认是true
//            urlConn.setDoInput(true);
//            // Post请求不能使用缓存
//            urlConn.setUseCaches(false);
//            // 设置为Post请求
//            urlConn.setRequestMethod("POST");
//            //设置本次连接是否自动处理重定向
//            urlConn.setInstanceFollowRedirects(true);
//            // 配置请求Content-Type
//            urlConn.setRequestProperty("Content-Type", "application/json");
//            // 开始连接
//            urlConn.connect();
//            // 发送请求参数
//            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
//            //dos.write(postData);
//            dos.writeBytes(json);
//            dos.flush();
//            dos.close();
//            // 判断请求是否成功
//            if (urlConn.getResponseCode() == 200) {
//                // 获取返回的数据
//                final String result = streamToString(urlConn.getInputStream());
//                Log.e("", "Post方式请求成功，result--->" + result);
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            JSONObject jsonObj = (JSONObject) JSON.parse(result);
//                            String text = item_log.getText().toString();
//                            item_log.setText(text + "\n" + nickName + "上传成功" + count + "个朋友圈");
//
//                        } catch (JSONException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            } else {
//                Log.e("", "Post方式请求失败 code = " + urlConn.getResponseCode());
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        String text = item_log.getText().toString();
//                        item_log.setText(text + "\n" + nickName + "上传失败" + count + "个朋友圈");
//                    }
//                });
//            }
//            // 关闭连接
//            urlConn.disconnect();
//        } catch (Exception e) {
//            Log.e("", e.toString());
//        }
//    }
//    private void jsonRequest(final String data, final MomentModel model, final String nickName, final int count) {
//
////        String url = Utils.HOST + "/public/index.php/admin/Wechat/uploadWechatContent";
//        String url = siteUrl + "/admin/Wechat/uploadWechatContent";
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST, url, new Response.Listener<org.json.JSONObject>() {
//            @Override
//            public void onResponse(org.json.JSONObject response) {
//                //responseText.setText(response.toString());
//                String text = item_log.getText().toString();
//                item_log.setText(text + "\n" + nickName + "上传成功" + count + "个朋友圈");
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //responseText.setText(error.getMessage());
//                Log.e("", "onErrorResponse = " + error.toString());
//                String text = item_log.getText().toString();
//                item_log.setText(text + "\n" + nickName + "上传失败" + count + "个朋友圈");
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                UserModel uModel = null;
//                if (!TextUtils.isEmpty(model.sha1))
//                    uModel =  followUserMap.get(model.sha1);
//                if (uModel == null && !TextUtils.isEmpty(model.nickName)) {
//                    uModel =  followUserMap.get(model.nickName);
//                }
//
//                Map<String, String> map = new HashMap<String, String>();
//                if (uModel != null) {
//                    map.put("openid", uModel.openid);
//                    map.put("uid", uModel.uid);
//                    map.put("booktype", "1");
//                } else {
//                    map.put("openid", "openid");
//                    map.put("uid", "uid");
//                    map.put("booktype", "1");
//                }
//
//                map.put("data", data);
//
//                return map;
//            }
//        };
//
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
//                30000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queues.add(jsonObjectRequest);
//    }

}