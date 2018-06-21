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
import com.lg.fragment.ImageFragment;
import com.lg.happysmile.MainActivity;
import com.lg.happysmile.R;
import com.lg.holder.ImageHolder;
import com.lg.util.FrescoUtil;

import java.io.File;

public class ImageAdapter extends RecyclerView.Adapter<ImageHolder> {
    private View view;
    private int lastPosition = -1;
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_item, parent, false);
        return new ImageHolder(view);
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
    public void onViewDetachedFromWindow(ImageHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.getImg_line().clearAnimation();
    }

    public static ImageHolder imgHolder;

    @Override
    public void onBindViewHolder(final ImageHolder holder, int position) {
        holder.getImg_title().setText(ImageFragment.images.get(position).getImg_title());
        Uri uri = Uri.parse(ImageFragment.images.get(position).getImg_url());
        if (MainActivity.isOk) {
            if (MainActivity.state) {
                DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                holder.getImg().setController(draweeController);
                //当item为最后一个的时候，加载分页
                if (position == getItemCount() - 1) {
                    ImageFragment.num++;
                    imgHolder = holder;
                    holder.getImg_pro().setVisibility(View.VISIBLE);
                    new ImageFragment.ImageAsyncTask().execute();
                }
            } else {
                CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(uri));
                File cacheFile = FrescoUtil.getCachedImageOnDisk(cacheKey);
                if (cacheFile != null) {   //存在缓存
                    DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                    holder.getImg().setController(draweeController);
                } else {
                    holder.getImg().setImageResource(R.drawable.error);
                    holder.getImg().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }
        } else {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(uri));
            File cacheFile = FrescoUtil.getCachedImageOnDisk(cacheKey);
            if (cacheFile != null) {   //存在缓存
                DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                holder.getImg().setController(draweeController);
            } else {
                holder.getImg().setImageResource(R.drawable.error);
                holder.getImg().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        }
        //设置加载item动画
        setAnimation(holder.getImg_line(), position);
        //设置点击事件
        holder.getImg().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemClick(holder.itemView, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ImageFragment.images.size();
    }
}
