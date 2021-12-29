package com.example.take_demo.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.take_demo.R;
import com.example.take_demo.bean.MyBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class My_fragmentview_Adapter extends BaseQuickAdapter<MyBean,BaseViewHolder> {
    private static final String TAG ="888" ;
    private List<MyBean>mlist;

    public My_fragmentview_Adapter(int layoutResId, List<MyBean> list) {
        super(layoutResId,list);
        mlist = list;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MyBean myBean) {
            baseViewHolder.setText(R.id.my_text, myBean.getName());
            baseViewHolder.setImageResource(R.id.my_icon,myBean.getIconId());
            Log.d(TAG, mlist.get(1).getName());
    }

}
