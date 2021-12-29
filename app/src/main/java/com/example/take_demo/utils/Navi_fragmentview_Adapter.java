package com.example.take_demo.utils;

import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.take_demo.R;
import com.example.take_demo.bean.NaviBean;
import com.example.take_demo.bean.TestBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Navi_fragmentview_Adapter extends BaseQuickAdapter<NaviBean.DataBean, BaseViewHolder> {
    private static final String TAG = "777";
    private List<NaviBean.DataBean>mlist;
    private NaviBean.DataBean dataBean;
    public Navi_fragmentview_Adapter(int layoutResId, List<NaviBean.DataBean> list) {
        super(layoutResId,list);
        mlist = list;

    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, NaviBean.DataBean naviBean) {

        Class data = naviBean.getClass();
        baseViewHolder.setText(R.id.navi_text, naviBean.getName() );
        baseViewHolder.setImageResource(R.id.navi_image,naviBean.getIconId());
    }

}
