package com.abethu.app.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abethu.app.R;
import com.abethu.app.listener.OnLoadMoreVideosListener;
import com.abethu.app.model.SubscriptionPlan;
import com.abethu.app.ui.activity.PlansActivity;
import com.abethu.app.util.UiUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Discounted_priceAdapter extends RecyclerView.Adapter<Discounted_priceAdapter.ViewHolder>{

    private final String TAG = PlanAdapter.class.getSimpleName();
    public static int position;

    private PlansActivity context;
    private ArrayList<SubscriptionPlan> discountList;
    private LayoutInflater inflater;
    private OnLoadMoreVideosListener listener;
    public static int selectedposition = 0;
    public static SubscriptionPlan discountplan;

    public Discounted_priceAdapter( PlansActivity plansActivity, OnLoadMoreVideosListener listener, ArrayList<SubscriptionPlan> discountplans) {
        this.context = plansActivity;
        this.discountList = discountplans;
        this.listener = listener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        UiUtils.log(TAG ,"Position: "+ viewType);
        View view = inflater.inflate(R.layout.discounted_price, viewGroup, false);
        switch (viewType) {
            case 0: view = inflater.inflate(R.layout.discounted_price, viewGroup, false);
                break;
            case 2: view = inflater.inflate(R.layout.discounted_price_plan_1, viewGroup, false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Animation scale = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.scale_zoomin_zoomout);
        viewHolder.llDcMonth.setText(discountList.get(i).getTitle());
        viewHolder.dcMonth.setText(String.valueOf(discountList.get(i).getMonths()));
        viewHolder.nonDc_price.setText(String.valueOf(discountList.get(i).getAmount()));
        viewHolder.nonDc_price.setPaintFlags(viewHolder.nonDc_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.dcPrice.setText(String.valueOf(discountList.get(i).getPercentage()));
        viewHolder.dcPercentage.setText(String.valueOf(discountList.get(i).getDiscount()));
        viewHolder.discount_Anim.startAnimation(scale);
        viewHolder.dcRenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedposition = i;
                PlansActivity.plan = discountList.get(i);
                notifyDataSetChanged();
                context.showAgePicker();
            }
        });
    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }
    public void showLoading() {
        if(listener!=null)
            listener.onLoadMore(discountList.size());
    }
    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position % 2 * 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.dcMonth)
    TextView dcMonth;
    @BindView(R.id.llDcMonth)
    TextView llDcMonth;
    @BindView(R.id.dcOriginalPrice_Currency)
    TextView dcOriginalPrice_Currency;
    @BindView(R.id.discountPrice_Currency)
    TextView discountPrice_Currency;
    @BindView(R.id.dcPercentage)
    TextView dcPercentage;
    @BindView(R.id.nonDc_price)
    TextView nonDc_price;
    @BindView(R.id.dcPrice)
    TextView dcPrice;
    @BindView(R.id.dcRenew)
    Button dcRenew;
    @BindView(R.id.discount_Layout)
    RelativeLayout discount_Layout;
    @BindView(R.id.discount_Anim)
    RelativeLayout discount_Anim;
    public ViewHolder(@NonNull View itemView){
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
}
