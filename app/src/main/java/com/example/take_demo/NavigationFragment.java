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
import com.example.take_demo.bean.NaviBean;
import com.example.take_demo.utils.Navi_fragmentview_Adapter;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NavigationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "99999";
    private String mParam1;
    private View rootView;
    private RecyclerView recyclerView;
    private Navi_fragmentview_Adapter adapter;
    private List<NaviBean.DataBean> naviBeanlist;
    private List<NaviBean.DataBean> localnaviBeanlist = new ArrayList<>();
    private NaviBean naviBean;
    private ConstraintLayout neterror;
    private ConstraintLayout loading;
    private ProgressBar progressBar;

    static final int SUCCESS = 0;
    static final int FAILURE = 1;
    static final int EXCEPTION = 2;
    static final int SHOW_TOAST = 3;


    public NavigationFragment() {
        // Required empty public constructor
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            loading.setVisibility(View.GONE);

            switch (msg.what){

                case SUCCESS:
                    initNavi(localnaviBeanlist);
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

    // TODO: Rename and change types and number of parameters
    public static NavigationFragment newInstance(String param1) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        naviBeanlist =new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null ) {
            rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        }
        recyclerView = rootView.findViewById(R.id.fragment_navi);
        neterror = rootView.findViewById(R.id.neterror2);
        loading = rootView.findViewById(R.id.loading);
        progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
        Wave doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);
        getnaviDataAsync();
        initView();
        return rootView;
    }

    private void initView() {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new MyDecoration());
        adapter = new Navi_fragmentview_Adapter(R.layout.navifragment_item,localnaviBeanlist);
        adapter.setAnimationFirstOnly(false);
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInLeft);
        recyclerView.setAdapter(adapter);
        //设置Item点击事件
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(getActivity(), SeconditemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("detail", (Serializable) localnaviBeanlist);
                intent.putExtra("position",position+"");
                intent.putExtra("Name", localnaviBeanlist.get(position).getName());
                Log.d("666",position+"" );
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void initNavi(List<NaviBean.DataBean> list1) {
        list1.get(0).setIconId(R.drawable.kaifahuanjing);
        list1.get(1).setIconId(R.drawable.jichuzhishi);
        list1.get(2).setIconId(R.drawable.zujian);
        list1.get(3).setIconId(R.drawable.kongjian);
        list1.get(4).setIconId(R.drawable.jiaohu);
        list1.get(5).setIconId(R.drawable.shujuchuanshu);
        list1.get(6).setIconId(R.drawable.jiazaitupian);
        list1.get(7).setIconId(R.drawable.shujuku);
        list1.get(8).setIconId(R.drawable.donghua);
        list1.get(9).setIconId(R.drawable.zidingyi);
        list1.get(10).setIconId(R.drawable.duomeiti);
        list1.get(11).setIconId(R.drawable.gaoxin);
        list1.get(12).setIconId(R.drawable.hot);
        list1.get(13).setIconId(R.drawable.yingjian);
        list1.get(14).setIconId(R.drawable.jni);
        list1.get(15).setIconId(R.drawable.sdk);
        list1.get(16).setIconId(R.drawable.framework);
        list1.get(17).setIconId(R.drawable.xiangmubibei);
        list1.get(18).setIconId(R.drawable.tuijianwangzhan);
        list1.get(19).setIconId(R.drawable.yanshenjishu);
        list1.get(20).setIconId(R.drawable.zhujie);
        list1.get(21).setIconId(R.drawable.kotlin);
        list1.get(22).setIconId(R.drawable.java);
        list1.get(23).setIconId(R.drawable.ganhuoziyuan);
        list1.get(24).setIconId(R.drawable.kaiyuanxiangmu);
        list1.get(25).setIconId(R.drawable.daohangtab);
        list1.get(26).setIconId(R.drawable.kaiyuanxiangmutab);
        list1.get(27).setIconId(R.drawable.yuancuang);
        list1.get(28).setIconId(R.drawable.tv3);
        list1.get(29).setIconId(R.drawable.api);
        list1.get(30).setIconId(R.drawable.kuapingtai);
        list1.get(31).setIconId(R.drawable.neitui);
        list1.get(32).setIconId(R.drawable.xiangmuguanli);
        list1.get(33).setIconId(R.drawable.kaiyuanjiexi);//
        list1.remove(34);
        list1.get(34).setIconId(R.drawable.gongzhonghao);
        list1.get(35).setIconId(R.drawable.jetpack);
        list1.get(36).setIconId(R.drawable.qa);
        list1.get(37).setIconId(R.drawable.shiping);
        list1.get(38).setIconId(R.drawable.banbenshipei);
        list1.get(39).setIconId(R.drawable.linux);
        list1.get(40).setIconId(R.drawable.changjianjiexi);
        list1.remove(41);
        list1.remove(41);
        list1.remove(41);
        list1.remove(41);
        list1.remove(41);
        list1.remove(41);
        list1.remove(41);







    }


    private void getnaviDataAsync() {
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建Request 对象
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com/tree/json")
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
                    naviBean = gson.fromJson(response.body().string(), NaviBean.class);
                    List<NaviBean.DataBean>listnavi = naviBean.getData();
                    Log.d("6666", "获取数据成功了");
                    Log.d("6666", "response.code()==" + response.code());
                    localnaviBeanlist.addAll(listnavi);
                    handler.sendEmptyMessage(SUCCESS);
                }
            }
        });
    }

    class MyDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            //设置每个item下方、左侧和右侧的间距
            outRect.bottom = 40;
            outRect.left = 25;
            outRect.right = 25;
        }
    }

}
