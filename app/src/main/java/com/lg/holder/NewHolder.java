package com.lg.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lg.happysmile.R;

public class NewHolder extends RecyclerView.ViewHolder {

    private TextView new_title;
    private SimpleDraweeView news;
    private LinearLayout new_line;
    private ProgressBar new_pro;

    public NewHolder(View itemView) {
        super(itemView);
        new_title = (TextView) itemView.findViewById(R.id.new_title);
        news = (SimpleDraweeView) itemView.findViewById(R.id.news);
        new_line = (LinearLayout) itemView.findViewById(R.id.new_line);
        new_pro=(ProgressBar)itemView.findViewById(R.id.new_pro);
    }

    public ProgressBar getNew_pro() {
        return new_pro;
    }

    public TextView getNew_title() {
        return new_title;
    }

    public SimpleDraweeView getNews() {
        return news;
    }

    public LinearLayout getNew_line() {
        return new_line;
    }
}
