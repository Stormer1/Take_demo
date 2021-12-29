package com.example.take_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.example.take_demo.bean.NaviBean;
import com.example.take_demo.bean.TabEntity;
import com.example.take_demo.bean.TestBean;
import com.example.take_demo.bean.bannerBean;
import com.example.take_demo.utils.Blank_fragmentview_Adapter;
import com.example.take_demo.utils.MyFragmentPagerAdapter;
import com.example.take_demo.utils.Second_itemAdapter;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SeconditemActivity extends AppCompatActivity {


    private List<NaviBean.DataBean> list = new ArrayList<>();
    private List<NaviBean.DataBean> locallist = new ArrayList<>();
    private static final String TAG = "6688";
    private Handler handler;
    NaviBean naviBean;
    private String[] mTitles;
    private TextView mTextview;
    private int currentpage = 0;
    private ViewPager2 viewPager;
    private Toolbar mToolbar;
    private ArrayList<CustomTabEntity> mTabEntitie = new ArrayList<>();
    ArrayList<Fragment>fragments = new ArrayList<>();
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private View view;
    private String msg;
    private String name;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .init();  //必须调用方可沉浸式
        setContentView(R.layout.activity_seconditem);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        msg = intent.getStringExtra("position");
        name = intent.getStringExtra("Name");
        try {
            position = Integer.parseInt(msg);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        list = (List<NaviBean.DataBean>) bundle.getSerializable("detail");
        mTitles = new String[list.get(position).getChildren().size()];
        for(int i = 0 ;i<list.get(position).getChildren().size();i++){
            mTitles[i] =  list.get(position).getChildren().get(i).getName();
            Log.d(TAG, mTitles[i]);
            Log.d(TAG, list.get(position).getChildren().get(i).getName());
        }
        mTextview = (TextView) this.findViewById(R.id.secondtoolbar_title);
        mTextview.setText(name);
        initPager();
//        initTab(list);
        MagicIndicator magicIndicator2 = findViewById(R.id.second_indicator2);
        MagicIndicatorUtil.initMagicIndicator(view,this,mTitles,viewPager,magicIndicator2,1);
    }

    private void initPager() {
        viewPager = findViewById(R.id.item_vp);
        for(int i = 0; i < mTitles.length ;i++){
        fragments.add(Navidetail_Fragment.newInstance(list.get(position).getChildren().get(i).getId()+""));}
        viewPager.setOffscreenPageLimit(1);
        MyFragmentPagerAdapter pagerAdapter2 =new MyFragmentPagerAdapter(getSupportFragmentManager(),getLifecycle(),fragments);
        viewPager.setAdapter(pagerAdapter2);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

            }
        });
    }


}