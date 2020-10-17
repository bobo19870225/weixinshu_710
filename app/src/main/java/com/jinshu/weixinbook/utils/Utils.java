package com.jinshu.weixinbook.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import android.content.Intent;
import java.nio.MappedByteBuffer;

@SuppressLint({"SdCardPath"})
public class Utils
{
    public final static String HOST = "http://wxbook.haoju.me/";
    public static final String COMPATIBLE_INFO_FILE_NAME = "CompatibleInfo.cfg";
    public static final String ENCRYPTED_DB_NAME = "EnMicroMsg.db";
    public static final String JSON_FILE_EXT = ".json64";
    public static final String MM_DATA_FOLDER = "/data/data/com.tencent.mm/MicroMsg/";
    public static final String RANDOM_ID_FILE_NAME = "ran_id.txt";
    public static final String SNS_DB_FILE_NAME = "SnsMicroMsg.db";
    public static final String SYSTEM_INFO_FILE_NAME = "systemInfo.cfg";
    public static final String XINSHU_FOLLOWED_OPENID_NAME = "openid";
    public static final String XINSHU_FOLLOWED_SCANNED_NAME = "scan";
    public static final String XINSHU_FOLLOWED_STATUS_URL = "http://wx.xinshu.me/api/check_qrcode/";
    public static final String XINSHU_JSON_UPLOAD_NAME = "docfile";
    public static final String XINSHU_JSON_UPLOAD_URL = "http://img.xinshu.me/upload/json";
    public static final String XINSHU_QRCODE_API = "http://wx.xinshu.me/api/qrcode";
    public static final String XINSHU_QRCODE_URL_NAME = "qrcode_url";

