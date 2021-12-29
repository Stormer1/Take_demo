package com.example.take_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.take_demo.bean.LoginBean;
import com.example.take_demo.bean.TestBean;
import com.example.take_demo.utils.Blank_fragmentview_Adapter;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.youth.banner.Banner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login_Activity extends AppCompatActivity {

    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Button login;
    private Button register;
    private Button quit;
    private LoginBean loginBean2;
    private static final String TAG = "6666666";
    private Context mContext;
    private Handler handler;
    private Toolbar mToolbar;
    private TextView login_text;
    private ClearableCookieJar cookieJar;
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .init();  //必须调用方可沉浸式
        mAccount = findViewById(R.id.etAccount);
        mPwd = findViewById(R.id.etPassword);
        login = findViewById(R.id.btn_login1);
        register = findViewById(R.id.btn_register1);
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               user = mAccount.getText().toString().trim();//获取用户名
               String pwd = mPwd.getText().toString().trim();//获取密码
               Log.d(TAG, user+pwd);
               postData2(user,pwd);
            }
        });
       register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Login_Activity.this,RegisterActivity.class);
                startActivity(intent2);

            }
        });

        mToolbar = (Toolbar) this.findViewById(R.id.log_toolbar);
        mToolbar.setTitle("");
        login_text = this.findViewById(R.id.toolbarlog_title);
        this.setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login_Activity.this.finish();
            }
        });

        if (login_text != null) {

            register.setTextColor(Color.parseColor("#5A5656"));

        }

        handler = new Handler(Looper.getMainLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                LoginBean login = (LoginBean ) msg.obj;
                if(login.getData() == null) {
                    Toast.makeText(Login_Activity.this, "账号密码不匹配", Toast.LENGTH_SHORT).show();
                }
                else{

                    Toast.makeText(Login_Activity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                    intent.putExtra("name1",user);
                    setResult(RESULT_OK, intent);
                    finish();
                    }
            }
        };
    }

    private void postData2(String username,String password) {
        cookieJar.clear();
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("username",username);
        formBody.add("password",password);//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url("https://www.wanandroid.com/user/login")
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
                   loginBean2 = gson2.fromJson(response.body().string(), LoginBean.class);
                   int errorCode = loginBean2.getErrorCode();
                    Log.d("666", "获取数据成功了"+ errorCode+"");
                    Message msg = new Message();
                    msg.obj = loginBean2;
                    handler.sendMessage(msg);
                }
            }
        });
    }
}
