package com.abethu.app.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abethu.app.R;
import com.abethu.app.model.GenreSeason;
import com.abethu.app.ui.activity.VideoPageActivity;
import com.abethu.app.util.UiUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SeasonTitleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = SeasonTitleAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private VideoPageActivity context;
    private ArrayList<GenreSeason> videos;

    public SeasonTitleAdapter(VideoPageActivity context, ArrayList<GenreSeason> videos) {
        this.context = context;
        this.videos = videos;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = inflater.inflate(R.layout.item_season_title, viewGroup, false);
        return new SeasonTitleAdapter.NormalVideoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        GenreSeason video = videos.get(position);
        SeasonTitleAdapter.NormalVideoViewHolder normalVideoViewHolder = (SeasonTitleAdapter.NormalVideoViewHolder) viewHolder;
        normalVideoViewHolder.title.setText(video.getName());
        /*if(video.isSelected()){
            normalVideoViewHolder.title.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_season));
        }else {
            normalVideoViewHolder.title.setBackgroundColor(Color.TRANSPARENT);
        }*/
        if(video.isSelected()){
            normalVideoViewHolder.title.setBackgroundTintList(AppCompatResources.getColorStateList(context,R.color.background_color_selected));
            normalVideoViewHolder.title.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            normalVideoViewHolder.title.setBackgroundTintList(AppCompatResources.getColorStateList(context,R.color.background_color_disable));
            normalVideoViewHolder.title.setTextColor(context.getResources().getColor(R.color.black));
        }
        checkWidth(normalVideoViewHolder.title);
        normalVideoViewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.updateSeason(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    class NormalVideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;

        NormalVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void checkWidth(TextView title)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int dp = (int) (width / Resources.getSystem().getDisplayMetrics().density);

        UiUtils.log(TAG, "Width: "+ width);
        UiUtils.log(TAG, "Width dp: "+ dp);

        if(dp>500 && dp<600)
        {
            UiUtils.log(TAG, "margin(500-600)");
            title.setTextSize(14);
        }else if(dp>600 && dp<700)
        {
            UiUtils.log(TAG, "margin(600-700)");
            title.setTextSize(14);
        }else if(dp>700 && dp<800)
        {
            UiUtils.log(TAG, "margin(700-800)");
            title.setTextSize(14);
        }else if(dp>800)
        {
            UiUtils.log(TAG, "margin(800)");
            title.setTextSize(16);
        }
    }
}
