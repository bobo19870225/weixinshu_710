package com.jinshu.weixinbook.mvp;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jinshu.weixinbook.utils.sns.UserModel;

import java.util.Map;

/**
 * Created by jinshu on 2017/8/24.
 */

public interface IBaseModel {
    interface RequestListening   {
        void onResponse(Object message);
        void onErrorResponse(VolleyError arg0);
    }
    void setSiteUrl(String siteUrl);

    void appAttendancerun(Map<String, String> map, IBaseModel.RequestListening requestListening); //上下班

    void getLatestUsers(IBaseModel.RequestListening requestListening); //刷新新关注的

    void uploadMoments(final String data, final UserModel userModel, final IBaseModel.RequestListening requestListening);//上传

    void appSendSms(final UserModel model, final IBaseModel.RequestListening requestListening);//通知

    public void otherMemberRegister(final  UserModel um, final IBaseModel.RequestListening requestListening);//注册
}
