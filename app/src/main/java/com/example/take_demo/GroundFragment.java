package com.example.take_demo;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.take_demo.bean.TabEntity;
import com.example.take_demo.utils.MyFragmentPagerAdapter;
import com.flyco.tablayout.listener.CustomTabEntity;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroundFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param3";
    private String mParam3;
    private ViewPager2 viewPager;
    private static final String TAG = "66666";
    private ArrayList<CustomTabEntity> mTabEntitie = new ArrayList<>();
    ArrayList<Fragment>fragments = new ArrayList<>();
    private String[] mTitles = {"项目","分类"};
    private Handler handler ;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private View rootView;//避免重复解析
    private Toolbar  mToolbar;
    private TextView groung_text;



    public GroundFragment() {
        // Required empty public constructor
    }


    public static GroundFragment newInstance(String param3) {
        GroundFragment fragment = new GroundFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam3 = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null ) {
            rootView = inflater.inflate(R.layout.fragment_ground, container, false);
        }
        mToolbar = (Toolbar) rootView.findViewById(R.id.groung_toolbar);
        mToolbar.setTitle("");
        groung_text = rootView.findViewById(R.id.groungtoolbar_title);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);//在嵌套fragment不能直接用this
        initPager();
        initTab();
        MagicIndicator magicIndicator2 = rootView.findViewById(R.id.book_indicator2);
        MagicIndicatorUtil.initMagicIndicator(rootView,getContext(),mTitles,viewPager,magicIndicator2,0);
        return rootView;
    }

    private void initTab() {
        for (String mTitle : mTitles) {
            if("1".equals(mTitle)){
                //后面两个值是选中图标和未选中(R.drawable.xxx)不要图标就填0
                mTabEntitie.add(new TabEntity(mTitle, 0, 0));
                viewPager.setCurrentItem(0);
            }else if("2".equals(mTitle)){
                mTabEntitie.add(new TabEntity(mTitle, 0, 0));
                viewPager.setCurrentItem(0);
            }

        }
    }

    private void initPager() {

        viewPager = rootView.findViewById(R.id.vp2);
//        segmentTabLayout = findViewById(R.id.tb);
        fragments.add(ItemFragment.newInstance("1"));
        fragments.add(NavigationFragment.newInstance("2"));
//        fragments.add(My_Fragment.newInstance("3"));
        MyFragmentPagerAdapter pagerAdapter2 =new MyFragmentPagerAdapter(getFragmentManager(),getLifecycle(),fragments);
        viewPager.setAdapter(pagerAdapter2);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
//                segmentTabLayout.setCurrentTab(position);
//                BottomNavigationView navView = findViewById(R.id.nav_view2);
//                navView.getMenu().getItem(position).setChecked(true);
            }
        });
    }

}
