package com.example.take_demo.utils;

import android.content.Context;
import android.text.Html;
import android.view.View;
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

public class Search_ResultAdapter extends BaseQuickAdapter<TestBean.DataBean.DatasBean, BaseViewHolder> implements LoadMoreModule {
    private Context mContext;
    private List<TestBean.DataBean.DatasBean> mList;
    private static final String TAG = "66666";
    private TestBean testBean;
    private Blank_fragmentview_Adapter.OnItemClickListener listener;
    private LikeButton likeButton;

    public Search_ResultAdapter(int layoutResId, List<TestBean.DataBean.DatasBean> list){
        super(layoutResId, list);

    }

    public Search_ResultAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final TestBean.DataBean.DatasBean datasBean) {
        holder.setText(R.id.item_num, Html.fromHtml(datasBean.getTitle()));
        holder.setText(R.id.auther,datasBean.getShareUser());
        holder.setText(R.id.time,datasBean.getNiceShareDate());
        holder.setText(R.id.type,datasBean.getChapterName());
        likeButton  = holder.findView(R.id.likeBtn);
        if(datasBean.getZan()!= 0){
            likeButton.setLiked(true);
        }else{
            likeButton.setLiked(false);
        }

    }



}
