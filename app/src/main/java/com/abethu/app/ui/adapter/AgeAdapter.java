package com.abethu.app.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abethu.app.R;
import com.abethu.app.model.AgeItem;
import com.abethu.app.model.SettingsItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AgeAdapter extends RecyclerView.Adapter<AgeAdapter.AgeViewHolder> {

    private Context context;
    private LayoutInflater inflate;
    private ArrayList<AgeItem> ageItems;

    public AgeAdapter(Context context, ArrayList<AgeItem> age) {
        this.context = context;
        this.ageItems = age;
        inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public AgeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflate.inflate(R.layout.item_age, viewGroup, false);
        return new AgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgeViewHolder viewHolder, int position) {
        AgeItem profileControlItem = ageItems.get(position);
        viewHolder.ageName.setText(profileControlItem.getAgeName());
        viewHolder.root.setOnClickListener(profileControlItem.getClickListener());
        viewHolder.root.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        if(profileControlItem.isSelected())
            viewHolder.root.setBackgroundColor(context.getResources().getColor(R.color.colorAccentTransparent));



    }

    @Override
    public int getItemCount() {
        return ageItems.size();
    }

    class AgeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ageName)
        TextView ageName;
        @BindView(R.id.ageItem)
        ViewGroup root;

        AgeViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
