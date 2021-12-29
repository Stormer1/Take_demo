package com.example.take_demo.utils;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.take_demo.R;
import com.example.take_demo.bean.Item_detailBean;
import com.example.take_demo.bean.NaviBean;

import java.util.List;

public class Second_itemAdapter extends BaseQuickAdapter<Item_detailBean.DataBean.DatasBean, BaseViewHolder> implements LoadMoreModule {
    public Second_itemAdapter(int layoutResId, List<Item_detailBean.DataBean.DatasBean> list){
        super(layoutResId, list);

    }

    public Second_itemAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final Item_detailBean.DataBean.DatasBean datasBean) {
        holder.setText(R.id.seconditem_num,datasBean.getTitle());
        holder.setText(R.id.secondauther,datasBean.getShareUser());
        holder.setText(R.id.secondtime,datasBean.getNiceShareDate());
        holder.setText(R.id.secondtype,datasBean.getChapterName());
    }
}
