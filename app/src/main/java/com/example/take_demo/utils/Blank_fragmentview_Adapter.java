package com.example.take_demo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.take_demo.Login_Activity;
import com.example.take_demo.bean.Blank;
import com.example.take_demo.R;
import com.example.take_demo.bean.TestBean;
import com.google.gson.Gson;
import com.like.LikeButton;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Blank_fragmentview_Adapter extends BaseQuickAdapter<TestBean.DataBean.DatasBean, BaseViewHolder>implements LoadMoreModule {
    private Context mContext;
    private List<TestBean.DataBean.DatasBean> mList;
    private static final String TAG = "661";
    private TestBean testBean;
    private OnItemClickListener listener;
    private LikeButton likeButton;
    private ImageView imageView;
    private ImageView settop;

    public interface OnItemClickListener{
        /*注意参数*/
        public void OnItemClick(View v,int position);
    }
    public void setOnItemClick(OnItemClickListener listener){
        this.listener = listener;
    }


    public Blank_fragmentview_Adapter(int layoutResId, List<TestBean.DataBean.DatasBean> list){
        super(layoutResId, list);

    }

    public Blank_fragmentview_Adapter(int layoutResId) {
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
        if(datasBean.isCollect()){
            likeButton.setLiked(true);
        }else{
            likeButton.setLiked(false);
        }

    }


//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.blankfragment_item,viewGroup,false);
//        ViewHolder viewHolder = newlog ViewHolder(view);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
////        List<TestBean.DataBean.DatasBean>list = testBean.getData().getDatas();
////        Log.d(TAG, "onBindViewHolder: "+ list.get(0).getChapterName());
////        getDataAsync();
//        Log.d(TAG, mList.get(0).getChapterName());
//        Log.d(TAG, mList.size()+"");
//        viewHolder.mTextView.setText(mList.get(position).getTitle());
//        viewHolder.mTextView2.setText( "作者: " + mList.get(position).getShareUser());
//        viewHolder.mTextView3.setText( "时间: " + mList.get(position).getNiceShareDate());
//        viewHolder.mtextView4.setText( "分类: " + mList.get(position).getChapterName());
//        viewHolder.mView.setOnClickListener(newlog View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int pos = viewHolder.getLayoutPosition();
//                Toast.makeText(mContext,mList.get(pos).getChapterName(),Toast.LENGTH_SHORT).show();
//                listener.OnItemClick(v,position);
//            }
//        });
//    }



//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }



    class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView mTextView;
        TextView mTextView2;
        TextView mTextView3;
        TextView mtextView4;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mTextView = (TextView)itemView.findViewById(R.id.item_num);
            mTextView2 = itemView.findViewById(R.id.auther);
            mTextView3  =  itemView.findViewById(R.id.time);
            mtextView4 = itemView.findViewById(R.id.type);
        }
    }



}
