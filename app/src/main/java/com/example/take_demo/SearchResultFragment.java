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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.example.take_demo.bean.Blank;
import com.example.take_demo.bean.TestBean;
import com.example.take_demo.utils.Blank_fragmentview_Adapter;
import com.example.take_demo.utils.Search_ResultAdapter;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.gson.Gson;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SearchResultFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mKey;
    private List<TestBean.DataBean.DatasBean> list = new ArrayList<>();
    private List<TestBean.DataBean.DatasBean> locallist = new ArrayList<>();
    private static final String TAG = "6666688";
    private Handler handler2;
    private String mTextString;
    private View rootView;//避免重复解析
    private RecyclerView recyclerView;
    private int currentpage = 0;
    private TestBean resultbean;
    private Search_ResultAdapter adapter;
    private ConstraintLayout searcherror;
    private ConstraintLayout neterror;
    private ConstraintLayout loading;
    ProgressBar progressBar;
    private  boolean isload;


    static final int SUCCESS = 0;
    static final int FAILURE = 1;
    static final int EXCEPTION = 2;
    static final int SHOW_TOAST = 3;
    static final int UNFIND = 4;


    public SearchResultFragment() {
        // Required empty public constructor
    }


    public static SearchResultFragment newInstance(String param1) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    //uiHandler在主线程中创建，所以自动绑定主线程
     Handler handler = new Handler(Looper.getMainLooper()){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            loading .setVisibility(View.GONE);
            switch (msg.what){
                case SUCCESS:
                    recyclerView.setVisibility(View.VISIBLE);
                    searcherror.setVisibility(View.GONE);
                    neterror.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    break;
                case FAILURE:
                    searcherror.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    neterror.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    break;
                case EXCEPTION:
                    searcherror.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    neterror.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "网络连接出错，请检查", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    break;
                case UNFIND:
                    searcherror.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    neterror.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "没找到你想要的呢，要不换一个？", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mKey = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null ) {
            rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        }
        recyclerView = rootView.findViewById(R.id.search_view);
        searcherror = rootView.findViewById(R.id.searcherror);
        neterror = rootView.findViewById(R.id.neterror2);
        loading = rootView.findViewById(R.id.loading);
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        CubeGrid doubleBounce = new CubeGrid();
        progressBar.setIndeterminateDrawable(doubleBounce);
        getDataAsync(currentpage);
        initView();
        return rootView;
    }

    private void initView() {


        SmartRefreshLayout refreshLayout = rootView.findViewById(R.id.search_refreshLayout);
        refreshLayout.setRefreshHeader( new ClassicsHeader(getContext()));//想换成那个快递盒子的看看，但是不行
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                locallist.clear();
                getDataAsync(0);
                adapter.setList(locallist);
                refreshlayout.finishRefresh();//传入false表示刷新失败

            }
        });

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


            }
        });

//        添加布局管理
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        添加分割线
        adapter = new Search_ResultAdapter(R.layout.blankfragment_item ,locallist);
        recyclerView.setAdapter(adapter);
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
        adapter.setAnimationFirstOnly(false);
        adapter.getLoadMoreModule().setAutoLoadMore(false);//关闭自动加载


        //设置Item点击事件
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(getActivity(), MainDetailActivity.class);
                intent.putExtra("data",locallist.get(position).getLink());
                Log.d("666",locallist.get(position).getLink() );
                startActivity(intent);
            }
        });
    }

    public void getDataAsync(final int currentpage) {
        int curg = currentpage;
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建Request 对象
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("k",mKey);//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url("https://www.wanandroid.com/article/query/"+curg+"/json")
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isload =false;
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Gson gson = new Gson();
                    resultbean = gson.fromJson(response.body().string(), TestBean.class);
                    List<TestBean.DataBean.DatasBean>list = resultbean.getData().getDatas();
                    Log.d("6666", "response.code()==" + response.code());
                    Log.d("12345", currentpage+"");
                    if( list.isEmpty()){
                        handler.sendEmptyMessage(UNFIND);
                    }
                    else{
                        isload = true;
                        locallist.addAll(list);
                        handler.sendEmptyMessage(SUCCESS);
                    }//  29

                }
            }
        });
    }
}
