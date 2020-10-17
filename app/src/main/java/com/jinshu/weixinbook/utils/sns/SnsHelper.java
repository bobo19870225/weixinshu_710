package com.jinshu.weixinbook.utils.sns;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jinshu.weixinbook.utils.EncryptedDbHelper;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import com.jinshu.weixinbook.utils.GlobeData;
import com.jinshu.weixinbook.utils.Utils;

//dbk 362694d
public class SnsHelper {
    public static String encodeBase64(byte[] paramArrayOfByte) {
        return new String(Base64.encodeBase64(paramArrayOfByte));
    }

    //获取数据库文件夹名称
    public static String findUserFolder(String exception)  {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String str1 = null;
        Process localProcess;
        try {
            localProcess = Runtime.getRuntime().exec("su");
            BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
            localDataOutputStream.writeBytes("ls -l /data/data/com.tencent.mm/MicroMsg/ | grep ^d\n");
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            Object localObject = null;
            while (true) {
                String str2 = localBufferedReader.readLine();
                if (str2 == null)
                    break;
                String[] arrayOfString = str2.split(" +");
                if ((arrayOfString.length == 6) && (arrayOfString[5].length() == 32)) {
                    Date localDate = localSimpleDateFormat.parse(arrayOfString[3] + " " + arrayOfString[4]);
                    if ((str1 == null) || (localDate.after((Date) localObject))) {
                        str1 = arrayOfString[5];
                        localObject = localDate;
                        // Log.e("TEMP", "latestUserFolder:" + str1);
                    }
                }
                if ((arrayOfString.length == 8) && (arrayOfString[7].length() == 32)) {
                    Date localDate = localSimpleDateFormat.parse(arrayOfString[5] + " " + arrayOfString[6]);
                    if ((str1 == null) || (localDate.after((Date) localObject))) {
                        str1 = arrayOfString[7];
                        localObject = localDate;
                        // Log.e("TEMP", "latestUserFolder:" + str1);
                    }
                }
            }
        } catch (Exception localException) {
            localException.printStackTrace();
            exception+=localException.toString();
            exception+="\n"+localException.getStackTrace().toString();
            return str1;
        }
        try {
            localProcess.waitFor();
        } catch (InterruptedException e) {
            exception+=e.toString();
            exception+="\n"+e.getStackTrace().toString();
            e.printStackTrace();
        }
        return str1;
    }

    /**
     * @param paramContext
     * @param //paramString (文件夹名称)
     * @return 获取EditorWxId
     */
    //
    public static String findEditorWxId(Context paramContext, String dir) {
        String str2 = null;
        if (dir.contains(".db")) {
            net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase;
            net.sqlcipher.Cursor localCursor;
            localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(dir);
            localCursor = localSQLiteDatabase.rawQuery("select value from userinfo where id = 2;", null);
            while (localCursor.moveToNext())
                str2 = localCursor.getString(0);
            localCursor.close();
            localSQLiteDatabase.close();
        } else {
            String str1 = "/data/data/com.tencent.mm/MicroMsg/" + dir + "EnMicroMsg.db";
            try {
                net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase;
                net.sqlcipher.Cursor localCursor;
                Process localProcess = Runtime.getRuntime().exec("su");
                DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
                localDataOutputStream.writeBytes("chmod 777 /data/data/com.tencent.mm\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/" + dir + "\n");
                localDataOutputStream.writeBytes("chmod 666 " + str1 + "\n");
                localDataOutputStream.writeBytes("exit\n");
                localDataOutputStream.flush();
                localProcess.waitFor();
                localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(str1);
                localCursor = localSQLiteDatabase.rawQuery("select value from userinfo where id = 2;", null);
                while (localCursor.moveToNext())
                    str2 = localCursor.getString(0);
                localCursor.close();
                localSQLiteDatabase.close();
            } catch (Exception localException) {
                localException.printStackTrace();
                if (str2 == null)
                    str2 = "";
            }
        }
        return str2;
    }


