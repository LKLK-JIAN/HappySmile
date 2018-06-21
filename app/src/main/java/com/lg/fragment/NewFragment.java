package com.lg.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lg.adapter.NewAdapter;
import com.lg.dao.OnItemClickLitener;
import com.lg.happysmile.ImageActivity;
import com.lg.happysmile.R;
import com.lg.pojo.Image;
import com.lg.service.HappyService;
import com.lg.util.DividerLinearItemDecoration;
import java.util.ArrayList;
import java.util.List;

public class NewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static List<Image> images;
    public static RecyclerView new_recycler;
    public static NewAdapter new_adapter;
    private View view;
    public static int num;
    public static LinearLayout new_frg_line;
    private SwipeRefreshLayout new_srl;
    private static LinearLayout new_error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new, container, false);
        init();
        NewAsyncTask imageAsyncTask = new NewAsyncTask();
        imageAsyncTask.execute();
        return view;
    }

    private void init() {
        //初始第一页
        num = 1;
        images = new ArrayList<>();
        new_recycler = (RecyclerView) view.findViewById(R.id.new_recycler);
        new_frg_line = (LinearLayout) view.findViewById(R.id.new_frg_line);
        new_srl = (SwipeRefreshLayout) view.findViewById(R.id.new_srl);
        new_error = (LinearLayout) view.findViewById(R.id.new_error);
        //设置下拉刷新
        new_srl.setOnRefreshListener(this);
        new_adapter = new NewAdapter();
        //设置显示模式
        new_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        //设置分割线
        new_recycler.addItemDecoration(new DividerLinearItemDecoration(getContext(), DividerLinearItemDecoration.VERTICAL_LIST));
        //点击事件
        new_adapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), ImageActivity.class);
                intent.putExtra("uri", images.get(position).getImg_url());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        //重回第一页
        num = 1;
        //清除list数据
        images.clear();
        //刷新adapter
        new_adapter.notifyDataSetChanged();
        //显示加载页面
        new_frg_line.setVisibility(View.VISIBLE);
        //异步加载数据
        new NewAsyncTask().execute();
        new_srl.setRefreshing(false);
    }

    public static class NewAsyncTask extends AsyncTask {
        @Override
        protected void onPostExecute(Object result) {
            try {
                getData(result);
                new_adapter.notifyDataSetChanged();
                //只设置一次adapter
                if (num == 1)
                    new_recycler.setAdapter(new_adapter);
            } catch (Exception e) {
                new_frg_line.setVisibility(View.GONE);
                new_error.setVisibility(View.VISIBLE);
            }
        }

        String httpUrl = "http://api.avatardata.cn/Joke/NewstImg?key=fe59aff1e3044424bb719c946f9cc291";
        String httpArg = "page=" + num + "&rows=10";

        @Override
        protected Object doInBackground(Object... params) {
            return HappyService.request(httpUrl, httpArg);
        }
    }

    public static void getData(Object jsonResult) {
        JSONObject result = JSON.parseObject((String) jsonResult);
        JSONArray content = JSONArray.parseArray(result.getString("result"));
        for (int i = 0; i <= 9; i++) {
            JSONObject json = JSON.parseObject(content.getString(i));
            Image image = new Image();
            image.setImg_title(json.get("content").toString());
            image.setImg_url(json.get("url").toString());
            images.add(image);
        }
        //当加载出数据时，加载页面隐藏
        if (images.size() != 0) {
            new_frg_line.setVisibility(View.GONE);
            new_error.setVisibility(View.GONE);
        }
        //当不是第一页时，隐藏上拉ProgressBar
        if (num != 1)
            NewAdapter.newHolder.getNew_pro().setVisibility(View.GONE);
    }
}
