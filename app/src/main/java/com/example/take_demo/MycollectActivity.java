package com.example.take_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.example.take_demo.bean.ItemBean;
import com.example.take_demo.bean.TestBean;
import com.example.take_demo.utils.Blank_fragmentview_Adapter;
import com.example.take_demo.utils.MycollectAdapter;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.like.LikeButton;
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

public class MycollectActivity extends AppCompatActivity {

    private List<TestBean.DataBean.DatasBean> list = new ArrayList<>();
    private List<TestBean.DataBean.DatasBean> locallist = new ArrayList<>();
    private static final String TAG = "6666688";
    private RecyclerView recyclerView;
    private int currentpage = 0;
    private TestBean testBean;
    private MycollectAdapter adapter;
    private Toolbar mToolbar;
    private ClearableCookieJar cookieJar;
    private boolean isreflash = true;
    private boolean isload =true;
    private  boolean nomore = false;
    private LikeButton likeButton;


    static final int SUCCESS = 0;
    static final int FAILURE = 1;
    static final int EXCEPTION = 2;
    static final int SHOW_TOAST = 3;

    Handler handler = new Handler(Looper.getMainLooper()){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case SUCCESS:
                        adapter.notifyDataSetChanged();
                        break;
                    case FAILURE:
                        Toast.makeText(MycollectActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        break;
                    case EXCEPTION:
                        Toast.makeText(MycollectActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        break;
                }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //????????????????????????????????????????????????
                .navigationBarDarkIcon(true) //????????????????????????????????????????????????
                .init();  //???????????????????????????
        setContentView(R.layout.activity_mycollect);
        recyclerView = findViewById(R.id.mycollectfragment);
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
        getDataAsync(currentpage);
        initView();
    }

    private void initView() {

        SmartRefreshLayout refreshLayout = findViewById(R.id.collect_refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                locallist.clear();
                getDataAsync(0);
                if(isreflash){
                    adapter.setList(locallist);
                    refreshlayout.finishRefresh();}//??????false??????????????????
                else {
                    refreshlayout.finishRefresh(false);
                }
            }
        });
//        ??????????????????
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        ???????????????
        adapter = new MycollectAdapter(R.layout.blankfragment_item,locallist);
        adapter.isUseEmpty();
        adapter.setEmptyView(R.layout.neterror);
        recyclerView.setAdapter(adapter);
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
        adapter.setAnimationFirstOnly(false);
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

                if(nomore){
                    refreshLayout.finishLoadMoreWithNoMoreData();
                    Toast.makeText(MycollectActivity.this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();}

            }
        });

        //??????Item????????????
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(MycollectActivity.this, MainDetailActivity.class);
                intent.putExtra("data",locallist.get(position).getLink());
                Log.d("666",locallist.get(position).getLink() );
                startActivity(intent);
            }
        });



        adapter.addChildClickViewIds(R.id.likeBtn);
        // ???????????????????????????
        adapter.setOnItemChildClickListener(new OnItemChildClickListener()  {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.likeBtn) {
                    likeButton = view.findViewById(R.id.likeBtn);
                    if(likeButton.isLiked()){
                        unshoucang(position);
                        Toast.makeText(MycollectActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        likeButton.setLiked(false);
                    }else {
                        Toast.makeText(MycollectActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        shoucang(position);
                        likeButton.setLiked(true);

                    }
                }
            }
        });

    }

    private void shoucang(int position) {
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//??????OkHttpClient?????????
        int id = locallist.get(position).getOriginId();
        Log.d(TAG, id+"");
        FormBody.Builder formBody = new FormBody.Builder();//?????????????????????
        formBody.add("title", locallist.get(position).getTitle());
        formBody.add("author",locallist.get(position).getAuthor());
        formBody.add("link", locallist.get(position).getLink());//?????????????????????
        Request request = new Request.Builder()//??????Request ?????????
                .url("https://www.wanandroid.com/lg/collect/add/json")
                .post(formBody.build())//???????????????
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//????????????????????????????????????

                }
            }
        });
    }

    private void unshoucang(int position) {
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//??????OkHttpClient?????????
        int id2 = locallist.get(position).getId();
        Log.d("12345", id2+"");
        FormBody.Builder formBody2 = new FormBody.Builder();//?????????????????????
        formBody2.add("originId", locallist.get(position).getOriginId()+"");
        Request request = new Request.Builder()//??????Request ?????????
                .url("https://www.wanandroid.com/lg/uncollect/"+id2+"/json")
                .post(formBody2.build())//???????????????
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//????????????????????????????????????
                    Gson gson = new Gson();
                    testBean = gson.fromJson(response.body().string(), TestBean.class);
                    Log.d("66677", testBean.getErrorCode()+"");
                }
            }
        });
    }





    public void getDataAsync(final int currentpage) {
        int curg = currentpage;
        //??????OkHttpClient??????
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        //??????Request ??????
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com/lg/collect/list/"+ curg +"/json")
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
                if (response.isSuccessful()) {//????????????????????????????????????
                    Gson gson = new Gson();
                    testBean = gson.fromJson(response.body().string(), TestBean.class);
                    List<TestBean.DataBean.DatasBean>list = testBean.getData().getDatas();
                    Log.d("66677", "123");
                    if(list.isEmpty() && !locallist.isEmpty()){
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