    public static String findUserName(Context paramContext, String alias, String path) {
        String str2 = "";
        if (path.contains(".db")) {
            net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(path);
            net.sqlcipher.Cursor localCursor = localSQLiteDatabase.rawQuery("select username from rcontact where alias = '" + alias + "';", null);

            if (localCursor.moveToNext()) {
                str2 = localCursor.getString(0);
                Log.i("findUserName", "username===" + str2 + "++++++alias======" + alias);
            }

            localCursor.close();
            localSQLiteDatabase.close();
        } else {
            String str1 = "/data/data/com.tencent.mm/MicroMsg/" + path + "EnMicroMsg.db";

            try {
                Process localProcess = Runtime.getRuntime().exec("su");
                DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/" + path + "\n");
                localDataOutputStream.writeBytes("chmod 666 " + str1 + "\n");
                localDataOutputStream.writeBytes("exit\n");
                localDataOutputStream.flush();
                localProcess.waitFor();
                net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(str1);
                net.sqlcipher.Cursor localCursor = localSQLiteDatabase.rawQuery("select username from rcontact where alias = '" + alias + "';", null);

                if (localCursor.moveToNext()) {
                    str2 = localCursor.getString(0);
                    Log.i("findUserName", "username===" + str2 + "++++++alias======" + alias);
                }
                localCursor.close();
                localSQLiteDatabase.close();

            } catch (Exception e) {
                Log.i("findUserName", "e===" + e.toString() + "++++++alias======" + alias);
            }
        }

        return str2;
    }

    /**
     * @param paramContext
     * @param ///paramString1 （username用户名）
     * @param /paramString2   （文件夹名称）
     * @return 返回用户昵称
     */
    public static String findNickName(Context paramContext, String userName, String dir) {
        String str2 = "";
        if (dir.contains(".db")) {
            net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(dir);
            net.sqlcipher.Cursor localCursor = localSQLiteDatabase.rawQuery("select nickname from rcontact where username = '" + userName + "';", null);
            if (localCursor.moveToNext()) {
                str2 = localCursor.getString(0);
                Log.e("nickname", str2);
            }
            localCursor.close();
            localSQLiteDatabase.close();
        } else {
            String str1 = "/data/data/com.tencent.mm/MicroMsg/" + dir + "EnMicroMsg.db";

            try {
                Process localProcess = Runtime.getRuntime().exec("su");
                DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/" + dir + "\n");
                localDataOutputStream.writeBytes("chmod 666 " + str1 + "\n");
                localDataOutputStream.writeBytes("exit\n");
                localDataOutputStream.flush();
                localProcess.waitFor();
                net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(str1);
                net.sqlcipher.Cursor localCursor = localSQLiteDatabase.rawQuery("select nickname from rcontact where username = '" + userName + "';", null);
                if (localCursor.moveToNext()) {
                    str2 = localCursor.getString(0);
                    Log.e("nickname", str2);
                }
                localCursor.close();
                localSQLiteDatabase.close();

            } catch (Exception e) {
                Log.e("nickname", e.toString());
            }
        }

        return str2;
    }


