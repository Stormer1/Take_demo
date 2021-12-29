package com.example.take_demo;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

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
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.example.take_demo.bean.ItemBean;
import com.example.take_demo.bean.Item_detailBean;
import com.example.take_demo.bean.NaviBean;
import com.example.take_demo.utils.ItemviewAdapter;
import com.example.take_demo.utils.Second_itemAdapter;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Navidetail_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Navidetail_Fragment extends Fragment {

    private static final String CID = "param1";

    // TODO: Rename and change types of parameters
    private String mCID;
    private List<Item_detailBean.DataBean.DatasBean> list = new ArrayList<>();
    private List<Item_detailBean.DataBean.DatasBean> locallist = new ArrayList<>();
    private static final String TAG = "16688";
    private String mTextString;
    private View rootView;//避免重复解析
    private RecyclerView recyclerView;
    private ViewPager2 viewPager;
    private Second_itemAdapter adapter;
    private Item_detailBean detailBean;
    private int currentpage = 0;
    private int cid = 0;
    private ConstraintLayout neterror;
    private ConstraintLayout loading;
    ProgressBar progressBar;
    private ConstraintLayout searcherror;
    private LikeButton likeButton;
    private ItemBean itemBean;
    private ClearableCookieJar cookieJar;
    private boolean isreflash = true;
    private boolean isload =true;
    private  boolean nomore = false;

    static final int SUCCESS = 0;
    static final int FAILURE = 1;
    static final int EXCEPTION = 2;
    static final int UNFIND = 3;

    Handler handler = new Handler(Looper.getMainLooper()){

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            loading.setVisibility(View.GONE);
            switch (msg.what) {
                case SUCCESS:
                    recyclerView.setVisibility(View.VISIBLE);
                    searcherror.setVisibility(View.GONE);
                    neterror.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    break;
                case FAILURE:
                    recyclerView.setVisibility(View.GONE);
                    searcherror.setVisibility(View.GONE);
                    neterror.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    break;
                case EXCEPTION:
                    recyclerView.setVisibility(View.GONE);
                    searcherror.setVisibility(View.GONE);
                    neterror.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "网络连接出错，请检查", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    break;
                case UNFIND:
                    searcherror.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    neterror.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "现在这里暂时没有东西呢", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public Navidetail_Fragment() {
        // Required empty public constructor
    }


    public static Navidetail_Fragment newInstance(String mCID) {
        Navidetail_Fragment fragment = new Navidetail_Fragment();
        Bundle args = new Bundle();
        args.putString(CID, mCID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCID = getArguments().getString(CID);
            Log.d(TAG, mCID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_navidetail_, container, false);
        }
        recyclerView = rootView.findViewById(R.id.seconditem_view);
        searcherror = rootView.findViewById(R.id.searcherror);
        neterror = rootView.findViewById(R.id.neterror2);
        loading = rootView.findViewById(R.id.loading);
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        ChasingDots doubleBounce = new ChasingDots();
        progressBar.setIndeterminateDrawable(doubleBounce);
        getDataAsync(currentpage);
        initView();
        return rootView;
    }

    private void initView() {
        SmartRefreshLayout refreshLayout = rootView.findViewById(R.id.secondrefreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));//想换成那个快递盒子的看看，但是不行
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));
                locallist.clear();
                getDataAsync(0);
                if(isreflash){
                adapter.setList(locallist);
                refreshlayout.finishRefresh();}//传入false表示刷新失败
                else {
                    refreshlayout.finishRefresh(false);
                }
            }
        });

