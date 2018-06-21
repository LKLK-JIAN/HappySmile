package com.lg.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.lg.fragment.JokeFragment;
import com.lg.happysmile.MainActivity;
import com.lg.happysmile.R;
import com.lg.holder.JokeHolder;

public class JokeAdapter extends RecyclerView.Adapter<JokeHolder> {
    private View view;
    private int lastPosition = -1;

    @Override
    public JokeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.joke_item, parent, false);
        return new JokeHolder(view);
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
    public void onViewDetachedFromWindow(JokeHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.getJoke_line().clearAnimation();
    }

    public static JokeHolder jokeholder;

    @Override
    public void onBindViewHolder(JokeHolder holder, int position) {
        holder.getJoke_tv().setText(JokeFragment.strings.get(position));
        if (MainActivity.isOk) {
            //当item为最后一个的时候，加载分页
            if (position == getItemCount() - 1) {
                JokeFragment.num++;
                jokeholder = holder;
                holder.getJoke_pro().setVisibility(View.VISIBLE);
                new JokeFragment.JokeAsyncTask().execute();
            }
        }
        //设置加载item动画
        setAnimation(holder.getJoke_line(), position);
    }

    @Override
    public int getItemCount() {
        return JokeFragment.strings.size();
    }
}
