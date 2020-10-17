package com.jinshu.weixinbook.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.base.AdapterHolder;
import com.jinshu.weixinbook.base.MyBaseAdapter;
import com.jinshu.weixinbook.utils.sns.UserModel;

/**
 * Created by jinshu on 2017/8/11.
 */

public class SearchAdapter extends MyBaseAdapter {

    public SearchAdapter(Context context) {
        super(context);
    }

    @Override
    public int setLatoutId() {
        return R.layout.item_search;
    }

    @Override
    public AdapterHolder setAdapterHolder(View convertView, int position) {
        return new AdapterHolder(convertView);
    }

    @Override
    public void initViews(AdapterHolder holder, Object item, int position) {
        UserModel model= (UserModel) item;
        holder.setText(R.id.tv_name,"名字："+model.nickname );
        holder.setText(R.id.tv_conRemark,"备注："+model.conRemark );

    }
}
