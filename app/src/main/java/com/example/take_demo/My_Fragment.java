package com.example.take_demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.take_demo.R;
import com.example.take_demo.bean.LoginBean;
import com.example.take_demo.bean.MyBean;
import com.example.take_demo.bean.RegisterBean;
import com.example.take_demo.bean.TestBean;
import com.example.take_demo.utils.Blank_fragmentview_Adapter;
import com.example.take_demo.utils.My_fragmentview_Adapter;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;

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
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class My_Fragment extends Fragment {

    private static final String ARG_TEXT = "param2";
    private List<TestBean.DataBean.DatasBean> list = new ArrayList<>();
    private List<MyBean>list2;
    private static final String TAG = "66666";
    private ClearableCookieJar cookieJar;
    private List<Cookie> validCookies;
    private Button button;
    private Button quit;
    private Button mycollect;
    private TextView textView;
    private boolean iSload = false;




    // TODO: Rename and change types of parameters
    private String mTextString;
    private View rootView;//避免重复解析
    private RecyclerView recyclerView;


    public My_Fragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static com.example.take_demo.My_Fragment newInstance(String param2) {
        com.example.take_demo.My_Fragment fragment = new com.example.take_demo.My_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTextString = getArguments().getString(ARG_TEXT);
        }
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));
        validCookies = cookieJar.loadForRequest(HttpUrl.parse("https://www.wanandroid.com/user/login"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null ) {
            rootView = inflater.inflate(R.layout.fragment_my, container, false);
        }

        button = rootView.findViewById(R.id.btn_login);
        quit = rootView.findViewById(R.id.quit);
        mycollect = rootView.findViewById(R.id.my_collect);
        textView = rootView.findViewById(R.id.welcome);
        if( !validCookies.isEmpty()){
            button.setVisibility(View.GONE);
            quit.setVisibility(View.VISIBLE);
            textView.setText("欢迎您，亲爱的开发者:"+" "+validCookies.get(0).value());
        }else{
            button.setVisibility(View.VISIBLE);
            quit.setVisibility(View.GONE);
            textView.setText("你还没登录噢，请先登录");
        }

//        getDataAsync();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getActivity(),Login_Activity.class);
               startActivityForResult(intent, 0);

            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //创建OkHttpClient对象
                OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
                //创建Request 对象
                Request request = new Request.Builder()
                        .url("https://www.wanandroid.com/user/logout/json")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {//回调的方法执行在子线程。
                        }
                    }
                });
                cookieJar.clear();
                validCookies = cookieJar.loadForRequest(HttpUrl.parse("https://www.wanandroid.com/user/login"));
                button.setVisibility(View.VISIBLE);
                quit.setVisibility(View.GONE);
                textView.setText("你还没登录噢，请先登录");
            }
        });


        mycollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validCookies.isEmpty()){
                Intent intent = new Intent(getActivity(), MycollectActivity.class);
                startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(),"请先登录", Toast.LENGTH_SHORT).show();

                }
            }
        });


        return rootView;
    }

    public void onLoginCallback(String name)
    {
            cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));
            validCookies = cookieJar.loadForRequest(HttpUrl.parse("https://www.wanandroid.com/user/login"));
            List<Cookie> cookies = cookieJar.loadForRequest(HttpUrl.parse("https://www.wanandroid.com/user/login"));
            Log.d(TAG, "onLoginCallback: "+ cookies.isEmpty());
            button.setVisibility(View.GONE);
            quit.setVisibility(View.VISIBLE);
            textView.setText("欢迎您，亲爱的开发者:"+" "+name);
    }

    @Override
    public void onResume() {
        //这是 Fragment 从创建到显示的最后一个回调的方法
        super.onResume();
        validCookies = cookieJar.loadForRequest(HttpUrl.parse("https://www.wanandroid.com/user/login"));
    }

    //自定义装饰
    class MyDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            //设置每个item下方、左侧和右侧的间距
            outRect.bottom = 20;
            outRect.left = 18;
            outRect.right = 18;
        }
    }






}
