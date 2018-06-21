package com.lg.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.lg.dao.OnItemClickLitener;
import com.lg.fragment.NewFragment;
import com.lg.happysmile.MainActivity;
import com.lg.happysmile.R;
import com.lg.holder.NewHolder;
import com.lg.util.FrescoUtil;

import java.io.File;

public class NewAdapter extends RecyclerView.Adapter<NewHolder> {
    private View view;
    private int lastPosition = -1;
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public NewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_item, parent, false);
        return new NewHolder(view);
    }

    //加载item动画
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                    .anim.item_bottom_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(NewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.getNew_line().clearAnimation();
    }

    public static NewHolder newHolder;

    @Override
    public void onBindViewHolder(final NewHolder holder, int position) {
        holder.getNew_title().setText(NewFragment.images.get(position).getImg_title());
        Uri uri = Uri.parse(NewFragment.images.get(position).getImg_url());
        if (MainActivity.isOk) {
            if (MainActivity.state) {
                DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                holder.getNews().setController(draweeController);
//        holder.getImg().setErrorImageResId(R.drawable.no_img);
//        holder.getImg().setDefaultImageResId(R.drawable.no_img);
//        holder.getImg().setImageUrl(NewFragment.images.get(position).getImg_url(), NewFragment.imageLoader);
                //当item为最后一个的时候，加载分页
                if (position == getItemCount() - 1) {
                    NewFragment.num++;
                    newHolder = holder;
                    holder.getNew_pro().setVisibility(View.VISIBLE);
                    new NewFragment.NewAsyncTask().execute();
                }
            } else {
                CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(uri));
                File cacheFile = FrescoUtil.getCachedImageOnDisk(cacheKey);
                if (cacheFile != null) {   //存在缓存
                    DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                    holder.getNews().setController(draweeController);
                } else {
                    holder.getNews().setImageResource(R.drawable.error);
                    holder.getNews().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }
        }else {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(uri));
            File cacheFile = FrescoUtil.getCachedImageOnDisk(cacheKey);
            if (cacheFile != null) {   //存在缓存
                DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                holder.getNews().setController(draweeController);
            } else {
                holder.getNews().setImageResource(R.drawable.error);
                holder.getNews().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        }

        //设置加载item动画
        setAnimation(holder.getNew_line(), position);
        //设置点击事件
        holder.getNews().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemClick(holder.itemView, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return NewFragment.images.size();
    }
}
