package com.example.take_demo;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.example.take_demo.bean.Blank;
import com.example.take_demo.bean.ItemBean;
import com.example.take_demo.bean.LocalTestBean;
import com.example.take_demo.bean.LoginBean;
import com.example.take_demo.bean.TestBean;
import com.example.take_demo.bean.TopBean;
import com.example.take_demo.bean.bannerBean;
import com.example.take_demo.utils.Blank_fragmentview_Adapter;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.gson.Gson;
import com.like.IconType;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.header.FalsifyHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.loader.ImageLoaderInterface;
import com.youth.banner.transformer.DepthPageTransformer;

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

import static androidx.recyclerview.widget.DividerItemDecoration.*;



public class BlankFragment extends Fragment {

    public class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    }

    private static final String ARG_TEXT = "param1";
    private List<TestBean.DataBean.DatasBean> toplist = new ArrayList<>();
    private List<TestBean.DataBean.DatasBean> locallist = new ArrayList<>();
    private static final String TAG = "66666882";
    private TestBean testBean;
    private TopBean topBean;
    private String mTextString;
    private View rootView;//避免重复解析
    private RecyclerView recyclerView;
    private Banner banner;
    private bannerBean bannerBean;
    private boolean isErr = true;
    private Blank_fragmentview_Adapter adapter;
    private int currentpage = 0;
    private ViewPager2 viewPager;
    private Toolbar mToolbar;
    private TextView blank_text;
    private LikeButton likeButton;
    private MaterialSearchView searchView;
    private ItemBean loginBean;
    private ClearableCookieJar cookieJar;
    private ConstraintLayout searcherror;
    private ConstraintLayout neterror;
    private ConstraintLayout loading;
    ProgressBar progressBar;
    private boolean isreflash = true;
    private boolean isload = true;

    static final int SUCCESS = 0;
    static final int FAILURE = 1;
    static final int EXCEPTION = 2;
    static final int COLLECT = 3;
    static final int UNCOLLECT = 4;


    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance(String param1) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            loading.setVisibility(View.GONE);

            switch (msg.what){

                case -1:
                    initBanner((List<bannerBean.DataBean>) msg.obj);
                    break;

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
            mTextString = getArguments().getString(ARG_TEXT);
        }
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        if (rootView == null ) {
            rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        }
        mToolbar = (Toolbar) rootView.findViewById(R.id.blank_toolbar);
        mToolbar.setTitle("");

        mToolbar.inflateMenu(R.menu.search_menu);
        blank_text = rootView.findViewById(R.id.blanktoolbar_title);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        recyclerView = rootView.findViewById(R.id.fragment_view);
        neterror = rootView.findViewById(R.id.neterror2);
        loading = rootView.findViewById(R.id.loading);
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        ChasingDots doubleBounce = new ChasingDots();
        progressBar.setIndeterminateDrawable(doubleBounce);
        getbannerDataAsync();
        getopDataAsync();
        getDataAsync(currentpage);
        initView();

        return rootView;
    }

    private void getopDataAsync() {

        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        //创建Request 对象
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com/article/top/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isreflash = false;
                isload =false;
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Gson gson = new Gson();
                    topBean = gson.fromJson(response.body().string(), TopBean.class);
                    List<TopBean.DataBean>list = topBean.getData();
                    Log.d("6666", "获取数据成功了");
                    Log.d("6666", "response.code()==" + response.code());

                    for(int i = 0 ; i < list.size() ; i++ ){
                        TestBean.DataBean.DatasBean datasBean = new TestBean.DataBean.DatasBean();
                        datasBean.setTitle(list.get(i).getTitle());
                        datasBean.setShareUser(list.get(i).getShareUser());
                        datasBean.setNiceShareDate(list.get(i).getNiceShareDate());
                        datasBean.setChapterName(list.get(i).getChapterName());
                        datasBean.setLink(list.get(i).getLink());
                        datasBean.setId(list.get(i).getId());
                        datasBean.setAuthor(list.get(i).getAuthor());
                        datasBean.setNewtag(1);
                        datasBean.setCollect(list.get(i).isCollect());
                        datasBean.setFresh(list.get(i).isFresh());
                        locallist.add(datasBean);
                    }

                    handler.sendEmptyMessage(SUCCESS);
                    isreflash = true;
                    isload =true;
                }
            }
        });
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(getContext(),SearchActivity.class);
                startActivity(intent);
        }
        return true;
    }

    private void initBanner(final List<bannerBean.DataBean> list1) {

        banner = (Banner) rootView.findViewById(R.id.banner2);
        List<bannerBean.DataBean> list = list1;
                //初始化图片数据
                List images = new ArrayList();
                images.add(list1.get(0).getImagePath());
                images.add(list1.get(1).getImagePath());
                images.add(list1.get(2).getImagePath());


                //初始化标题数据
                List titles = new ArrayList();
                titles.add(list1.get(0).getTitle());
                titles.add(list1.get(1).getTitle());
                titles.add(list1.get(2).getTitle());


                Log.d(TAG, list1.get(0).getTitle() + "   " + list1.get(0).getImagePath() );

                //设置图片加载器
                banner.setImageLoader(new MyImageLoader());
                //设置图片集合
                banner.setImages(images);
                //设置标题集合（当banner样式有显示title时）
                banner.setBannerTitles(titles);
                //设置轮播的动画效果
                banner.setPageTransformer(true,new DepthPageTransformer());
                //设置自动轮播，默认为true
                banner.isAutoPlay(true);
                //设置轮播时间（设置2.5秒切换下一张图片）
                banner.setDelayTime(2500);
                //设置banner显示样式（带标题的样式）
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
                //设置指示器位置（当banner模式中有指示器时）

                //增加监听事件
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Intent intent = new Intent(getActivity(), MainDetailActivity.class);
                        intent.putExtra("data",list1.get(position).getUrl());
                        startActivity(intent);
                        Toast.makeText(getContext(), "position"+position, Toast.LENGTH_SHORT).show();
                    }
                });
                //banner设置方法全部调用完毕时最后调用
                banner.start();

            }





    private void initView() {

        SmartRefreshLayout refreshLayout = rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader( new ClassicsHeader(getContext()));//想换成那个快递盒子的看看，但是不行
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));
                recyclerView.setVisibility(View.GONE);
                neterror.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                locallist.clear();
                getopDataAsync();
                getDataAsync(0);
                getbannerDataAsync();
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
        recyclerView.addItemDecoration(new MyDecoration());

        adapter = new Blank_fragmentview_Adapter(R.layout.blankfragment_item, locallist);
        adapter.setEmptyView(R.layout.neterror);
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


            }
        });


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

        adapter.addChildClickViewIds(R.id.likeBtn);
        // 设置子控件点击监听
        adapter.setOnItemChildClickListener(new OnItemChildClickListener()  {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.likeBtn) {
                    likeButton = view.findViewById(R.id.likeBtn);
                    cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));
                    List<Cookie> validCookies = cookieJar.loadForRequest(HttpUrl.parse("https://www.wanandroid.com/user/login"));
                   if(validCookies.isEmpty()){
                        Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                   }else {
                        if (likeButton.isLiked()) {
                            unshoucang(position);
                            Toast.makeText(getContext(), "取消收藏", Toast.LENGTH_SHORT).show();
                            likeButton.setLiked(false);
                        } else {
                            Toast.makeText(getContext(), "成功收藏", Toast.LENGTH_SHORT).show();
                            shoucang(position);
                            likeButton.setLiked(true);
                        }
                    }
                }
            }
        });

    }



    private void shoucang(int position) {
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
        int id = locallist.get(position).getId();
        Log.d(TAG, "id:    "+id+"");
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
//        formBody.add("title", locallist.get(position).getTitle());
//        formBody.add("author",locallist.get(position).getAuthor());
//        formBody.add("link", locallist.get(position).getLink());//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url("https://www.wanandroid.com/lg/collect/"+locallist.get(position).getId()+"/json")
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。

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
                .url("https://www.wanandroid.com/lg/uncollect_originId/"+locallist.get(position).getId()+"/json")
                .post(formBody2.build())//传递请求体
                .build();
        Log.d("1112", locallist.get(position).getUserId()+"");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Gson gson2 = new Gson();
                    ItemBean loginBean2 = gson2.fromJson(response.body().string(), ItemBean.class);
                    String errorCode = loginBean2.getErrorMsg();
                    Log.d("11123", "获取数据成功了"+loginBean2.getErrorMsg());
                }
            }
        });
    }



    //自定义装饰
    class MyDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            //设置每个item下方、左侧和右侧的间距
            outRect.bottom = 8;
            outRect.left = 10;
            outRect.right = 10;
        }
    }

    private void getbannerDataAsync() {
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建Request 对象
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com/banner/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Gson gson = new Gson();
                    bannerBean = gson.fromJson(response.body().string(), bannerBean.class);
                    List<bannerBean.DataBean>listbanner = bannerBean.getData();
                    Log.d("6666", "获取数据成功了");
                    Log.d("6666", "response.code()==" + response.code());
                    Message msg2 = new Message();
                    msg2.what = -1;
                    msg2.obj = listbanner;
                    handler.sendMessage(msg2);
                }
            }
        });
    }



    public void getDataAsync(final int currentpage) {
        final int curg = currentpage;
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        //创建Request 对象
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com/article/list/"+curg+"/json")
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
                    testBean = gson.fromJson(response.body().string(), TestBean.class);
                    List<TestBean.DataBean.DatasBean>list = testBean.getData().getDatas();
                    Log.d("6666", "获取数据成功了");
                    Log.d("6666", "response.code()==" + response.code());
                    Log.d("12345", currentpage+"");
                    locallist.addAll(list);
                    handler.sendEmptyMessage(SUCCESS);
                    isreflash = true;
                    isload =true;
                }
            }
        });
    }

}
