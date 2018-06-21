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
import com.lg.adapter.ImageAdapter;
import com.lg.dao.OnItemClickLitener;
import com.lg.happysmile.ImageActivity;
import com.lg.happysmile.R;
import com.lg.pojo.Image;
import com.lg.service.SmileService;
import com.lg.util.DividerLinearItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ImageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static List<Image> images;
    public static RecyclerView img_recycler;
    public static ImageAdapter img_adapter;
    private View view;
    public static int num;
    public static LinearLayout img_frg_line;
    private SwipeRefreshLayout img_srl;
    private static LinearLayout img_error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_image, container, false);
        init();
        ImageAsyncTask imageAsyncTask = new ImageAsyncTask();
        imageAsyncTask.execute();
        return view;
    }

    private void init() {
        //初始第一页
        num = 1;
        images = new ArrayList<>();
        img_recycler = (RecyclerView) view.findViewById(R.id.img_recycler);
        img_frg_line = (LinearLayout) view.findViewById(R.id.img_frg_line);
        img_srl = (SwipeRefreshLayout) view.findViewById(R.id.img_srl);
        img_error = (LinearLayout) view.findViewById(R.id.img_error);
        //设置下拉刷新
        img_srl.setOnRefreshListener(this);
        img_adapter = new ImageAdapter();
        //设置显示模式
        img_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        //设置分割线
        img_recycler.addItemDecoration(new DividerLinearItemDecoration(getContext(), DividerLinearItemDecoration.VERTICAL_LIST));
        //点击事件
        img_adapter.setOnItemClickLitener(new OnItemClickLitener() {
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
        img_adapter.notifyDataSetChanged();
        //显示加载页面
        img_frg_line.setVisibility(View.VISIBLE);
        //异步加载数据
        new ImageAsyncTask().execute();
        img_srl.setRefreshing(false);
    }

    public static class ImageAsyncTask extends AsyncTask {
        @Override
        protected void onPostExecute(Object result) {
            try {
                getData(result);
                img_adapter.notifyDataSetChanged();
                //只设置一次adapter
                if (num == 1)
                    img_recycler.setAdapter(img_adapter);
            } catch (Exception e) {
                img_frg_line.setVisibility(View.GONE);
                img_error.setVisibility(View.VISIBLE);
            }
        }

        String httpUrl = "http://apis.baidu.com/showapi_open_bus/showapi_joke/joke_pic";
        String httpArg = "page=" + num;

        @Override
        protected Object doInBackground(Object... params) {
            return SmileService.request(httpUrl, httpArg);
        }
    }

    public static void getData(Object jsonResult) {
        JSONObject result = JSON.parseObject((String) jsonResult);
        JSONObject body = JSON.parseObject(result.get("showapi_res_body").toString());
        JSONArray contentlist = JSONArray.parseArray(body.get("contentlist").toString());
        for (int i = 0; i < 20; i++) {
            JSONObject img = JSON.parseObject(contentlist.get(i).toString());
            Image image = new Image();
            image.setImg_title(img.get("title").toString());
            String strUrl = img.get("img").toString();
            strUrl = strUrl.replaceAll("</p>", "");
            strUrl = strUrl.replaceAll("/>", "");
            strUrl = strUrl.replaceAll("\"", "");
            image.setImg_url(strUrl);
            images.add(image);
        }
        //当加载出数据时，加载页面隐藏
        if (images.size() != 0) {
            img_frg_line.setVisibility(View.GONE);
            img_error.setVisibility(View.GONE);
        }
        //当不是第一页时，隐藏上拉ProgressBar
        if (num != 1)
            ImageAdapter.imgHolder.getImg_pro().setVisibility(View.GONE);
    }
}
