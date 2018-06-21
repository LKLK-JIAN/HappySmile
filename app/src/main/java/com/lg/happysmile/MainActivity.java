package com.lg.happysmile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.NetworkInfo.State;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.lg.adapter.MyViewPagerAdapter;
import com.lg.fragment.CarefullyFragment;
import com.lg.fragment.NewFragment;
import com.lg.fragment.JokeFragment;
import com.lg.fragment.ImageFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private TabLayout tab_layout;
    private ViewPager viewPager;
    private MyViewPagerAdapter view_adapter;
    private String[] titles = new String[]{"最新", "精选", "趣图", "段子"};
    private List<Fragment> fragments;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    //检测网络连接状态
    private ConnectivityManager manager;
    //显示状态
    public static boolean state = true;
    private long exitTime = 0;
    //网络是否可用
    public static boolean isOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void init() {
        sp = getSharedPreferences("Happy", MODE_PRIVATE);
        editor = sp.edit();
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fragments = new ArrayList<>();
        //设置TabLayout标签的显示方式
        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        //循环注入标签
        for (String tab : titles) {
            tab_layout.addTab(tab_layout.newTab().setText(tab));
        }
        //设置TabLayout点击事件
        tab_layout.setOnTabSelectedListener(this);
        fragments.add(new NewFragment());
        fragments.add(new CarefullyFragment());
        fragments.add(new ImageFragment());
        fragments.add(new JokeFragment());
        view_adapter = new MyViewPagerAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(view_adapter);
        tab_layout.setupWithViewPager(viewPager);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //获取连接信息
                manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                // 获取NetworkInfo对象
                NetworkInfo networkinfo = manager.getActiveNetworkInfo();

                if (networkinfo != null || networkinfo.isAvailable()) {
                    isOk=true;
                    isNetworkAvailable();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isOk = false;
        }
    };


    /**
     * 网络已经连接，然后去判断是wifi连接还是GPRS连接
     */
    private void isNetworkAvailable() {
        State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (sp.getString("state", null) == null) {
            editor.putString("state", "false");
            editor.commit();
        }

        if (sp.getString("state", null).equals("true")) {
            if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
                state = false;
            }
            if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
                state = true;
            }

        } else if (sp.getString("state", null).equals("false"))
            state = true;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(MainActivity.this, "再点我就走喽！", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }



    public void onClick(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
