package com.jinshu.weixinbook.mvp.Host;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.db.DatabaseManager;
import com.jinshu.weixinbook.mvp.BasePresenter;
import com.jinshu.weixinbook.mvp.IBaseModel;
import com.jinshu.weixinbook.utils.GlobeData;
import com.jinshu.weixinbook.utils.SPUtils;
import com.jinshu.weixinbook.utils.StringUtils;
import com.jinshu.weixinbook.utils.sns.SnsHelper;
import com.jinshu.weixinbook.utils.sns.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinshu on 2017/9/20.
 */

public class SearchPresenter extends BasePresenter implements ISearchContract.Presenter {
    ISearchContract.View mView;
    HostModel mHostModel;
    DatabaseManager dm;
    String siteUrl = "";
    public SearchPresenter(Context baseActivity, ISearchContract.View mView, RequestQueue queue) {
        this.baseActivity = baseActivity;
        this.mView = mView;
        mHostModel = new HostModel(queue);
        dm = new DatabaseManager(baseActivity);
        siteUrl = (String) SPUtils.get(baseActivity, Constant.siteUrl, "");
    }

    @Override
    public void start() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void search(String str) {
        List<UserModel> list=new ArrayList<>();
        if(!StringUtils.isNotEmpty(str)){
            mView.setMyList(list);
            return ;
        }
        mView.showWaitDialogImpl("查询中");
        if( GlobeData.listRcontact==null|| GlobeData.listRcontact.size()<=0){
            try {
                String string = "";
                SnsHelper.loadAllUser(baseActivity, SnsHelper.findUserFolder(string) + "/");
                if (StringUtils.isNotEmpty(string)) {
                    mView.showErrorDialogImpl("错误1" + string);
                }
            } catch (final Exception e) {
                String s = e.toString();
                for (StackTraceElement se : e.getStackTrace()) {
                    s += "\tat " + se + "\r\n";
                }
                mView.showErrorDialogImpl("错误2" + s);
            }
        }

        for (int i=0;i< GlobeData.listRcontact.size();i++){
            if(GlobeData.listRcontact.get(i).conRemark.indexOf(str)>=0){
                UserModel model=new UserModel();
                model.nickname=GlobeData.listRcontact.get(i).nickname;
                model.conRemark=GlobeData.listRcontact.get(i).conRemark;
                model.weixinId=GlobeData.listRcontact.get(i).username;
                list.add(model);
            }else if(GlobeData.listRcontact.get(i).nickname.indexOf(str)>=0){
                UserModel model=new UserModel();
                model.nickname=GlobeData.listRcontact.get(i).nickname;
                model.conRemark=GlobeData.listRcontact.get(i).conRemark;
                model.weixinId=GlobeData.listRcontact.get(i).username;
                list.add(model);
            }
        }
        mView.setMyList(list);
        mView.hideWaitDialogImpl();
    }

    @Override
    public void onItemClick(final UserModel um) {
        dm.queryUser(um,siteUrl);
        if(StringUtils.isNotEmpty(um.openid)){
            mView.jump(um);
            return;
        }
        mHostModel.setSiteUrl(siteUrl);
        mView.showWaitDialogImpl("注册中");
        mHostModel.otherMemberRegister(um, new IBaseModel.RequestListening() {
            @Override
            public void onResponse(Object message) {
                mView.jump(um);
            }

            @Override
            public void onErrorResponse(VolleyError arg0) {
                mView.hideWaitDialogImpl();
            }
        });
    }
    public void finish(){
        baseActivity=null;
        mView=null;
    }
}