    public static HashMap loadHashMapFromFile(String paramString)
    {
        try
        {
            ObjectInputStream localObjectInputStream = new ObjectInputStream(new FileInputStream(paramString));
            Object localObject = localObjectInputStream.readObject();
            localObjectInputStream.close();
            HashMap localHashMap = (HashMap)localObject;
            return localHashMap;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
        return null;
    }

    public static String md5(String paramString)
    {
        try
        {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramString.getBytes());
            byte[] arrayOfByte = localMessageDigest.digest();
            StringBuffer localStringBuffer = new StringBuffer("");
            for (int i = 0; i < arrayOfByte.length; i++)
            {
                int j = arrayOfByte[i];
                if (j < 0)
                    j += 256;
                if (j < 16)
                    localStringBuffer.append("0");
                localStringBuffer.append(Integer.toHexString(j));
            }
            String str = localStringBuffer.toString();
            return str;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
        return "";
    }

	public static String encrypt(String strSrc, String encName) {
		// parameter strSrc is a string will be encrypted,
		// parameter encName is the algorithm name will be used.
		// encName dafault to "MD5"
		MessageDigest md = null;
		String strDes = null;
		byte[] bt = strSrc.getBytes();
		try {
			if (encName == null || encName.equals("")) {
				encName = "MD5";
			}
			md = MessageDigest.getInstance(encName);
			md.update(bt);
			strDes = bytes2Hex(md.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Invalid algorithm.");
			return null;
		}
		return strDes;
	}

	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}


    public static String getFileSha1(String urlPath) {
        File file = null;
        try {

            // 统一资源
            URL url = new URL(urlPath);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.connect();

            // 文件大小
            int fileLength = httpURLConnection.getContentLength();
            String text = downloadAsString(httpURLConnection);
            return encrypt(text, "SHA-1");

//            return new SHA1().Digest(text);

//            return PHPsha1.computeSha1OfString(text);
            // 文件名
//			String filePathUrl = httpURLConnection.getURL().getFile();
//			String fileFullName = filePathUrl.substring(filePathUrl
//					.lastIndexOf(File.separatorChar) + 1);

            //URLConnection con = url.openConnection();

//            BufferedInputStream bin = new BufferedInputStream(
//                    httpURLConnection.getInputStream());
//
//            String path = savePath;
//            if (path == null) {
//                //path = FileUtils.getCachePath(urlPath);
//            }
//
//            //String path = downloadDir + File.separatorChar + fileFullName;
//            file = new File(path);
//            if (!file.getParentFile().exists()) {
//                file.getParentFile().mkdirs();
//            }
//
//            OutputStream out = new FileOutputStream(file);
//            int size = 0;
//            int len = 0;
//            byte[] buf = new byte[1024];
//            while ((size = bin.read(buf)) != -1) {
//                len += size;
//                out.write(buf, 0, size);
//
//                float progress = len*1.0f/(fileLength*1.0f);
//
//            }
//
//            bin.close();
//            out.flush();
//            out.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  finally {

        }

        return null;
    }



    public static String downloadAsString(URLConnection urlConnection)
    {
        StringBuilder sb = new StringBuilder();
        String temp = null;
        try {
            InputStream is = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void killProcess(Context context, String pkgName) {
        ActivityManager mAm;

        mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        mAm.restartPackage(pkgName);//.killBackgroundProcesses(pkgName);//.forceStopPackage(pkgName);

    }

/*
    微信要反编译 把这些值找出来就行
    或者你直接 去找个 6.3.13.49_r4080b63   6.3.13.64_r4488992 版本的
*/
    //http://www.cnblogs.com/Free-Thinker/p/4580617.html
    //https://blog.chionlab.moe/2016/03/31/WeChatMomentStat-update-log/
    public static void forceStopProgress(Context context, String pkgName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        try {
            Method forceStopPackage = am.getClass().getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(am, pkgName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        for (ActivityManager.RunningAppProcessInfo service : am.getRunningAppProcesses()) {

            if (service.processName.contains(pkgName)) {
                Log.e("process name " , service.processName);
                android.os.Process.killProcess(service.pid);
            }
        }
    }


    public static void killApp(Context context, String pkgName) throws Throwable {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
        int pid = -1;
        for (int i = 0; i < pids.size(); i++) {
            final ActivityManager.RunningAppProcessInfo info = pids.get(i);
            if (info.processName.contains(pkgName)) {
                pid = info.pid;
                if (pid != -1) {
                    final Process su = Runtime.getRuntime().exec("su");
                    final DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
                    outputStream.writeBytes("kill " + pid + "\n");
                    outputStream.writeBytes("exit\n");
                    outputStream.flush();
                    outputStream.close();
                }
            }
        }

    }


    public static void copyFile(String srcDir, final String destDir, final String fileName) {
        Log.e("", "srcDir = " + srcDir + ", destDir = " + destDir + ", fileName = " + fileName);
        final Process su;
        try {
            su = Runtime.getRuntime().exec("su");
            final DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes("mount -o remount,rw " + srcDir + "\n");
            outputStream.writeBytes("cd " + srcDir + "\n");
            outputStream.writeBytes("ls | while read line; do cp ${line}/" + fileName + " " + destDir + "/ ; done \n");
            outputStream.writeBytes("sleep 1\n");
            outputStream.writeBytes("chmod 777 " + destDir + "/" + fileName + "\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            outputStream.close();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String dbPathForWeChat(String pathName) {
        String path = "/data/data/com.tencent.mm/MicroMsg/" + pathName + "/";

        return path;
    }

    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }


    public static void copyWeChatFile(String dir, String fileName) {
        String fullPath = getExtDbPath(fileName);
        //Log.e("", "fullPath = " + fullPath);
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }

        copyFile(dbPathForWeChat(dir)+fileName, fullPath);
    }

    public static String getExtDbPath(String fileName) {
        return getInnerSDCardPath() + "/windows/Pictures/" + fileName;
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                 }
                inStream.close();
             }
         } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
             e.printStackTrace();

          }

    }

    public static void restartApp(Context context, String pkgName) throws Throwable {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
        int pid = -1;
        for (int i = 0; i < pids.size(); i++) {
            final ActivityManager.RunningAppProcessInfo info = pids.get(i);
            if (info.processName.equalsIgnoreCase(pkgName)) {
                pid = info.pid;
            }
        }
        if (pid != -1) {
            final Process su = Runtime.getRuntime().exec("su");
            final DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes("kill " + pid + "\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            outputStream.close();
        }
        final Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
        context.startActivity(launchIntent);

    }


    public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getFileSha1ByUrl(String urlPath) {
        File file = null;
        try {

            // 统一资源
            URL url = new URL(urlPath);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.connect();

            // 文件大小
            int fileLength = httpURLConnection.getContentLength();

            // 文件名
			String filePathUrl = httpURLConnection.getURL().getFile();
			String fileFullName = filePathUrl.substring(filePathUrl
					.lastIndexOf(File.separatorChar) + 1);

            URLConnection con = url.openConnection();

            BufferedInputStream bin = new BufferedInputStream(
                    httpURLConnection.getInputStream());

            String path = getExtDbPath(MD5(urlPath));

            //String path = downloadDir + File.separatorChar + fileFullName;
            file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            OutputStream out = new FileOutputStream(file);
            int size = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            while ((size = bin.read(buf)) != -1) {
                len += size;
                out.write(buf, 0, size);
            }

            bin.close();
            out.flush();
            out.close();

            return getFileSha1(new File(path));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  finally {

        }

        return null;
    }


    public static String getFileSha1(File file) throws OutOfMemoryError,IOException, NoSuchAlgorithmException {
         MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");
         FileInputStream in = new FileInputStream(file);
         FileChannel ch = in.getChannel();
         MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
         messagedigest.update(byteBuffer);

        return bufferToHex(messagedigest.digest());
     }


    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
     for (int l = m; l < k; l++) {
              appendHexPair(bytes[l], stringbuffer);
             }
          return stringbuffer.toString();
       }


    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

//    public static void listProcess(Context context, String pkgName) {
//        SparseArray<AppProcessInfo> mTmpAppProcesses = new SparseArray<AppProcessInfo>();
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//
//        // Retrieve list of running processes, organizing them into a sparse
//        // array for easy retrieval.
//        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses(); // 获取正在运行的进程
//        final int NP = processes != null ? processes.size() : 0;
//        mTmpAppProcesses.clear();
//        for (int i=0; i<NP; i++) {
//            ActivityManager.RunningAppProcessInfo pi = processes.get(i);
//            mTmpAppProcesses.put(pi.pid, new AppProcessInfo(pi));
//        }
//    }


    public static void saveString(Context context, String keyStr, String valueStr) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(keyStr, valueStr);
        //提交当前数据
        editor.commit();
    }

    public static String getString(Context context, String keyStr) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",
                Activity.MODE_PRIVATE);

