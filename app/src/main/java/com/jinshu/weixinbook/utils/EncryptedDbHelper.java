package com.jinshu.weixinbook.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.util.HashMap;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;


@SuppressLint({"SdCardPath"})
 public class EncryptedDbHelper {
    private static EncryptedDbHelper instance = null;

    private SQLiteDatabase database = null;
    private String dbKey = null;
    private SQLiteDatabaseHook openHook = null;
    private EncryptedDbHelper(Context paramContext)
    {
        SQLiteDatabase.loadLibs(paramContext);
        if (this.openHook == null)
            this.openHook = new SQLiteDatabaseHook()
            {
                public void postKey(SQLiteDatabase paramAnonymousSQLiteDatabase)
                {
                    paramAnonymousSQLiteDatabase.rawExecSQL("PRAGMA kdf_iter = 4000;");
                    paramAnonymousSQLiteDatabase.rawExecSQL("PRAGMA cipher_use_hmac = OFF;");
                }

                public void preKey(SQLiteDatabase paramAnonymousSQLiteDatabase)
                {
                }
            };
    }

    /// <summary>
    /// 静态方法，获取数据库连接实例
    /// </summary>
    /// <returns>数据库连接实例</returns>
    public static EncryptedDbHelper getInstance(Context paramContext)
    {
        if (EncryptedDbHelper.instance == null)
        {
            EncryptedDbHelper.instance = new EncryptedDbHelper(paramContext);
        }
        return EncryptedDbHelper.instance;
    }

    @SuppressLint({"DefaultLocale"})
    public static String getDBKey()
    {

        try
        {
          HashMap localHashMap1;
            Process localProcess = Runtime.getRuntime().exec("su");
            DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
            localDataOutputStream.writeBytes("chmod 771 /data/data/com.tencent.mm\n");
            localDataOutputStream.writeBytes("chmod 777 /data/data/com.tencent.mm/MicroMsg/\n");
            localDataOutputStream.writeBytes("chmod 777 /data/data/com.tencent.mm/MicroMsg/systemInfo.cfg\n");
            localDataOutputStream.writeBytes("chmod 777 /data/data/com.tencent.mm/MicroMsg/CompatibleInfo.cfg\n");
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
                localHashMap1 = Utils.loadHashMapFromFile("/data/data/com.tencent.mm/MicroMsg/systemInfo.cfg");
            if (localHashMap1 == null)
            {
                Log.e("ERROR", "An ERROR occured while reading systemInfo.cfg");
                return "ERROR";
            }
            HashMap localHashMap2 = Utils.loadHashMapFromFile("/data/data/com.tencent.mm/MicroMsg/CompatibleInfo.cfg");
            if (localHashMap2 == null)
            {
                Log.e("ERROR", "An ERROR occured while reading CompatibleInfo.cfg");
                return "ERROR";
            }
            String str1 = getUIN(localHashMap1);
            Log.v("EnMicroMsg UIN", str1);
            String str2 = getIMEI(localHashMap1, localHashMap2);
            Log.v("EnMicroMsg IMEI", str2);
            if (("".equals(str1) ) || ("".equals(str2)))
            {
                Log.e("ERROR", "An ERROR occured while generating key.");
                return "ERROR";
            }
            return Utils.md5(str2 + str1).toLowerCase().substring(0, 7);


        }
        catch (Exception localException)
        {
          //  HashMap localHashMap1;
        //    while (true)
               // localException.printStackTrace();
            return "ERROR";
        }
    }
    public static String getUIN(HashMap paramHashMap)
    {
        String str;
        try
        {
            str = paramHashMap.get(1).toString();
            if ((str == null) || (str.length() == 0))
                throw new Exception();
        }
        catch (Exception localException)
        {
            Log.e("ERROR", "An ERROR occured while getting UIN code.");
            localException.printStackTrace();
            str = "";
        }
        return str;
    }
    public static String getIMEI(HashMap paramHashMap1, HashMap paramHashMap2)
    {
        String str;
        try
        {
            boolean bool = paramHashMap1.containsKey(258);
            str = null;
            if (bool)
                str = paramHashMap1.get(258).toString();
            if ((str == null) || (str.length() == 0))
                str = (String)paramHashMap2.get(258);
            if ((str == null) || (str.length() == 0))
                throw new Exception();
        }
        catch (Exception localException)
        {
            Log.e("ERROR", "An ERROR occured while getting IMEI code.");
            localException.printStackTrace();
            str = "";
        }
        return str;
    }
    public SQLiteDatabase OpenDB(String paramString)
    {
        this.dbKey = getDBKey();
        Log.v("EnMicroMsg DBKey", this.dbKey);
        File localFile = new File(paramString);
        Log.v("OpenEnDb PATH", localFile.getAbsolutePath());
        Log.v("OpenEnDb CANREAD", localFile.canRead() + "");
        Log.v("OpenEnDb CANWRITE", localFile.canWrite() + "");
        Log.v("OpenEnDb EXISTS", localFile.exists() + "");
        this.database = SQLiteDatabase.openOrCreateDatabase(localFile, this.dbKey, null, this.openHook);
        return this.database;
    }
}
