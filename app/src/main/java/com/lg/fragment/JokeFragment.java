package com.lg.fragment;

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
import com.lg.adapter.JokeAdapter;
import com.lg.happysmile.R;
import com.lg.service.HappyService;
import com.lg.util.DividerLinearItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class JokeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static List<String> strings;
    public static RecyclerView joke_recycler;
    public static JokeAdapter joke_adapter;
    private View view;
    public static LinearLayout joke_frg_line;
    public static int num;
    private SwipeRefreshLayout joke_srl;
    private static LinearLayout joke_error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_joke, container, false);
        init();
        JokeAsyncTask jokeAsyncTask = new JokeAsyncTask();
        jokeAsyncTask.execute();
        return view;
    }

    private void init() {
        //初始页数为1
        num = 1;
        strings = new ArrayList<>();
        joke_recycler = (RecyclerView) view.findViewById(R.id.joke_recycler);
        joke_frg_line = (LinearLayout) view.findViewById(R.id.joke_frg_line);
        joke_srl = (SwipeRefreshLayout) view.findViewById(R.id.joke_srl);
        joke_error = (LinearLayout) view.findViewById(R.id.joke_error);
        //设置下拉刷新
        joke_srl.setOnRefreshListener(this);
        joke_adapter = new JokeAdapter();
        //设置显示模式
        joke_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        //画分割线
        joke_recycler.addItemDecoration(new DividerLinearItemDecoration(getContext(), DividerLinearItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onRefresh() {
        //重回第一页
        num = 1;
        //清除list数据
        strings.clear();
        //刷新adapter
        joke_adapter.notifyDataSetChanged();
        //显示刷新页面
        joke_frg_line.setVisibility(View.VISIBLE);
        //异步加载数据
        new JokeAsyncTask().execute();
        joke_srl.setRefreshing(false);
    }

    public static class JokeAsyncTask extends AsyncTask {
        @Override
        protected void onPostExecute(Object result) {
            try {
                getData(result);
                //只是指一次adapter
                if (num == 1)
                    joke_recycler.setAdapter(joke_adapter);
            } catch (Exception e) {
                joke_frg_line.setVisibility(View.GONE);
                joke_error.setVisibility(View.VISIBLE);
            }
        }

        String httpUrl = "http://api.avatardata.cn/Joke/NewstJoke?key=fe59aff1e3044424bb719c946f9cc291";
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
            String str = json.get("content").toString().replaceAll("　　", "\n        ");
            strings.add("        " + str);
        }
        //当加载出数据时，加载页面隐藏
        if (strings.size() != 0) {
            joke_frg_line.setVisibility(View.GONE);
            joke_error.setVisibility(View.GONE);
        }
        //当不是第一页时，隐藏上拉ProgressBar
        if (num != 1)
            JokeAdapter.jokeholder.getJoke_pro().setVisibility(View.GONE);
    }
}
