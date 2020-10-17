package com.jinshu.weixinbook.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.utils.LogUtil;
import com.jinshu.weixinbook.widget.AbstractAction;
import com.jinshu.weixinbook.widget.ActionBar;
import com.jinshu.weixinbook.widget.dialog.CenterDialog;
import com.jinshu.weixinbook.widget.dialog.DialogControl;
import com.jinshu.weixinbook.widget.dialog.MyProgressDialog;
import com.jinshu.weixinbook.widget.dialog.OkDialog;

import java.util.List;


/**
 * Created by admin on 2016/7/28.
 */
public abstract class BaseActivity extends FragmentActivity  implements  DialogControl{

    public boolean isMainActivity = false; //当前界面是否是主界面
    private boolean isEntered = false;//当前应用是否进入后台
    private ActionBar actionBar;
    protected BaseActivity baseActivity;
    public RequestQueue queues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(setContentViewId());

        baseActivity=this;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制是竖屏
        queues = Volley.newRequestQueue(this);
        initViews();
        closeInput();
    }

    /**
     * 初始化布局
     */
    protected abstract int setContentViewId();

    /**
     * 初始化参数
     */
    protected abstract void initViews();

    /**
     * activity的跳转
     */
    public void jump(Class cla, Bundle bundle) {
        Intent intent = new Intent(this, cla);
        if (bundle != null) {
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            startActivity(intent);
        }
    }
    public void setTitle(String title) {

        getMyActionBar().setTitle(title);
    }
    public ActionBar getMyActionBar() {
        if (actionBar == null) {
            View barView = findViewById(R.id.actionbar);
            if (barView instanceof ActionBar) {
                actionBar = (ActionBar) barView;
            }
        }
        if (actionBar == null) {
            LogUtil.d("actionBar == null --- -----");
        }

        return actionBar;
    }
    public void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    public void setBackAction() {
        getMyActionBar().setHomeAction(
                new AbstractAction(R.mipmap.ic_back) {

                    @Override
                    public int getBackgroundResource() {
                        return 0;
                    }

                    @Override
                    public void performAction(View view) {
                        finish();
                    }

                });
    }

    public <T extends Object> T getData(Bundle data, String key, T def) {
        if (data != null) {
            T t = (T) data.get(key);
            if (t != null) {
                return t;
            }
        }
        return def;
    }

    public <T extends Object> T getData(String key, T def) {
        return getData(getIntent().getExtras(), key, def);
    }

//===========================================================================

    /**
     * 在主界面是连续点击两次退出应用@#
     */
    @Override
    public void onBackPressed() {
        if (isMainActivity) {
            isMainActivity = false;
            showErrorDialog( "再次点击退出应用程序");
            onBack();
        } else {
            super.onBackPressed();
        }
    }

    int delayMeillis = 2500;
    Handler handler = new Handler();

    public void onBack() {
        handler.postDelayed(runnable, delayMeillis);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isMainActivity = true;
        }
    };
//===========================================================================

    /**
     * 判断当前应用是否 处于前台
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        closeInput();
        hideWaitDialog();
        hideErrorDialog();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            Log.i("LOG____", "进入后台");
            isEntered = true;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isAppOnForeground() && isEntered) {
            Log.i("LOG____", "重启");
            isEntered = false;
        }
    }

    /**
     * 关闭软件盘
     */
    public void closeInput() {
        View v = getWindow().peekDecorView();
        if (v != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    //弹出框
    protected MyProgressDialog progressDialog;
    protected CenterDialog errorDialog;
    protected OkDialog okDialog;
    //=================================等待对话框=======================
    @Override
    public void hideWaitDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public MyProgressDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    public boolean isDialogShowing() {
        return progressDialog == null ? false : progressDialog.isShowing();
    }

    @Override
    public MyProgressDialog showWaitDialog(int resid) {
        return showWaitDialog("加载中");
    }

    @Override
    public MyProgressDialog showWaitDialog(String text) {
        if (progressDialog == null) {
            progressDialog = MyProgressDialog.show(this, text);
        } else {
            progressDialog.setMessage(text);
        }
        progressDialog.show();
        return progressDialog;
    }

    //=================================错误对话框=======================
    @Override
    public void hideErrorDialog() {
        if (errorDialog != null) {
            errorDialog.dismiss();
            errorDialog = null;
        }
    }

    @Override
    public CenterDialog showErrorDialog(int resid) {
        return showErrorDialog("加载中");
    }

    @Override
    public CenterDialog showErrorDialog(String text) {
        if (errorDialog == null) {
            errorDialog = new CenterDialog(this, R.layout.dialog_error_toast, new int[]{R.id.tv_cancel});
            errorDialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
                @Override
                public void OnCenterItemClick(CenterDialog dialog, View view) {
                    errorDialog.dismiss();
                }
            });
        }
        errorDialog.show();
        TextView tv_errorMsg = (TextView) errorDialog.findViewById(R.id.tv_errorMsg);
        tv_errorMsg.setText(text);

        return errorDialog;
    }

    @Override
    public CenterDialog showErrorDialog(String text, CenterDialog.OnCenterItemClickListener listener) {
        if (errorDialog == null) {
            errorDialog = new CenterDialog(this, R.layout.dialog_error_toast, new int[]{R.id.tv_cancel});
            errorDialog.setOnCenterItemClickListener(listener);
        }
        errorDialog.show();
        TextView tv_errorMsg = (TextView) errorDialog.findViewById(R.id.tv_errorMsg);
        tv_errorMsg.setText(text);
        return errorDialog;
    }

    @Override
    public OkDialog showOkDialog(String text, OkDialog.OnCenterItemClickListener listener) {
        if (okDialog == null) {
            okDialog = new OkDialog(this, R.layout.dialog_normal_layout, new int[]{R.id.positiveButton, R.id.negativeButton});
            okDialog.setOnCenterItemClickListener(listener);
        }
        okDialog.show();
        TextView message = (TextView) okDialog.findViewById(R.id.message);
        message.setText(text);
        return okDialog;
    }
}
