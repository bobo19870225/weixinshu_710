package com.jinshu.weixinbook.mvp.Host;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.db.DatabaseManager;
import com.jinshu.weixinbook.mvp.BasePresenter;
import com.jinshu.weixinbook.mvp.IBaseModel;
import com.jinshu.weixinbook.utils.GlobeData;
import com.jinshu.weixinbook.utils.SPUtils;
import com.jinshu.weixinbook.utils.StringUtils;
import com.jinshu.weixinbook.utils.Utils;
import com.jinshu.weixinbook.utils.sns.MomentModel;
import com.jinshu.weixinbook.utils.sns.SnsHelper;
import com.jinshu.weixinbook.utils.sns.UserModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

/**
 * Created by jinshu on 2017/8/22.
 */

public class HostPresenter extends BasePresenter implements IHostContract.Presenter {
    String siteUrl = "";
    Handler mHandler = new Handler();
    public List<UserModel> followList = new ArrayList<>();
    public List<UserModel> userModels = new ArrayList<>();
    public List<UserModel> userModelsWeixin = new ArrayList<>();
    DatabaseManager dm;
    private long time;
    private boolean isAsynchronous = false;//上传的时候为true，不上传的时候为false，
    IHostContract.View mView;
    IBaseModel mHostModel;
    public static final  long RefreshInterval=3*60*1000;//刷新时间间隔
    public static final  long UploadInterval=60*60*1000;//上传时间间隔
    public HostPresenter(Context baseActivity, IHostContract.View mView, RequestQueue queue) {
        this.baseActivity = baseActivity;
        this.mView = mView;
        mHostModel = new HostModel(queue);
    }
    private Runnable refreshRole = new Runnable() {
        public void run() {
            Log.e("refreshRole", "refreshRole");
            if (!isAsynchronous) {
                getLatestUsers();
            } else {
                mHandler.postDelayed(refreshRole, RefreshInterval);
            }
        }
    };

    @Override
    public void start() {
        siteUrl = (String) SPUtils.get(baseActivity, Constant.siteUrl, "");
        mHostModel.setSiteUrl(siteUrl);
        dm = new DatabaseManager(baseActivity);
        getLatestUsers();
    }
    /***
     * 刷新人
     */
    private void getLatestUsers() {
        mView.showWaitDialogImpl("刷新中");
        mHostModel.getLatestUsers(new IBaseModel.RequestListening() {
            @Override
            public void onResponse(Object message) {
                userModels.clear();
                userModels.addAll((List<UserModel>) message);
                Observable.just("")
                        .map(new Function<String, String>() {
                            @Override
                            public String apply(@NonNull String s) throws Exception {
                                dm.queryUserList(userModels, siteUrl);
                                for(int i=0;i<userModels.size();i++){
                                    UserModel model= userModels.get(i);
                                    if(model.uploadTime>=model.refreshTime){
                                        userModels.remove(i);
                                        i--;
                                    }
                                }
                                return "";
                            }
                        }).subscribeOn(Schedulers.io())//把工作线程指定为了IO线程
                        .observeOn(AndroidSchedulers.mainThread())//把回调线程指定为了UI线程
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                mView.hideWaitDialogImpl();
                                String str="";
                                for (int i=0;i<userModels.size();i++){
                                    if(i!=0)  str+=",";
                                    str+=userModels.get(i).nickname;
                                }
                                mView.nameSetText(str.toString());
                                mHandler.postDelayed(refreshRole, RefreshInterval);
                            }
                        });
            }

