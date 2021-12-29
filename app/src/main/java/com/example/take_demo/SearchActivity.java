package com.example.take_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.take_demo.bean.Hotkeybean;
import com.example.take_demo.bean.bannerBean;
import com.example.take_demo.utils.SearchviewAdapter;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener{

    private List<Hotkeybean.DataBean> list = new ArrayList<>();
    private List<Hotkeybean.DataBean> locallist;
    private static final String TAG = "234";
    private EditText msearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ImmersionBar.with(this).init();  //必须调用方可沉浸式
        msearch =findViewById(R.id.edit_search);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fra_search, FirstsearchFragment.newInstance("1"))
                .commit();
        initsearch();
    }

    /**
     * 初始化监听
     */
    private void initsearch() {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(msearch, InputMethodManager.SHOW_FORCED); //显示软键盘
        msearch.setOnEditorActionListener(this);
    }

    /**
     * 监听
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // 当按了搜索之后关闭软键盘
            ((InputMethodManager) msearch.getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    SearchActivity.this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
                    String key = msearch.getText().toString();
            Log.d(TAG, key);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fra_search, SearchResultFragment.newInstance(key))
                    .commit();
            return true;
        }
        return false;
    }
}


