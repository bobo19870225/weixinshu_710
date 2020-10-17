package com.jinshu.weixinbook.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jinshu.weixinbook.utils.sns.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinshu on 2017/7/21.
 */

public class DatabaseManager {
    private static DatabaseHelper dbHelper;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 角色查询
     * <p>
     * 查询 openid  站点   返回 时间 微信号
     *
     * @return
     */
    public List<UserModel> queryUserList(List<UserModel> userModels, String url) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d("database", "正在查询.....");
        String sql = "";

        List<UserModel> users = new ArrayList<>();
        if (userModels == null || userModels.size() == 0) {
            return userModels;
        } else {
            String[] strs = new String[userModels.size() + 1];
            strs[0] = url;
            sql = "select * from " + DatabaseHelper.TABLE_members + " where url =  ?  AND   " + "openId " + " in(";
            for (int i = 0; userModels.size() > i; i++) {
                if (i != 0) {
                    sql = sql + ",";
                }
                sql = sql + " ? ";
                strs[i + 1] = userModels.get(i).openid;
            }
            sql = sql + ") ";
            Log.d("database", "sql=" + sql);
            Cursor cursor = db.rawQuery(sql, strs);

            while (cursor.moveToNext()) {
                String openID = cursor.getString(cursor.getColumnIndex("openId"));
                for (int i = 0; i < userModels.size(); i++) {
                    UserModel model = userModels.get(i);
                    if (model.openid.equals(openID)) {
                        model.weixinId = cursor.getString(cursor.getColumnIndex("weixinId"));
                        model.uploadTime = cursor.getLong(cursor.getColumnIndex("uploadTime"));
                        break;
                    }
                }
            }
            cursor.close();
            db.close();
            return userModels;
        }
    }

    /**
     * 添加人  微信号，openid 站点
     *
     * @param weixinhao
     * @param userModel
     * @param url
     * @return
     */
    public int insertMembers(String weixinhao, UserModel userModel, String url) {
        Log.d("database", "----insert----");
        if (queryUserList(userModel,url)) {
            update_weixinId(weixinhao, userModel, url);
            return 2;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            db.execSQL("insert into " + DatabaseHelper.TABLE_members + "(weixinId,openId,url,nickname," +
                            "headimgurl,headurlsha,uid,refreshTime )"
                            + " values(?, ?, ?, ?, ?, ?, ?, ? )",
                    new Object[]{weixinhao, userModel.openid, url, userModel.nickname
                            , userModel.headimgurl, userModel.headurlsha, userModel.uid, userModel.refreshTime});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("database", "__" + e.toString());
            return 0;
        } finally {
            db.endTransaction();
        }
        db.close();
        return 1;
    }
    /**
     * 开始上传要是有的人已经存了openid但是没有存微信id
     * 修改微信id 根据网址和openid 改变时间
     *
     * @return
     */
    public int update_weixinId(String weixinhao, UserModel userModel, String url) {
        Log.d("database", "----insert----");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("UPDATE  " + DatabaseHelper.TABLE_members + "    SET  weixinId = ?  WHERE url=? AND openId=?",
                    new Object[]{weixinhao, url, userModel.openid});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("database", "__" + e.toString());
            return 0;
        } finally {
            db.endTransaction();
        }
        db.close();
        return 1;
    }
    /**
     * 上传好了
     * 修改上传时间 根据微信号和openid 改变时间
     *
     * @param userModel
     * @param uploadTime
     * @return
     */
    public int updateTime(UserModel userModel, String uploadTime) {
        Log.d("database", "----insert----");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("UPDATE  " + DatabaseHelper.TABLE_members + "    SET  uploadTime = ?  WHERE weixinId=? AND openId=?",
                    new Object[]{uploadTime, userModel.weixinId, userModel.openid});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("database", "__" + e.toString());
            return 0;
        } finally {
            db.endTransaction();
        }
        db.close();
        return 1;
    }

    /**
     * 刷新的时间修改名字的能等信息，为了防止有人改名
     *
     * @param userModel
     * @return
     */
    public int updateMembers(UserModel userModel, String url) {
        Log.d("database", "----insert----");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("UPDATE  " + DatabaseHelper.TABLE_members +
                            "  SET  refreshTime = ? ,nickname=?,headimgurl=?,headurlsha=? WHERE url=? AND openId=?",
                    new Object[]{userModel.refreshTime, userModel.nickname, userModel.headimgurl, userModel.headurlsha, url, userModel.openid});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("database", "__" + e.toString());
            return 0;
        } finally {
            db.endTransaction();
        }
        db.close();
        return 1;
    }

    /**
     * 根据站点id，查询所有人
     *
     * @return
     */
    public List<UserModel> queryRefreshList(String url) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d("database", "正在查询.....");
        String sql = "";

        List<UserModel> users = new ArrayList<>();
        String[] strs = new String[1];
        strs[0] = url;
        sql = "select * from " + DatabaseHelper.TABLE_members + " where url =  ?  ";
        Log.d("database", "sql=" + sql);
        Cursor cursor = db.rawQuery(sql, strs);

        while (cursor.moveToNext()) {
            UserModel model = new UserModel();
            model.openid = cursor.getString(cursor.getColumnIndex("openId"));
            model.nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            model.headimgurl = cursor.getString(cursor.getColumnIndex("headimgurl"));
            model.headurlsha = cursor.getString(cursor.getColumnIndex("headurlsha"));
            model.weixinId = cursor.getString(cursor.getColumnIndex("weixinId"));
            model.refreshTime = cursor.getLong(cursor.getColumnIndex("refreshTime"));
            model.uid = cursor.getString(cursor.getColumnIndex("uid"));
            users.add(model);
        }
        cursor.close();
        db.close();
        return users;
    }

    /**
     * 角色查询
     * <p>
     * 查询 openid  站点   返回 时间 微信号
     *
     * @return
     */
    public boolean queryUserList(UserModel userModel, String url) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d("database", "正在查询.....");
        String sql = "";
        boolean istrue=false;
        String[] strs = new String[2];
        strs[0] = url;
        strs[1] = userModel.openid;
        sql = "select * from " + DatabaseHelper.TABLE_members + " where url =  ?  AND    openId  =?";
        Log.d("database", "sql=" + sql);
        Cursor cursor = db.rawQuery(sql, strs);

        while (cursor.moveToNext()) {
            istrue=true;
        }
        cursor.close();
        db.close();
        return istrue;

    }
    /**
     * 角色查询
     * <p>
     * 查询 微信号  站点   返回 完整数据
     *
     * @return
     */
    public void queryUser (UserModel userModels, String url) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d("database", "正在查询.....");
        String sql = "";
        boolean istrue=false;
        String[] strs = new String[2];
        strs[0] = url;
        strs[1] = userModels.weixinId;
        sql = "select * from " + DatabaseHelper.TABLE_members + " where url =  ?  AND    weixinId  =?";
        Log.d("database", "sql=" + sql);
        Cursor cursor = db.rawQuery(sql, strs);

        while (cursor.moveToNext()) {
            userModels.openid = cursor.getString(cursor.getColumnIndex("openId"));
            userModels.nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            userModels.headimgurl = cursor.getString(cursor.getColumnIndex("headimgurl"));
            userModels.headurlsha = cursor.getString(cursor.getColumnIndex("headurlsha"));
            userModels.weixinId = cursor.getString(cursor.getColumnIndex("weixinId"));
            userModels.refreshTime = cursor.getLong(cursor.getColumnIndex("refreshTime"));
            userModels.uid = cursor.getString(cursor.getColumnIndex("uid"));
        }
        cursor.close();
        db.close();

    }

        /**
     * 刷新时添加人   openid 站点。。。。
     *
     * @param userModel
     * @param url
     * @return
     */
    public int insertRefresh(UserModel userModel, String url ) {
        Log.d("database", "----insert----");
        if(queryUserList(userModel,url)){
            return 2;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("insert into " + DatabaseHelper.TABLE_members + "( openId,url,nickname,headimgurl,headurlsha,refreshTime,uid)"
                            + " values(?, ?, ? ,?,?,?,?)",
                    new Object[]{userModel.openid, url, userModel.nickname, userModel.headimgurl, userModel.headurlsha,userModel.refreshTime,userModel.uid});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("database", "__" + e.toString());
            return 0;
        } finally {
            db.endTransaction();
        }
        db.close();
        return 1;
    }
