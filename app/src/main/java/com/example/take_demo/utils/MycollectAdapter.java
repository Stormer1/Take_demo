package com.example.take_demo.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.take_demo.R;
import com.example.take_demo.bean.TestBean;
import com.like.LikeButton;

import java.util.List;

public class MycollectAdapter extends BaseQuickAdapter<TestBean.DataBean.DatasBean, BaseViewHolder>  {
    private Context mContext;
    private List<TestBean.DataBean.DatasBean> mList;
    private static final String TAG = "661";
    private TestBean testBean;
    private LikeButton likeButton;
    private ImageView imageView;
    private ImageView settop;


    public MycollectAdapter(int layoutResId, List<TestBean.DataBean.DatasBean> list){
        super(layoutResId, list);

    }

    public MycollectAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final TestBean.DataBean.DatasBean datasBean) {
        holder.setText(R.id.item_num,datasBean.getTitle());
        holder.setText(R.id.auther,datasBean.getShareUser());
        holder.setText(R.id.time,datasBean.getNiceShareDate());
        holder.setText(R.id.type,datasBean.getChapterName());
        imageView = holder.findView(R.id.new1);
        if(datasBean.isFresh() ){
            Log.d(TAG, datasBean.getTitle());
            imageView.setVisibility(View.VISIBLE);
        }else {
            imageView.setVisibility(View.GONE);
        }

        settop = holder.findView(R.id.setop);
        if(datasBean.getNewtag() == 1 ){
            Log.d(TAG, datasBean.getChapterName());
            settop.setVisibility(View.VISIBLE);
        }else {
            settop.setVisibility(View.GONE);
        }

        likeButton  = holder.findView(R.id.likeBtn);
            likeButton.setLiked(true);

    }


}