    /**
     * @param paramContext
     * @param userName     （username用户名）
     * @param path         （文件夹名称）
     * @return 返回用户头像
     */
    public static String findAvatar(Context paramContext, String userName, String path) {
        String str2 = "";
        if (path.contains(".db")) {
            net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(path);
            net.sqlcipher.Cursor localCursor = localSQLiteDatabase.rawQuery("select reserved1 ,reserved2 from img_flag where username = '" + userName + "';", null);
            if (localCursor.moveToNext()) {
                str2 = localCursor.getString(0);
                if ("".equals(str2)){
                    str2 = localCursor.getString(1);
                }
                Log.e("avatar", str2);
            }
            localCursor.close();
            localSQLiteDatabase.close();
        } else {
            String str1 = "/data/data/com.tencent.mm/MicroMsg/" + path + "EnMicroMsg.db";

            try {
                Process localProcess = Runtime.getRuntime().exec("su");
                DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/" + path + "\n");
                localDataOutputStream.writeBytes("chmod 666 " + str1 + "\n");
                localDataOutputStream.writeBytes("exit\n");
                localDataOutputStream.flush();
                localProcess.waitFor();
                net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(str1);
                net.sqlcipher.Cursor localCursor = localSQLiteDatabase.rawQuery("select reserved1,reserved2 from img_flag where username = '" + userName + "';", null);
                if (localCursor.moveToNext()) {
                    str2 = localCursor.getString(0);
                    if ("".equals(str2)){
                        str2 = localCursor.getString(1);
                    }
                    Log.e("avatar", str2);
                }
                localCursor.close();
                localSQLiteDatabase.close();

            } catch (Exception e) {
                Log.e("nickname", e.toString());
            }
        }

        return str2;
    }

    /**
     * @param paramContext
     * @param //paramCharSequence (用户昵称)
     * @param //                  paramString(文件夹名称)
     * @return 根据昵称获取用户名
     */
    public static ArrayList<String> findUserName(Context paramContext, CharSequence nickName, String dir) {
        ArrayList localArrayList = new ArrayList();
        if (dir.contains(".db")) {
            net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase;
            net.sqlcipher.Cursor localCursor;
            localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(dir);
            localCursor = localSQLiteDatabase.rawQuery("select username from rcontact where verifyFlag = 0 and (conRemark = '" + nickName + "' or nickname = '" + nickName + "');", null);
            while (localCursor.moveToNext()) {
                localArrayList.add(localCursor.getString(0));
                Log.e("TEMP", "findUserName:" + localCursor.getString(0));
            }
            localCursor.close();
            localSQLiteDatabase.close();
        } else {
            String str = "/data/data/com.tencent.mm/MicroMsg/" + dir + "EnMicroMsg.db";

            try {
                net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase;
                net.sqlcipher.Cursor localCursor;
                Process localProcess = Runtime.getRuntime().exec("su");
                DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/\n");
                localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/" + dir + "\n");
                localDataOutputStream.writeBytes("chmod 666 " + str + "\n");
                localDataOutputStream.writeBytes("exit\n");
                localDataOutputStream.flush();
                localProcess.waitFor();

                localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(str);
                localCursor = localSQLiteDatabase.rawQuery("select username from rcontact where verifyFlag = 0 and (conRemark = '" + nickName + "' or nickname = '" + nickName + "');", null);
                while (localCursor.moveToNext()) {
                    localArrayList.add(localCursor.getString(0));
                    Log.e("TEMP", "findUserName:" + localCursor.getString(0));
                }
                localCursor.close();
                localSQLiteDatabase.close();
            } catch (Exception e) {
                Log.e("nickname", e.toString());

            }
        }

        return localArrayList;
    }


    public static void loadAllUser(Context paramContext, String dir) throws Exception {
        String str = "/data/data/com.tencent.mm/MicroMsg/" + dir + "EnMicroMsg.db";
        //Log.e("readSnsInfo", "str = " + str);
        try {
            net.sqlcipher.database.SQLiteDatabase localSQLiteDatabase;
            net.sqlcipher.Cursor localCursor;
            Process localProcess = Runtime.getRuntime().exec("su");
            DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
            localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm\n");
            localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/\n");
            localDataOutputStream.writeBytes("chmod -R 777 /data/data/com.tencent.mm/MicroMsg/" + dir + "\n");
            localDataOutputStream.writeBytes("chmod 666 " + str + "\n");
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();

//            GlobeData.userNameMap.clear();
            GlobeData.listRcontact.clear();
            localSQLiteDatabase = EncryptedDbHelper.getInstance(paramContext).OpenDB(str);
            localCursor = localSQLiteDatabase.rawQuery("select username,nickname,conRemark,type from rcontact where verifyFlag = 0 and (type = 1 or type = 2 or type = 3 or type = 0);", null);
            while (localCursor.moveToNext()) {
                RcontactModel rm=new RcontactModel();
                rm.username=localCursor.getString(0);
                rm.nickname=localCursor.getString(1);
                rm.conRemark=localCursor.getString(2);
                GlobeData.listRcontact.add(rm);


//                GlobeData.userNameMap.put(localCursor.getString(0), localCursor.getString(1));
                Log.e("TEMP", "findUserName:" + localCursor.getString(0) + ":" + localCursor.getString(1)+ ":" + localCursor.getString(2));
            }
            localCursor.close();
            localSQLiteDatabase.close();
        } catch (Exception e) {
            Log.e("nickname", e.toString());
            throw e;
        }

    }


