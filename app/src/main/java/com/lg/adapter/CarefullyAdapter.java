package com.lg.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.lg.fragment.CarefullyFragment;
import com.lg.happysmile.MainActivity;
import com.lg.happysmile.R;
import com.lg.holder.CarefullyHolder;

public class CarefullyAdapter extends RecyclerView.Adapter<CarefullyHolder> {
    private View view;
    private int lastPosition = -1;

    @Override
    public CarefullyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carefully_item, parent, false);
        return new CarefullyHolder(view);
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
    public void onViewDetachedFromWindow(CarefullyHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.getCarefully_line().clearAnimation();
    }

    public static CarefullyHolder carefullyholder;

    @Override
    public void onBindViewHolder(CarefullyHolder holder, int position) {
        holder.getCarefully_tv().setText(CarefullyFragment.strings.get(position));
        if (MainActivity.isOk) {
            //当item为最后一个的时候，加载分页
            if (position == getItemCount() - 1) {
                CarefullyFragment.num++;
                carefullyholder = holder;
                holder.getCarefully_pro().setVisibility(View.VISIBLE);
                new CarefullyFragment.CarefullyAsyncTask().execute();
            }
        }
        //设置加载item动画
        setAnimation(holder.getCarefully_line(), position);
    }

    @Override
    public int getItemCount() {
        return CarefullyFragment.strings.size();
    }
}
