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
    private View rootView;//??????????????????
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

    //uiHandler???????????????????????????????????????????????????
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
                    Toast.makeText(getContext(), "??????????????????????????????", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    break;
                case EXCEPTION:
                    searcherror.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    neterror.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "??????????????????????????????", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    break;
                case UNFIND:
                    searcherror.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    neterror.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
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
        refreshLayout.setRefreshHeader( new ClassicsHeader(getContext()));//???????????????????????????????????????????????????
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                locallist.clear();
                getDataAsync(0);
                adapter.setList(locallist);
                refreshlayout.finishRefresh();//??????false??????????????????

            }
        });

        //???????????????????????????????????????????????????
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        //????????????
        refreshLayout.setOnLoadMoreListener(new com.scwang.smart.refresh.layout.listener.OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //????????????
                getDataAsync(currentpage + 1);
                //isLoad???true?????????????????????????????????
                if(isload){
                    currentpage += 1;
                    //??????????????????
                    refreshLayout.finishLoadMore(2000);
                }
                else {
                    refreshLayout.finishLoadMore(false);
                }


            }
        });

//        ??????????????????
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        ???????????????
        adapter = new Search_ResultAdapter(R.layout.blankfragment_item ,locallist);
        recyclerView.setAdapter(adapter);
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
        adapter.setAnimationFirstOnly(false);
        adapter.getLoadMoreModule().setAutoLoadMore(false);//??????????????????


        //??????Item????????????
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
        //??????OkHttpClient??????
        OkHttpClient client = new OkHttpClient();
        //??????Request ??????
        FormBody.Builder formBody = new FormBody.Builder();//?????????????????????
        formBody.add("k",mKey);//?????????????????????
        Request request = new Request.Builder()//??????Request ?????????
                .url("https://www.wanandroid.com/article/query/"+curg+"/json")
                .post(formBody.build())//???????????????
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isload =false;
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//????????????????????????????????????
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