//        添加布局管理
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));
        adapter = new Second_itemAdapter(R.layout.second_item,locallist);
        recyclerView.setAdapter(adapter);
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
        adapter.setAnimationFirstOnly(false);
        adapter.getLoadMoreModule().setAutoLoadMore(false);//关闭自动加载


        //内容不满一页时不能开启上拉加载功能
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        //上拉监听
        refreshLayout.setOnLoadMoreListener(new com.scwang.smart.refresh.layout.listener.OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //加载数据
                getDataAsync(currentpage + 1);
                //isLoad为true加载成功，否则加载失败
                if(isload){
                    currentpage += 1;
                    //完成加载更多
                    refreshLayout.finishLoadMore(2000);
                }
                else {
                    refreshLayout.finishLoadMore(false);
                }

                if(nomore){
               refreshLayout.finishLoadMoreWithNoMoreData();
                    Toast.makeText(getContext(), "已经到底啦，没有更多了哦", Toast.LENGTH_SHORT).show();}

            }
        });

        //设置Item点击事件
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(getContext(), MainDetailActivity.class);
                intent.putExtra("data", locallist.get(position).getLink());
                Log.d("666", locallist.get(position).getLink());
                startActivity(intent);
            }
        });

        adapter.addChildClickViewIds(R.id.likeBtn);
        // 设置子控件点击监听
        adapter.setOnItemChildClickListener(new OnItemChildClickListener()  {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.likeBtn) {
                    cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));
                    List<Cookie> validCookies = cookieJar.loadForRequest(HttpUrl.parse("https://www.wanandroid.com/user/login"));
                    if(validCookies.isEmpty()){
                        Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        likeButton = view.findViewById(R.id.likeBtn);
                        if (likeButton.isLiked()) {
                            shoucang(position);
                            likeButton.setLiked(false);
                            Toast.makeText(getContext(), "取消收藏", Toast.LENGTH_SHORT).show();
                        } else {
                            unshoucang(position);
                            likeButton.setLiked(true);
                            Toast.makeText(getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }



    private void shoucang(int position) {
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
        int id = locallist.get(position).getId();
        Log.d(TAG, id+"");
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("title", locallist.get(position).getTitle());
        formBody.add("author",locallist.get(position).getAuthor());
        formBody.add("link", locallist.get(position).getLink());//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url("https://www.wanandroid.com/lg/collect/add/json")
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Gson gson2 = new Gson();
                    itemBean = gson2.fromJson(response.body().string(), ItemBean.class);
                    String errorCode = itemBean.getErrorMsg();
                    Log.d("111", "获取数据成功了"+ errorCode+"");
                }
            }
        });
    }

    private void unshoucang(int position) {
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
        int id2 = locallist.get(position).getId();
        Log.d(TAG, id2+"");
        FormBody.Builder formBody2 = new FormBody.Builder();//创建表单请求体
        Request request = new Request.Builder()//创建Request 对象。
                .url("https://www.wanandroid.com/lg/uncollect_originId/"+ id2 +"/json")
                .post(formBody2.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Gson gson2 = new Gson();
                    ItemBean loginBean2 = gson2.fromJson(response.body().string(), ItemBean.class);
                    String errorCode = loginBean2.getErrorMsg();
                    Log.d("111", "获取数据成功了"+ errorCode+"");
                }
            }
        });
    }

    public void getDataAsync(final int currentpage) {
        int curg = currentpage;
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建Request 对象
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com/article/list/"+curg+"/json?cid="+ mCID)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isreflash = false;
                isload = false;
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Gson gson = new Gson();
                    detailBean = gson.fromJson(response.body().string(), Item_detailBean.class);
                    List<Item_detailBean.DataBean.DatasBean> list = detailBean.getData().getDatas();
                    Log.d("6666", "获取数据成功了");
                    Log.d("6666", "response.code()==" + response.code());
                    Log.d("12345", currentpage+" "+ mCID );
                    if(list.isEmpty() && locallist.isEmpty()){
                        handler.sendEmptyMessage(UNFIND);
                    }
                    else if(list.isEmpty() && !locallist.isEmpty()){
                        nomore = true;
                    }
                    else {
                    isreflash = true;
                    isload = true;
                    locallist.addAll(list);
                    handler.sendEmptyMessage(SUCCESS);}
                }
            }
        });
    }
    }

