package com.jinshu.weixinbook.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.adapter.RefreshAdapter;
import com.jinshu.weixinbook.base.BaseActivity;
import com.jinshu.weixinbook.constant.Constant;
import com.jinshu.weixinbook.db.DatabaseManager;
import com.jinshu.weixinbook.utils.GlobeData;
import com.jinshu.weixinbook.utils.SPUtils;
import com.jinshu.weixinbook.utils.sns.UserModel;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HistoyActivity extends BaseActivity {

    private ListView list;
    private RefreshAdapter adapter;
    DatabaseManager dm;
    private String siteUrl;
    private List<UserModel> lists;
    @Override
    protected int setContentViewId() {
        return R.layout.activity_histoy;
    }

    @Override
    protected void initViews() {
        setTitle("历史");
        setBackAction();
        list= (ListView) findViewById(R.id.list);
        adapter=new RefreshAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle=new Bundle();
                bundle.putParcelable("UserModel", lists.get(position));
                jump(SingleActivity.class,bundle);
            }
        });
        dm = new DatabaseManager(this);
        siteUrl = (String) SPUtils.get(baseActivity, Constant.siteUrl, "");
        Observable.just("")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        try{
                            lists=dm.queryRefreshList( siteUrl);
                        }catch (Exception e){
                            lists=dm.queryRefreshList( siteUrl);
                        }
                        return "";
                    }
                }).subscribeOn(Schedulers.io())//把工作线程指定为了IO线程
                .observeOn(AndroidSchedulers.mainThread())//把回调线程指定为了UI线程
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        adapter.setMyList(lists);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
