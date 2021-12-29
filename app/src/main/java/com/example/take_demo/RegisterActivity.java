package com.example.take_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.take_demo.bean.LoginBean;
import com.example.take_demo.bean.RegisterBean;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {



    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private EditText msurepwd;
    private Button register;
    private RegisterBean registerBean;
    private static final String TAG = "6666666";
    private Handler handler;
    private Toolbar mToolbar;
    private TextView regtext;
    private ClearableCookieJar cookieJar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .init();  //必须调用方可沉浸式
        setContentView(R.layout.activity_register);
        mAccount = findViewById(R.id.etAccount2);
        mPwd = findViewById(R.id.etPassword2);
        register = findViewById(R.id.btn_register1);
        msurepwd = findViewById(R.id.ensure_etPassword);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mAccount.getText().toString().trim();//获取用户名
                String pwd = mPwd.getText().toString().trim();//获取密码
                String ensurepwd = msurepwd.getText().toString().trim();
                Log.d(TAG, user+pwd);
                if (ensurepwd.equals(pwd)) {
                    postData3(user, pwd);
                }
                else {
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不一致噢，请仔细检查呢", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mToolbar = (Toolbar) this.findViewById(R.id.reg_toolbar);
        mToolbar.setTitle("");
        this.setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
        regtext = (TextView) this.findViewById(R.id.regtoolbar_title);
        if (regtext != null) {
            register.setTextColor(Color.parseColor("#5A5656"));

        }

        handler = new Handler(Looper.getMainLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                RegisterBean registerBean = (RegisterBean ) msg.obj;
                if(registerBean.getErrorMsg().equals("")) {
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(RegisterActivity.this, Login_Activity.class);
                    startActivity(intent1);
                    finish();
                }
                else{
                    Toast.makeText(RegisterActivity.this, registerBean.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void postData3(String username,String password) {
//
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("username",username);
        formBody.add("password",password);
        formBody.add("repassword",password);//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url("https://www.wanandroid.com/user/register")
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
                    registerBean = gson2.fromJson(response.body().string(),RegisterBean.class);
                    int errorCode = registerBean.getErrorCode();
                    Log.d("777", "获取数据成功了"+ errorCode+""+registerBean.getErrorMsg());
                    Message msg = new Message();
                    msg.obj = registerBean;
                    handler.sendMessage(msg);
                }
            }
        });
    }
}
