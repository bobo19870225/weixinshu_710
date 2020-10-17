package com.jinshu.weixinbook.utils.sns;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by laidayuan on 2017/5/16.
 */

public class UserModel  implements Parcelable {
    public String uid;//uid
    public String nickname;//名字
    public String  conRemark;//备注
    public String openid;//openid
    public String headimgurl;//头像地址
    public String headurlsha;//头像加密值
    public String weixinId;//微信号
    public long refreshTime;//刷新时间
    public long uploadTime=0;//上传时间
    public int  uploadNumStame=0;
    public int  uploadNumEnd=0;
    public boolean isRefresh=true;//是否需要刷新了

    public UserModel(){

    }
    protected UserModel(Parcel in) {
        uid = in.readString();
        nickname = in.readString();
        openid = in.readString();
        headimgurl = in.readString();
        headurlsha = in.readString();
        weixinId = in.readString();
        refreshTime = in.readLong();
        uploadTime = in.readLong();
        uploadNumStame = in.readInt();
        uploadNumEnd = in.readInt();
        isRefresh =false;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(nickname);
        dest.writeString(openid);
        dest.writeString(headimgurl);
        dest.writeString(headurlsha);
        dest.writeString(weixinId);
        dest.writeLong(refreshTime);
        dest.writeLong(uploadTime);
        dest.writeInt(uploadNumStame);
        dest.writeInt(uploadNumEnd);

    }
}
