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
                .statusBarDarkFont(true)   //????????????????????????????????????????????????
                .navigationBarDarkIcon(true) //????????????????????????????????????????????????
                .init();  //???????????????????????????


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
        //BottomNavigationView ??????????????????
        final BottomNavigationView navView = findViewById(R.id.nav_view2);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int menuId = menuItem.getItemId();
                // ?????????????????????Fragment
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
                .commitAllowingStateLoss();//?????????commit???
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
        //uiHandler???????????????????????????????????????????????????
        handler = new Handler(Looper.getMainLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                List<bannerBean.DataBean> list1 = (List<bannerBean.DataBean>) msg.obj;
                //?????????????????????
                List images = new ArrayList();
                images.add(list1.get(0).getImagePath());
                images.add(list1.get(1).getImagePath());
                images.add(list1.get(2).getImagePath());
                images.add(list1.get(3).getImagePath());

                //?????????????????????
                List titles = new ArrayList();
                titles.add(list1.get(0).getTitle());
                titles.add(list1.get(1).getTitle());
                titles.add(list1.get(2).getTitle());
                titles.add(list1.get(3).getTitle());

                Log.d(TAG, list1.get(0).getTitle() + "   " + list1.get(0).getImagePath() );

                //?????????????????????
                banner.setImageLoader(new MyImageLoader());
                //??????????????????
                banner.setImages(images);
                //????????????????????????banner???????????????title??????
                banner.setBannerTitles(titles);
                //???????????????????????????
                banner.setPageTransformer(true,new DepthPageTransformer());
                //??????????????????????????????true
                banner.isAutoPlay(true);
                //???????????????????????????2.5???????????????????????????
                banner.setDelayTime(2500);
                //??????banner????????????????????????????????????
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
                //???????????????????????????banner???????????????????????????

                //??????????????????
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Toast.makeText(MainActivity.this, "position"+position, Toast.LENGTH_SHORT).show();
                    }
                });
                //banner?????????????????????????????????????????????
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
        //??????OkHttpClient??????
        OkHttpClient client = new OkHttpClient();
        //??????Request ??????
        Request request = new Request.Builder()
                .url("https://www.wanandroid.com/banner/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//????????????????????????????????????
                    Gson gson = new Gson();
                   listbanner = gson.fromJson(response.body().string(), bannerBean.class);
                    List<bannerBean.DataBean>list = listbanner.getData();
                    Log.d("6666", "?????????????????????");
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

