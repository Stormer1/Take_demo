package com.example.take_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.take_demo.bean.TabEntity;
import com.example.take_demo.bean.bannerBean;
import com.example.take_demo.utils.MyFragmentPagerAdapter;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.transformer.DepthPageTransformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    }

    private  ViewPager2 viewPager;
    private static final String TAG = "66666";
    private CommonTabLayout segmentTabLayout;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    ArrayList<Fragment>fragments = new ArrayList<>();
    private Fragment[] fragment;
    private bannerBean listbanner;
    private String[] mTitles = {"1","2","3"};
    private Handler handler ;
    private Banner banner;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private int lastFragemtntIndex;
    View view;
    My_Fragment my_fragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .init();  //必须调用方可沉浸式


//        initPager();
////        initTab();

        my_fragment = new My_Fragment();

        fragment = new Fragment[]{
                new BlankFragment(),
                new GroundFragment(),
                my_fragment
        };
        lastFragemtntIndex = 0;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_fra,fragment[0])
                .commit();
//        initBanner();
//        MagicIndicator magicIndicator = findViewById(R.id.book_indicator);
//        MagicIndicatorUtil.initMagicIndicator(view,this,mTitles,viewPager,magicIndicator);
        //BottomNavigationView 点击事件监听
        final BottomNavigationView navView = findViewById(R.id.nav_view2);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int menuId = menuItem.getItemId();
                // 跳转指定页面：Fragment
                switch (menuId) {
                    case R.id.navigation_home:
                        navView.getMenu().getItem(0).setChecked(true);
                        switchFragemnet(0);
                        break;
                    case R.id.navigation_dashboard:
                       switchFragemnet(1);
                        navView.getMenu().getItem(1).setChecked(true);
                        break;
                    case R.id.navigation_notifications:
                        switchFragemnet(2);
                        navView.getMenu().getItem(2).setChecked(true);
                        break;
                }
                return false;
            }
        });

    }

    private void switchFragemnet(int to) {

        if(lastFragemtntIndex == to){
            return;
        }
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        if(!fragment[to].isAdded()){
            fragmentTransaction.add(R.id.main_fra,fragment[to]);
       }else {
            fragmentTransaction.show(fragment[to]);
        }
        fragmentTransaction.hide(fragment[lastFragemtntIndex])
                .commitAllowingStateLoss();//一定要commit！
        lastFragemtntIndex = to;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            String name = data.getStringExtra("name1");
            my_fragment.onLoginCallback(name);
        }
    }

    private void initBanner() {
        getDataAsync();
        banner = (Banner) findViewById(R.id.banner);
        //uiHandler在主线程中创建，所以自动绑定主线程
        handler = new Handler(Looper.getMainLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                List<bannerBean.DataBean> list1 = (List<bannerBean.DataBean>) msg.obj;
                //初始化图片数据
                List images = new ArrayList();
                images.add(list1.get(0).getImagePath());
                images.add(list1.get(1).getImagePath());
                images.add(list1.get(2).getImagePath());
                images.add(list1.get(3).getImagePath());

                //初始化标题数据
                List titles = new ArrayList();
                titles.add(list1.get(0).getTitle());
                titles.add(list1.get(1).getTitle());
                titles.add(list1.get(2).getTitle());
                titles.add(list1.get(3).getTitle());

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
                        Toast.makeText(MainActivity.this, "position"+position, Toast.LENGTH_SHORT).show();
                    }
                });
                //banner设置方法全部调用完毕时最后调用
                banner.start();
            }
        };


    }



    private void initPager() {
        viewPager = findViewById(R.id.vp);
//        segmentTabLayout = findViewById(R.id.tb);
        fragments.add(BlankFragment.newInstance("kexie"));
        fragments.add(BlankFragment.newInstance("kexie1"));
        fragments.add(My_Fragment.newInstance("kexie2"));
        MyFragmentPagerAdapter pagerAdapter =new MyFragmentPagerAdapter(getSupportFragmentManager(),getLifecycle(),fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
//                segmentTabLayout.setCurrentTab(position);
//                BottomNavigationView navView = findViewById(R.id.nav_view2);
//                navView.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    private void getDataAsync() {
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
                   listbanner = gson.fromJson(response.body().string(), bannerBean.class);
                    List<bannerBean.DataBean>list = listbanner.getData();
                    Log.d("6666", "获取数据成功了");
                    Log.d("6666", "response.code()==" + response.code());
                    Message msg = new Message();
                    msg.obj = list;
                    handler.sendMessage(msg);
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

