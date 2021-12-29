package com.example.take_demo.utils;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.take_demo.R;
import com.example.take_demo.bean.Hotkeybean;

import java.util.List;

public class SearchviewAdapter extends BaseQuickAdapter<Hotkeybean.DataBean, BaseViewHolder> {
    private Context mContext;
    private static final String TAG = "66666";
    private OnItemClickListener listener;


    public SearchviewAdapter(int layoutResId, List<Hotkeybean.DataBean> list){
        super(layoutResId, list);

    }

    public SearchviewAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final Hotkeybean.DataBean datasBean) {
        holder.setText(R.id.hotkey,datasBean.getName());

    }


}
