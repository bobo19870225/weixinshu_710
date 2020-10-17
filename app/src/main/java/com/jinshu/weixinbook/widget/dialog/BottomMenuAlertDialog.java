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
 * 底部弹出式菜单
 * 使用方法
 * 1. 创建底部弹出菜单的布局文件
 * 2. 创建BottomMenuAlertDialog对象
 *    BottomMenuAlertDialog menu = new BottomMenuAlertDialog(this, R.layout.xxx, new int[]{R.id.xxx, R.id.xxxx});
 * 3. 调用show()使菜单显示出来
 *    menu.show();
 * 4. 设置监听器OnBottomMenuItemClickListener
 *    menu.setOnBottomMenuItemClickListener(new MyOnBottomMenuItemClickListener());
 * 5. 在onClick回调中使用传人的dialog和被点击的view进行处理
 *
 */

public class BottomMenuAlertDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private int layoutResID;

    /** 要监听的控件id */
    private int[] listenedItems;

    private OnBottomMenuItemClickListener listener;

    public BottomMenuAlertDialog(Context context, int layoutResID, int[] listenedItems) {
        super(context, R.style.dialogBottom);
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItems = listenedItems;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.bottom_menu_animation); // 添加动画
        setContentView(layoutResID);
        // 宽度全屏
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth()*10/10); // 设置宽度
        getWindow().setAttributes(lp);
        // 点击Dialog外部消失
        setCanceledOnTouchOutside(true);

        for (int id : listenedItems) {
            findViewById(id).setOnClickListener(this);
        }
    }

    public interface OnBottomMenuItemClickListener {

        void onBottomMenuItemClick(BottomMenuAlertDialog dialog, View view);

    }

    public void setOnBottomMenuItemClickListener(OnBottomMenuItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        dismiss();
        listener.onBottomMenuItemClick(this, view);
    }

}