        return sharedPreferences.getString(keyStr, null);
    }

    public static boolean isLogin(Context context) {
        String name = getString(context, "userName");

        //Log.e("", "name = " + name + ", userId = " + userId);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",
                Activity.MODE_PRIVATE);

        return (name != null && name.length() > 0);
    }

    public  static String getFileSha1byte(String urlPath) {
        File file = null;
        try {

            // 统一资源
            URL url = new URL(urlPath);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.connect();
            // 文件大小
            int fileLength = httpURLConnection.getContentLength();
//            String text = downloadAsString(httpURLConnection);
//            return encrypt(text, "SHA-1");
            String test=downloadAsByte(httpURLConnection);
            return test;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  finally {

        }

        return null;
    }

    public  static String downloadAsByte(URLConnection urlConnection)
    {
        StringBuilder sb = new StringBuilder();
        String temp = null;
        int fileLength = urlConnection.getContentLength();
        byte[] bytes=new byte[fileLength];
        try {
            InputStream is = urlConnection.getInputStream();
            int b;
            int i=0;
            for ( ;(b=is.read())!=-1;i++){
                bytes[i]=(byte) b;
            }

            MessageDigest md = null;
            String strDes = null;
            String encName="SHA-1";
            try {
                if (encName == null || encName.equals("")) {
                    encName = "MD5";
                }
                md = MessageDigest.getInstance(encName);
                md.update(bytes);
                strDes = bytes2Hex(md.digest()); // to HexString
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Invalid algorithm.");
                return null;
            }
            return strDes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}






/*
1.1 获取1个小时之内关注的用户
●▪功能介绍
      	获取1个小时之内关注的用户
●▪接口路径
http://120.25.218.99/www/wxbook/public/index.php/admin/Wechat/check_60_mins_subscribe_time●▪提交表单参数详解（方式提交：get）

●▪返回值（json）

1.2 上传朋友圈数据
●▪功能介绍
      	上传朋友圈数据
●▪接口路径
http://120.25.218.99/www/wxbook/public/index.php/admin/Wechat/uploadWechatContent参数详解（方式提交：post）

●▪返回值（json）


*/