    public static ArrayList<MomentModel> getSnsRecordsByUserName(Context context, String dir, String userName, UserModel userModel) {
        ArrayList<MomentModel> list = new ArrayList<MomentModel>();
        if (dir.contains(".db")) {
//            String nickName = SnsHelper.findNickName(context, userName, dir);
//            String avatar = SnsHelper.findAvatar(context, userName, dir);
//            String sha1 = null;
//            if (!TextUtils.isEmpty(avatar)) {//图片的处理
//                avatar = avatar.replace("/0", "/46");
////                avatar = avatar.replace("/46","/0" );
//                sha1 = Utils.getFileSha1byte(avatar);
//            }
//            if (!sha1.equals(userModel.headurlsha)) {
//                return list;
//            }
//            Log.e("cursors", userName + ":" + userModel.nickname + ", avatar : " +  avatar + ", sha1 = " +  sha1 );
            android.database.sqlite.SQLiteDatabase localSQLiteDatabase;
            android.database.Cursor localCursor;
            if (dir.contains("EnMicroMsg.db")) {
                dir = dir.replace("EnMicroMsg.db", "SnsMicroMsg.db");
            }
            localSQLiteDatabase = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(new File(dir), null);
            String str3 = "select userName, content, type, head, createTime, stringSeq, attrBuf from snsInfo where userName='" + userName + "'";
            String str4 = str3 + " order by createTime desc;";
            localCursor = localSQLiteDatabase.rawQuery(str4, null);

            int i = 0;
            while (localCursor.moveToNext()) {
                int type = localCursor.getInt(2);
                int createTime = localCursor.getInt(4);
                String stringSeq = localCursor.getString(5);
                Log.i("", "++++type===" + type);
                if (type == 1 || type == 2||type==15) {// || type == 15
//                if (type == 1 ) {// || type == 15
                    byte[] contentByte = localCursor.getBlob(1);
                    ByteBuffer content = ByteBuffer.wrap(contentByte);
                    //byte[] attrBuf = localCursor.getBlob(6);
                    //String attrBufStr = new String(attrBuf);
                    //localSnsRecord.addPost(new SnsPost(localCursor.getString(0), localCursor.getBlob(1), localCursor.getInt(2), localCursor.getInt(3), localCursor.getInt(4), localCursor.getString(5), localCursor.getBlob(6)));
                    //Log.e("snsInfo",  "type = " + type + ", createTime = " + createTime + ", content = " + content  + "\n head = " + localCursor.getInt(3));
                    MomentModel model = parseContent(context, type, userName, content);
                    model.createTime = createTime;
                    model.type = 1;
                    if(type==15){
                        for (int j=0;j<model.imageList.size();j++){
                            if(model.imageList.get(j).indexOf("support.weixin.qq.com")!=-1){
                                model.imageList.remove(j);
                                j--;
                            }
                        }
                        model.video="1";
                    }
                    model.userName = userName;
                    model.nickName = userModel.nickname;
                     model.sha1 = userModel.headurlsha;
                     model.sep = stringSeq;
                    list.add(model);
                    i++;
                    if (i >= 3) {
                     }
                }
            }
            localCursor.close();
            localSQLiteDatabase.close();
        } else {
            String str1 = "/data/data/com.tencent.mm/MicroMsg/" + dir + "SnsMicroMsg.db";
//            String avatar = SnsHelper.findAvatar(context, userName, dir);
//            String sha1 = null;
//            if (!TextUtils.isEmpty(avatar)) {//图片的处理
//                avatar = avatar.replace("/0", "/46");
////                avatar = avatar.replace("/46","/0" );
//                sha1 = Utils.getFileSha1byte(avatar);
//            }
////            if (!sha1.equals(userModel.headurlsha)) {
////                return list;
////            }
//            Log.e("cursors", userName + ":" + userModel.nickname + ", avatar : " +  avatar + ", sha1 = " +  sha1 );
            try {
                android.database.sqlite.SQLiteDatabase localSQLiteDatabase;
                android.database.Cursor localCursor;
                //SnsRecord localSnsRecord;
                Process localProcess = Runtime.getRuntime().exec("su");
                DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
                localDataOutputStream.writeBytes("chmod 666 " + str1 + "\n");
                localDataOutputStream.writeBytes("chmod 666 " + str1 + "-journal\n");
                localDataOutputStream.writeBytes("exit\n");
                localDataOutputStream.flush();
                localProcess.waitFor();
                localSQLiteDatabase = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(new File(str1), null);
                String str3 = "select userName, content, type, head, createTime, stringSeq, attrBuf from snsInfo where userName='" + userName + "'";
                String str4 = str3 + " order by createTime desc;";
                localCursor = localSQLiteDatabase.rawQuery(str4, null);
                int i = 0;
                while (localCursor.moveToNext()) {
                    int type = localCursor.getInt(2);
                    int createTime = localCursor.getInt(4);
                    String stringSeq = localCursor.getString(5);
                    Log.i("", "++++type===" + type);
                    if (type == 1 || type == 2||type==15) {// || type == 15
//                        if (type == 1){// || type == 15
                        byte[] contentByte = localCursor.getBlob(1);
                        ByteBuffer content = ByteBuffer.wrap(contentByte);
                        Log.i("", "++++content===" + content.toString());
                        //byte[] attrBuf = localCursor.getBlob(6);
                        //String attrBufStr = new String(attrBuf);
                        //localSnsRecord.addPost(new SnsPost(localCursor.getString(0), localCursor.getBlob(1), localCursor.getInt(2), localCursor.getInt(3), localCursor.getInt(4), localCursor.getString(5), localCursor.getBlob(6)));
                        // Log.e("snsInfo",  "type = " + type + ", createTime = " + createTime + ", content = " + content  + "\n head = " + localCursor.getInt(3));
                        MomentModel model = parseContent(context, type, userName, content);
                        model.createTime = createTime;
                        if(type==15){
                            for (int j=0;j<model.imageList.size();j++){
                                if(model.imageList.get(j).indexOf("support.weixin.qq.com")!=-1){
                                    model.imageList.remove(j);
                                    j--;
                                }
                            }
                            model.video="1";
                        }
                        model.type = 1;
                        model.userName = userName;
                        model.nickName = userModel.nickname;
                        model.sha1 = userModel.headurlsha;
//                            model.seq = stringSeq;
                        model.sep = stringSeq;
                        list.add(model);
                        i++;
                        if (i >= 3) {
                            // break;
                        }
                    }
                }
                localCursor.close();
                localSQLiteDatabase.close();
            } catch (Exception localException) {
                Log.e("snsInfo", " " + localException.toString());
            }
        }
        return list;
    }