//    /**
//     * 角色查询
//     * <p>
//     * 查询 openid  站点   返回 时间 微信号
//     *
//     * @return
//     */
//    public List<UserModel> queryRefreshList(List<UserModel> userModels, String url) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Log.d("database", "正在查询.....");
//        String sql = "";
//
//        List<UserModel> users = new ArrayList<>();
//        if (userModels == null || userModels.size() == 0) {
//            return userModels;
//        } else {
//            String[] strs = new String[userModels.size() + 1];
//            strs[0] = url;
//            if(userModels.size()>1){
//                sql = "select openId from " + DatabaseHelper.TABLE_refresh + " where url =  ?  AND   " + "openId " + " in(";
//                for (int i = 0; userModels.size() > i; i++) {
//                    if (i != 0) {
//                        sql = sql + ",";
//                    }
//                    sql = sql + " ? ";
//                    strs[i + 1] = userModels.get(i).openid;
//                }
//                sql = sql + ") ";
//                Log.d("database", "sql=" + sql);
//            }else{
//                sql = "select openId from " + DatabaseHelper.TABLE_refresh + " where url =  ?  AND openId = ?";
//                strs[ 1] = userModels.get(0).openid;
//                Log.d("database", "sql=" + sql);
//            }
//            Cursor cursor = db.rawQuery(sql, strs);
//
//            while (cursor.moveToNext()) {
//                String openID = cursor.getString(cursor.getColumnIndex("openId"));
//                for (int i = 0; i < userModels.size(); i++) {
//                    UserModel model = userModels.get(i);
//                    if (model.openid.equals(openID)) {
//                        model.isRefresh = false;
//                        break;
//                    }
//                }
//            }
//            cursor.close();
//            db.close();
//            return userModels;
//        }
//    }
//


}
