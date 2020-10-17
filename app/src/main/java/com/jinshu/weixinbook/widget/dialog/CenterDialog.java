package com.jinshu.weixinbook.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jinshu.weixinbook.R;


/**
 * Created by fengshuai on 15/9/8.
 */
public class CenterDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private int layoutResID;

    /** 要监听的控件id */
    private int[] listenedItems;

    private OnCenterItemClickListener listener;

    public CenterDialog(Context context, int layoutResID, int[] listenedItems) {
        super(context, R.style.dialogCenter);
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItems = listenedItems;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.bottom_menu_animation); // 添加动画
        setContentView(layoutResID);
        // 宽度全屏
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth()*4/5); // 设置宽度
        getWindow().setAttributes(lp);
//        ((TextView)findViewById(R.id.tv_errorMsg)).setTextSize(TypedValue.COMPLEX_UNIT_DIP ,30);
//        ((TextView)findViewById(R.id.tv_cancel)).setTextSize(TypedValue.COMPLEX_UNIT_DIP ,30);
        // 点击Dialog外部消失
        setCanceledOnTouchOutside(true);

        for (int id : listenedItems) {
            findViewById(id).setOnClickListener(this);
        }
    }

    public interface OnCenterItemClickListener {

        void OnCenterItemClick(CenterDialog dialog, View view);

    }

    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        dismiss();
        listener.OnCenterItemClick(this, view);
    }
}