    public static ArrayList<MomentModel> testGetSnsRecords(Context context, String dir, String userName) {
        android.database.sqlite.SQLiteDatabase localSQLiteDatabase;
        android.database.Cursor localCursor;
        ArrayList<MomentModel> list = new ArrayList<MomentModel>();

        localSQLiteDatabase = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(new File(dir), null);

        String str3 = "select userName, content, type, head, createTime, stringSeq, attrBuf from snsInfo where userName='" + userName + "'";
        String str4 = str3 + " order by createTime desc;";

        localCursor = localSQLiteDatabase.rawQuery(str4, null);
        Log.i("android.database.Cursor", "count=" + localCursor.getCount());

        while (localCursor.moveToNext()) {
            int type = localCursor.getInt(2);
            int createTime = localCursor.getInt(4);
            Log.i("", "++++type===" + type);
            if (type == 1) {// || type == 15
                byte[] contentByte = localCursor.getBlob(1);
                ByteBuffer content = ByteBuffer.wrap(contentByte);

                //byte[] attrBuf = localCursor.getBlob(6);
                //String attrBufStr = new String(attrBuf);

                //localSnsRecord.addPost(new SnsPost(localCursor.getString(0), localCursor.getBlob(1), localCursor.getInt(2), localCursor.getInt(3), localCursor.getInt(4), localCursor.getString(5), localCursor.getBlob(6)));
                //Log.e("snsInfo",  "type = " + type + ", createTime = " + createTime + ", content = " + content  + "\n head = " + localCursor.getInt(3));

                MomentModel model = parseContent(context, type, userName, content);
                model.createTime = createTime;
                model.type = type;
                model.userName = userName;
                model.nickName = "";
                model.sha1 = "";
                list.add(model);
            }

        }

        localCursor.close();

        localSQLiteDatabase.close();

        return list;
    }

