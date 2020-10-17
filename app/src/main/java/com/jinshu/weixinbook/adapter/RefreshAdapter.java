package com.jinshu.weixinbook.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.base.AdapterHolder;
import com.jinshu.weixinbook.base.MyBaseAdapter;
import com.jinshu.weixinbook.utils.sns.UserModel;

/**
 * Created by jinshu on 2017/8/11.
 */

public class RefreshAdapter extends MyBaseAdapter {

    public RefreshAdapter(Context context) {
        super(context);
    }

    @Override
    public int setLatoutId() {
        return R.layout.item_refres;
    }

    @Override
    public AdapterHolder setAdapterHolder(View convertView, int position) {
        return new AdapterHolder(convertView);
    }

    @Override
    public void initViews(AdapterHolder holder, Object item, int position) {
        UserModel model= (UserModel) item;
        ImageView iv_portrait = holder.getView(R.id.iv_portrait);
        Glide.with(context).load(model.headimgurl).into(iv_portrait);
        holder.setText(R.id.tv_name,model.nickname );

    }
}
