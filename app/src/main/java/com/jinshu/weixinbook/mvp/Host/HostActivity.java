package com.jinshu.weixinbook.mvp.Host;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.activity.HistoyActivity;
import com.jinshu.weixinbook.base.BaseActivity;

public class HostActivity extends BaseActivity implements IHostContract.View {
    private TextView item_name;
    private TextView item_log;
    private TextView item_refresh;
    private Button btn_history;
    private Button btn_after_work;
    private Button item_friends;
    private HostPresenter presenter;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        presenter = new HostPresenter(this, this,queues);
        item_name = (TextView) findViewById(R.id.item_name);
        item_log = (TextView) findViewById(R.id.item_log);
        item_log.setMovementMethod(new ScrollingMovementMethod());
        item_refresh = (TextView) findViewById(R.id.item_refresh);
        btn_history = (Button) findViewById(R.id.btn_history);
        btn_after_work = (Button) findViewById(R.id.btn_after_work);
        item_friends = (Button) findViewById(R.id.item_friends);
        item_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.refresh();
            }
        });
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump(HistoyActivity.class, null);
            }
        });
        btn_after_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.appAttendancerun();
            }
        });
        item_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump(SearchActivity.class, null);
            }
        });

        this.findViewById(R.id.item_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.upload();
            }
        });
//        String Folder = SnsHelper.findUserFolder("");
//        String dbk = EncryptedDbHelper.getDBKey();
//        Log.e("dbk================ ", dbk + ", Folder = " + Folder);
//        if (!Utils.isLogin(baseActivity)) {
//        }
        presenter.start();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.finish();
    }

    @Override
    public void hideWaitDialogImpl() {
        hideWaitDialog();
    }

    @Override
    public void showErrorDialogImpl(String str) {
        showErrorDialog(str);
    }

    @Override
    public void showWaitDialogImpl(String str) {
        showWaitDialog(str);
    }

    @Override
    public void logSetText(String str) {
        item_log.setText(str);
    }
    @Override
    public void nameSetText(String str) {
        item_name.setText(str);
    }

    @Override
    public String logGetText() {
        return item_log.getText().toString();
    }
}