    //匹配网址
    public static String RegexUrl = "([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/_&=])+";
    //匹配文本内容
    public static String RegexCh = "[\\u4e00-\\u9fa5，。！、《》？#@%&\\~；()（）={}【】:：,.|<>`·‘'“”★/……a-zA-Z\\d\\s*]+";//，。！、《》？#@%&\~；()（）={}【】:：,.|<>`·‘'“”★…… //"[\\u4e00-\\u9fa5\\p{P}a-zA-Z\\d\\s*]+";
    //匹配朋友圈图片url
    public static String RegexImg = "http://.*?/0";//(http://mmsns.qpic.cn|http://shmmsns.qpic.cn)
    //匹配朋友圈视频url
    public static String RegexVideo = "http://vweixinf.tc.qq.com.*?bizid=\\d{1,9}";
    //匹配微信号
    public static String RegexWx = "wx[a-zA-Z\\d]+";
    // 内容字段类型
    private final static byte ContentFieldImage = 0x22;  // 字符串 url
    private final static byte ContentFieldImageThumb = 0x32;  // 字符串 url
    private final static byte ContentFieldImageThumb2 = 0x5A;  // 字符串 url
    private final static byte ContentFieldWxId = 0x12;  // 字符串
    private final static byte ContentFieldMemo = 0x2A;  // 字符串, 文字 ；也可能是一个节点的开始，类似于对象
    private final static byte ContentFieldMemo2 = 0x1A;  // 字符串, 文字，同上
    private final static byte ContentFieldTime = 0x0A;  // 字符串，时间戳？
    private final static byte ContentFieldUnknown1 = 0x18;  // var int ?
    //    private final static byte ContentFieldUnknown2 = (byte)0x80;  // skip 27 bytes
    private final static byte ContentFieldUnknown7 = 0x52;  // string
    private final static int ContentFieldToken = 0x00D2;  // 字符串 hash 32 bytes
    private final static int ContentFieldString1 = 0x00F2;  // 字符串
    private final static int ContentFieldString2 = 0x00EA;  // 字符串
    private final static int ContentFieldString3 = 0x0072;  // 字符串 MSG_SHARE_MOMENT_BEST_SCORE

