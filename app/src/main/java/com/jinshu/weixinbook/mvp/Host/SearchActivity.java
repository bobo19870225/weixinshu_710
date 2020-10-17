package com.jinshu.weixinbook.mvp.Host;

import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.activity.SingleActivity;
import com.jinshu.weixinbook.adapter.RefreshAdapter;
import com.jinshu.weixinbook.adapter.SearchAdapter;
import com.jinshu.weixinbook.base.BaseActivity;
import com.jinshu.weixinbook.db.DatabaseManager;
import com.jinshu.weixinbook.utils.sns.UserModel;
import com.jinshu.weixinbook.widget.AbstractAction;

import java.util.List;

public class SearchActivity  extends BaseActivity implements ISearchContract.View {
    private EditText et_search;
    private ListView lv;
    private SearchPresenter presenter;
    private SearchAdapter adapter;
    private List<UserModel> lists;
    @Override
    protected int setContentViewId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initViews() {
        setTitle("好友");
        setBackAction();
        getMyActionBar().addAction(new AbstractAction("搜索") {
            @Override
            public int getBackgroundResource() {
                return R.color.theme_color;
            }

            @Override
            public void performAction(View view) {
                presenter.search(et_search.getText().toString());
            }
        });
        presenter = new SearchPresenter(this, this,queues);
        et_search= (EditText) findViewById(R.id.et_search);
//        et_search .setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    presenter.search(et_search.toString());
//                    return true;
//                }
//                return false;
//            }
//        });
        lv= (ListView) findViewById(R.id.lv);
        adapter=new SearchAdapter(this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 presenter.onItemClick(lists.get(position));
            }
        });
    }

    @Override
    public void jump(UserModel um) {
        Bundle bundle=new Bundle();
        bundle.putParcelable("UserModel", um);
        jump(SingleActivity.class,bundle);
    }

    @Override
    public void setMyList(List lists){
        this.lists=lists;
        adapter.setMyList(lists);
        adapter.notifyDataSetChanged();
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
}
