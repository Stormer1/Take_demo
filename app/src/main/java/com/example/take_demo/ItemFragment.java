package com.example.take_demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toolbar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.example.take_demo.bean.ItemBean;
import com.example.take_demo.utils.ItemviewAdapter;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.FoldingCube;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ItemFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private List<ItemBean.DataBean.DatasBean> list = new ArrayList<>();
    private List<ItemBean.DataBean.DatasBean> locallist = new ArrayList<>();
    private static final String TAG = "6666688";
    private String mTextString;
    private View rootView;//避免重复解析
    private RecyclerView recyclerView;
    private int currentpage = 0;
    private ItemBean itemBean;
    private ItemviewAdapter adapter;
    private ClearableCookieJar cookieJar;
    private ConstraintLayout neterror;
    private ConstraintLayout loading;
    ProgressBar progressBar;
    private boolean isreflash = true;
    private boolean isload = true;




    private OnFragmentInteractionListener mListener;

    public ItemFragment() {
        // Required empty public constructor
    }

    public static ItemFragment newInstance(String param1) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    static final int SUCCESS = 0;
    static final int FAILURE = 1;
    static final int EXCEPTION = 2;
    static final int SHOW_TOAST = 3;

    Handler handler = new Handler(Looper.getMainLooper()){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            loading.setVisibility(View.GONE);
            switch (msg.what) {
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
        if (rootView == null ) {
            rootView = inflater.inflate(R.layout.fragment_theitem, container, false);
        }
        recyclerView = rootView.findViewById(R.id.item_fragment);
        neterror = rootView.findViewById(R.id.neterror2);
        loading = rootView.findViewById(R.id.loading);
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        FoldingCube doubleBounce = new FoldingCube();
        progressBar.setIndeterminateDrawable(doubleBounce);
        getDataAsync(currentpage);
        initView();


        return rootView;
    }

    private void initView() {
        SmartRefreshLayout refreshLayout = rootView.findViewById(R.id.item_refreshLayout);
        refreshLayout.setRefreshHeader( new ClassicsHeader(getContext()));//想换成那个快递盒子的看看，但是不行
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                recyclerView.setVisibility(View.GONE);
                neterror.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                locallist.clear();
                getDataAsync(0);
                if(isreflash){
                    adapter.setList(locallist);
                    refreshlayout.finishRefresh();}
                else {
                    refreshlayout.finishRefresh(false);//传入false表示刷新失败
                }

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
                //显示全部加载完成，并不再触发加载更多事件
//                refreshLayout.finishLoadMoreWithNoMoreData();

            }
        });



//        添加布局管理
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.addItemDecoration(newlog ItemFragment().MyDecoration());
//        添加分割线
//        recyclerView.addItemDecoration(newlog DividerItemDecoration(getContext(), VERTICAL));
        adapter = new ItemviewAdapter(R.layout.item_item,locallist);
        adapter.setEmptyView(R.layout.neterror);
        recyclerView.setAdapter(adapter);
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
        adapter.setAnimationFirstOnly(false);
//        adapter.getLoadMoreModule().setAutoLoadMore(false);//关闭自动加载
//        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                getDataAsync(currentpage + 1);
//                currentpage+=1;
//                adapter.getLoadMoreModule().loadMoreComplete();
//
//            }
//        });

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

    private void getDataAsync(int currentpage) {
        final int curg = currentpage;
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建Request 对象
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com/project/list/"+curg+"/json?cid=294")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(FAILURE);
                isreflash = false;
                isload = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Gson gson = new Gson();
                    itemBean = gson.fromJson(response.body().string(), ItemBean.class);
                    List<ItemBean.DataBean.DatasBean>list = itemBean.getData().getDatas();
                    Log.d(TAG, "onBindViewHolder: "+ list.get(0).getChapterName());
                    Log.d("6666", "获取数据成功了");
                    Log.d("6666", "response.code()==" + response.code());
                    locallist.addAll(list);
                    handler.sendEmptyMessage(SUCCESS);
                    isreflash = true;
                    isload =true;
                }
            }
        });
    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