    private static MomentModel parseContent(Context context, int type, String userName, ByteBuffer content) {
        MomentModel model = new MomentModel();
        String img, wxid, token = "";
        Log.i("parseContent", "buflen=" + content.remaining());
//        boolean firstTokenFlag = true;
        boolean memoFound = false;

        try {
            while (content.remaining() >= 2) {
                boolean eof = false;

//                byte flag = content.get();
                int flag = readInt(content);
//                Log.i("parseContent", String.format("%02x", flag));
                switch (flag) {
                    case ContentFieldImage:
                        img = readString(content);
                        if (!img.isEmpty()) {
                            model.imageList.add(img);
                            Log.i("parseContent", "img=" + img);
                        }
                        break;

                    case ContentFieldWxId:
                        wxid = readString(content);
                        break;

                    case ContentFieldMemo:
//                        int savedPos1 = content.position();
//                        int len = readInt(content);
//                        int savedPos2 = content.position();
//                        byte tempflag = content.get();
//                        if (tempflag == ContentFieldTime) {
//                            content.position(savedPos2);
//                            continue;
//                        }
                        if (memoFound) {
                            readInt(content);
                        } else {
//                            content.position(savedPos1);
                            model.content = readString(content);
                            memoFound = true;
                            Log.i("parseContent", "content=" + model.content);
                        }
                        break;

                    case ContentFieldImageThumb:
                    case ContentFieldImageThumb2:
                    case ContentFieldTime:
                    case ContentFieldMemo2:
                    case ContentFieldUnknown7:
                    case ContentFieldString1:
                    case ContentFieldString2:
                    case ContentFieldString3:
//                        readString(content);
//                        String temp = readString(content);
                        try{
                            int len = readInt(content);
                            if (len > 0) {
                                for (int i = 0; i < len; ++i) {
                                    content.get();
                                }
                            }
                        }catch (BufferUnderflowException e){

                        }

                        break;

//                    case ContentFieldUnknown2:
//                        content.position(content.position() + 27);
//                        break;

                    case ContentFieldToken:
//                        if (firstTokenFlag) {
//                            readInt(content);
//                            firstTokenFlag = false;
//                        } else {
//                            byte[] bytes = new byte[32];
//                            content.get(bytes);
//                            token = new String(bytes);
//                            eof = true;
//                        }
                        token = readString(content);
                        Log.i("parseContent", "token=" + token);
                        eof = true;
                        break;

                    default:
                        readInt(content);
                        break;
                }

                if (eof && token.isEmpty()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        while (matcherCh.find()){
//            String strCh = matcherCh.group();
//            if (strCh.length() >= 3) {
//
////                if (userName.equalsIgnoreCase(strCh)) {
////                    continue;
////                }
//
//                Matcher matcherNum = patternNum.matcher(strCh);
//                Matcher matcherWx = patternWx.matcher(strCh);
//                if (!matcherNum.matches() && !strCh.equalsIgnoreCase("Sight") && !strCh.equalsIgnoreCase("微信小视频") && !matcherWx.matches()) {
//
//                    model.content = strCh;
//                    break;
//                }
//
//            }
//
//        }

        return model;
    }


    private static String readString(ByteBuffer content) {
        int len = readInt(content);
//        Log.i("readString", "len=" + len);
        if (len > 0) {
            byte[] bytes = new byte[len];
            content.get(bytes, 0, len);
//            Log.i("readString", "pos=" + content.position());
            return new String(bytes, Charset.forName("UTF-8"));
        }
        return "";
    }

    private static int readInt(ByteBuffer content) {
        int val1 = content.get();
        int val2 = 0;
        int val3 = 0;
        int val4 = 0;
        int val5 = 0;

        if ((val1 & 0x80) == 0x80) {
            val2 = content.get();
            if ((val2 & 0x80) == 0x80) {
                val3 = content.get();
                if ((val3 & 0x80) == 0x80) {
                    val4 = content.get();
                    if ((val4 & 0x80) == 0x80) {
                        val5 = content.get();
                    }
                }
            }
        }

        return ((val1 & 0x7f) | ((val2 & 0x7f) << 7) | ((val3 & 0x7f) << 14) | ((val4 & 0x7f) << 21) | (val5 << 28));
    }

}

//wxid_39zmq0ute1th11

