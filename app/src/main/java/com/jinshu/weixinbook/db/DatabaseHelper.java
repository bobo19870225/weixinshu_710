package com.jinshu.weixinbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jinshu on 2017/7/21.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    //类没有实例化,是不能用作父类构造器的参数,必须声明为静态
    private static final String DATABASE_NAME = "weixinshu_zjy.db";//库名
    private static final int version = 1; //数据库版本
    public static final String TABLE_members = "members";//上传的表

    //    public static final String TABLE_refresh = "refresh";//刷新的表
    public DatabaseHelper(Context context) {
        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//        db.execSQL("CREATE TABLE IF NOT EXISTS person (personid integer primary key autoincrement, name varchar(20), age INTEGER)");
        {
            StringBuffer sql = new StringBuffer();
            sql.append("CREATE TABLE [" + TABLE_members + "] (");
            sql.append("memberId integer primary key autoincrement,");       //主键id
            sql.append("weixinId TEXT,");                                    //微信号
            sql.append("openId TEXT,");                                      //openid
            sql.append("url TEXT,");                                         //站点
            sql.append("uploadTime TEXT, ");                                  //上传时间，我上传的时间，
            sql.append("nickname TEXT,");                                    //名字
            sql.append("headimgurl TEXT,");                                  //头像
            sql.append("headurlsha TEXT,");                                  //头像加密值
            sql.append("uid TEXT,");                                         //uid
            sql.append("refreshTime TEXT ");                                  //刷新时间，后台返回给我的时间
            sql.append(")");
            db.execSQL(sql.toString());
        }
//        {
//            StringBuffer sql_refresh = new StringBuffer();
//            sql_refresh.append("CREATE TABLE [" + TABLE_refresh + "]  (");
//            sql_refresh.append(" Id integer primary key autoincrement,");       //主键id
//            sql_refresh.append("openId TEXT,");                                 //openid
//            sql_refresh.append("url TEXT  ,");                                  //站点
//            sql_refresh.append("nickname TEXT,");                                    //名字
//            sql_refresh.append("headimgurl TEXT,");                                    //头像
//            sql_refresh.append("headurlsha TEXT,");                                    //头像加密值
//            sql_refresh.append("uid TEXT,");                                    //头像加密值
//            sql_refresh.append("refreshTime TEXT ");                             //刷新时间
//            sql_refresh.append(")");
//            db.execSQL(sql_refresh.toString());
//        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}