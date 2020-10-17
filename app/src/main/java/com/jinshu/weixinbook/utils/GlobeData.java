package com.jinshu.weixinbook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jinshu.weixinbook.utils.sns.RcontactModel;
import com.jinshu.weixinbook.utils.sns.UserModel;

/**
 * Created by laidayuan on 2017/5/15.
 */

public class GlobeData {

//    public static ConcurrentHashMap<String, String> userNameMap = new ConcurrentHashMap<String, String>();//以唯一码的为键，名字为值
    public static List<RcontactModel> listRcontact=new ArrayList<>();//这个是朋友列表
//    public static List< UserModel> followList = new ArrayList<>();
//    public static List<UserModel> userModels=new ArrayList<>();
//    public static List<UserModel> userModelsWeixin=new ArrayList<>();
//
//
//    public static volatile boolean isFetching = false;
//
//    public static void saveUserName(Context context) {
//
//        String json = JSON.toJSONString(userNameMap.keys());
//        Log.e("", "json = " + json);
//        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
//        Editor editor = sp.edit();
//        editor.putString("json",json);
//        editor.commit();
//
//    }
//
//    public static void load(Context context) {
//        userNameMap.clear();
//        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
//        String json = sp.getString("json", "");
//        Log.e("", "json = " + json);
//        JSONArray jArray = (JSONArray)JSONArray.parse(json);
//        if (jArray != null) {
//            for (int i = 0; i < jArray.size(); i++) {
//                String text = (String) jArray.get(i);
//                userNameMap.put(text, text);
//            }
//        }
//    }
}
