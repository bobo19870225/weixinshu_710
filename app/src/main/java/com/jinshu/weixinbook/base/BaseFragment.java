package com.jinshu.weixinbook.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.utils.LogUtil;
import com.jinshu.weixinbook.widget.AbstractAction;
import com.jinshu.weixinbook.widget.ActionBar;


/**
 * Created by admin on 2017/5/3.
 */
public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    private ActionBar actionBar;
    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = initView();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setView();
    }

    protected void setView() {
    }


    public ActionBar getMyActionBar() {
        if (actionBar == null) {
            View barView = mRootView.findViewById(R.id.actionbar);
            if (barView instanceof ActionBar) {
                actionBar = (ActionBar) barView;
            }
        }
        if (actionBar == null) {
            LogUtil.d("actionBar == null --- -----");
        }

        return actionBar;
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
                        getActivity().finish();
                    }

                });
    }

    protected abstract View initView();

    protected void initData() {

    }
}
