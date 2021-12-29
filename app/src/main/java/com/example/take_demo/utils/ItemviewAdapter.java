package com.example.take_demo.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.take_demo.R;
import com.example.take_demo.bean.ItemBean;

import java.util.List;

public class ItemviewAdapter extends BaseQuickAdapter<ItemBean.DataBean.DatasBean, BaseViewHolder> implements LoadMoreModule {

    public ItemviewAdapter(int layoutResId, List<ItemBean.DataBean.DatasBean> list){
        super(layoutResId, list);

    }

    public ItemviewAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final ItemBean.DataBean.DatasBean datasBean) {
        holder.setText(R.id.item_item_num,datasBean.getTitle());
        holder.setText(R.id.item_auther,datasBean.getShareUser());
        holder.setText(R.id.item_time,datasBean.getNiceShareDate());
        holder.setText(R.id.item_type,datasBean.getChapterName());
        // 设置图片
        Glide.with(getContext()).load(datasBean.getEnvelopePic()).into((ImageView) holder.getView(R.id.item_image));
    }
}
