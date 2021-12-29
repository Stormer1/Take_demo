package com.example.take_demo;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.take_demo.bean.Hotkeybean;
import com.example.take_demo.bean.bannerBean;
import com.example.take_demo.utils.SearchviewAdapter;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FirstsearchFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private View rootView;
    private List<Hotkeybean.DataBean> list = new ArrayList<>();
    private List<Hotkeybean.DataBean> locallist;
    private static final String TAG = "6677";
    private RecyclerView recyclerView;
    private RecyclerView hisrecyclerView;
    private SearchviewAdapter adapter;
    private Hotkeybean hotkeybean;
    private ConstraintLayout neterror;
    private ConstraintLayout loading;
    ProgressBar progressBar;

    static final int SUCCESS = 0;
    static final int FAILURE = 1;
    static final int EXCEPTION = 2;
    static final int SHOW_TOAST = 3;




    public FirstsearchFragment() {
        // Required empty public constructor
    }


    public static FirstsearchFragment newInstance(String param1) {
        FirstsearchFragment fragment = new FirstsearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
                loading.setVisibility(View.GONE);
                switch (msg.what){
                    case SUCCESS:
                        recyclerView.setVisibility(View.VISIBLE);
                        neterror.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        break;
                    case FAILURE:
                        recyclerView.setVisibility(View.GONE);
                        neterror.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        break;
                    case EXCEPTION:
                        recyclerView.setVisibility(View.GONE);
                        neterror.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "网络连接出错，请检查", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null ) {
            rootView = inflater.inflate(R.layout.fragment_firstsearch, container, false);
        }

        recyclerView = rootView.findViewById(R.id.hot_view);
        neterror = rootView.findViewById(R.id.neterror2);
        loading = rootView.findViewById(R.id.loading);
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        Wave doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);
        locallist = new ArrayList<>();
        getDataAsync();
        initView();
        return rootView;
    }

    private void initView() {

//        //设置主轴方向为横轴
//        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext(), FlexDirection.ROW);
//        //设置item沿主轴方向的位置
//        manager.setJustifyContent(JustifyContent.CENTER);
//        //设置item 沿次轴方向的位置
//        manager.setAlignItems(AlignItems.CENTER);


//        添加布局管理
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new SearchviewAdapter(R.layout.search_item,locallist);
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
        adapter.setAnimationFirstOnly(false);
        recyclerView.setAdapter(adapter);
        //设置Item点击事件
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
               getFragmentManager().beginTransaction()
                        .replace(R.id.fra_search, SearchResultFragment.newInstance(locallist.get(position).getName()))
                        .commit();
            }
        });


    }

    private void getDataAsync() {
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建Request 对象
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com//hotkey/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Gson gson = new Gson();
                    hotkeybean = gson.fromJson(response.body().string(), Hotkeybean.class);
                    List<Hotkeybean.DataBean>hotlist = hotkeybean.getData();
                    Log.d("6677", hotlist.get(0).getName());
                    Log.d("6677", "response.code()==" + response.code());
                    locallist.addAll(hotlist);
                    handler.sendEmptyMessage(SUCCESS);
                }
            }
        });
    }
}