            @Override
            public void onErrorResponse(VolleyError arg0) {
                Log.e("", "onErrorResponse = " + arg0.toString());
                mView.hideWaitDialogImpl();
            }
        });
    }
    /**
     * 准备上传，过滤一下
     */
    @Override
    public void upload() {
        mView.logSetText("");
        followList.clear();
        userModelsWeixin.clear();
        isAsynchronous = true;
        mView.showWaitDialogImpl("正在上传...");
        Date date = new Date();//创建现在的日期
        time = date.getTime()/1000;//获得毫秒数
        Observable.create(new ObservableOnSubscribe<UserModel>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<UserModel> e) throws Exception {
                if (userModels.size() > 0 && userModels.get(0).uploadTime < 10) {
                    dm.queryUserList(userModels, siteUrl);
                }
                for(int i=0;i<userModels.size();i++){
                    UserModel model= userModels.get(i);
                    if(model.uploadTime>=model.refreshTime){
                        userModels.remove(i);
                        i--;
                    }
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

//                        if (time > (model.uploadTime + UploadInterval)) {
                            if (StringUtils.isNotEmpty(model.weixinId)) {
                                userModelsWeixin.add(model);
                            } else {
                                followList.add(model);
                                dm.insertRefresh(model,siteUrl);
                            }
                            return false;
//                        }
//                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//把回调线程指定为了UI线程
                .subscribe(new Observer<UserModel>() {
                    @Override
                    public void onNext(UserModel model) {
                        mView.logSetText(mView.logGetText() + "\n" + model.nickname + "刚刚上传过了");
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                    @Override
                    public void onComplete() {
                        String str="";
                        for (int i=0;i<userModels.size();i++){
                            if(i!=0)  str+=",";
                            str+=userModels.get(i).nickname;
                        }
                        mView.nameSetText(str.toString());
                        int size = followList.size() + userModelsWeixin.size();
                        if (size <= 0) {
                            Toast.makeText(baseActivity, "已经全部上传过了", Toast.LENGTH_SHORT).show();
                            isAsynchronous = false;
                            mView.hideWaitDialogImpl();
                            return;
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                loadMomentList();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        isAsynchronous = false;
                                        mView.hideWaitDialogImpl();
                                        Toast.makeText(baseActivity, "开始上传", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).start();
                    }
                });
    }

    /**
     * 准备上传
     */
    private void loadMomentList() {
        try {
            String string = "";
            SnsHelper.loadAllUser(baseActivity, SnsHelper.findUserFolder(string) + "/");
            if (StringUtils.isNotEmpty(string)) {
                mView.logSetText("错误1" + string);
            }
        } catch (final Exception e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    String s = e.toString();
                    for (StackTraceElement se : e.getStackTrace()) {
                        s += "\tat " + se + "\r\n";
                    }
                    mView.logSetText("错误2" + s);
                }
            });
        }
        for (int i = 0; i < userModelsWeixin.size(); i++) {
            UserModel model = userModelsWeixin.get(i);
            dm.updateMembers( model, siteUrl);
            readSnsInfo(model.weixinId, model);
        }
        if (followList.size() > 0) {
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
    }

    /**
     * 数据处理
     * @param alias
     * @param model
     */
    public void readSnsInfo(String alias, UserModel model) {
        Log.i("alias===", alias);
        alias = alias.trim();
        String Folder = SnsHelper.findUserFolder("") + "/";

        String paramString = Folder;
        //周改
        String userName = alias;
        final ArrayList<MomentModel> momentList = SnsHelper.getSnsRecordsByUserName(baseActivity, paramString, userName, model);
        if (momentList.size() > 0) {
            final String name = userName;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    String text = mView.logGetText();
                    mView.logSetText(text + "\n" + name + "开始上传" + momentList.size() + "个朋友圈");
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

                uploadMoments(JSON.toJSONString(list), model, 20);
            }

            int nLeft = momentList.size() % 20;
            if (nLeft > 0) {
                ArrayList<MomentModel> list = new ArrayList<MomentModel>();
                list.addAll(momentList.subList(nCount * 20, momentList.size()));
                uploadMoments(JSON.toJSONString(list), model, list.size());
            }
        } else {
            if (momentList.size() > 0) {
                uploadMoments(JSON.toJSONString(momentList), model, momentList.size());
            }
        }

    }

    /**
     * 上传
     *
     * @param data
     * @param userModel
     * @param count
     */
    private void uploadMoments(final String data, final UserModel userModel, final int count) {
        mHostModel.uploadMoments(data, userModel, new IBaseModel.RequestListening() {
            @Override
            public void onResponse(Object message) {
                String text = mView.logGetText();
                mView.logSetText(text + "\n" + userModel.nickname + "上传成功" + count + "个朋友圈");
                userModel.uploadNumEnd += count;
                if (userModel.uploadNumEnd == userModel.uploadNumStame) {
                    appSendSms(userModel);
                }
            }

            @Override
            public void onErrorResponse(VolleyError arg0) {
                Log.e("", "onErrorResponse = " + arg0.toString(), arg0);
                byte[] htmlBodyBytes = arg0.networkResponse.data;
                Log.e("LOGIN-ERROR", new String(htmlBodyBytes), arg0);
                String text = mView.logGetText();
                text += "\n" + userModel.nickname + "上传失败" + count + "个朋友圈";
                String s = arg0.toString();
                for (StackTraceElement se : arg0.getStackTrace()) {
                    s += "\tat " + se + "\r\n";
                }
                mView.logSetText(text + "\n" + s);
            }
        });
    }

    /**
     * 上传结束，通知
     * @param model
     */
    private void appSendSms(final UserModel model) {
        mHostModel.appSendSms(model, new IBaseModel.RequestListening() {
            @Override
            public void onResponse(Object message) {
                Date date = new Date();//创建现在的日期
                long time = date.getTime()/1000;//获得毫秒数
                model.uploadTime=time;
                dm.updateTime(model, time + "");
            }

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
    }

    /**
     * 点击刷新用户，先把异步去掉，然后直接开始刷新
     */
    public void refresh() {
        if (mHandler != null && refreshRole != null) {
            mHandler.removeCallbacks(refreshRole);
        }
        getLatestUsers();
    }

    @Override
    public void onPause() {
        if (mHandler != null && refreshRole != null) {
            mHandler.removeCallbacks(refreshRole);
        }
    }

    @Override
    public void onResume() {
        if (mHandler != null && refreshRole != null) {
            mHandler.removeCallbacks(refreshRole);
        }
        mHandler.postDelayed(refreshRole, RefreshInterval);
    }

    /**
     * 上下班
     */
    @Override
    public void appAttendancerun() {
        if (!StringUtils.isNotEmpty(siteUrl)) {
            mView.hideWaitDialogImpl();
            mView.showErrorDialogImpl("请重新选择站点");
            return;
        }
        String uid = (String) SPUtils.get(baseActivity, Constant.uid, "");
        Map<String, String> map = new HashMap<String, String>();
        map.put("wx_userID", uid);
        mHostModel.appAttendancerun(map, new IBaseModel.RequestListening() {
            @Override
            public void onResponse(Object message) {
                mView.hideWaitDialogImpl();
                Toast.makeText(baseActivity, (String) message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorResponse(VolleyError arg0) {
                mView.hideWaitDialogImpl();
                mView.showErrorDialogImpl("网络错误，请重试");
            }
        });
    }
    public void finish(){
        baseActivity=null;
        mView=null;
    }
}
