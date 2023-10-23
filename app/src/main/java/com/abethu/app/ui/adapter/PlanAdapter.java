package com.abethu.app.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abethu.app.R;
import com.abethu.app.listener.OnLoadMoreVideosListener;
import com.abethu.app.model.SubscriptionPlan;
import com.abethu.app.network.APIClient;
import com.abethu.app.network.APIInterface;
import com.abethu.app.ui.activity.PlansActivity;
import com.abethu.app.util.UiUtils;
import com.abethu.app.util.sharedpref.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private final String TAG = PlanAdapter.class.getSimpleName();
    public static int position;

    private PlansActivity context;
    private ArrayList<SubscriptionPlan> planList;
    private LayoutInflater inflater;
    private OnLoadMoreVideosListener listener;
    APIInterface apiInterface;
    TextView amountToPay, dedectedAmt;
    public static int selectedposition = 0;

    public PlanAdapter(PlansActivity activity, OnLoadMoreVideosListener listener, ArrayList<SubscriptionPlan> subList) {
        this.context = activity;
        this.listener = listener;
        this.planList = subList;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        UiUtils.log("TAG," ,"Position: "+ viewType);
        View view = inflater.inflate(R.layout.item_plan_3_subscribe_1, viewGroup, false);

        switch (viewType) {
            case 0: view = inflater.inflate(R.layout.item_plan_3_subscribe_1, viewGroup, false);
                break;
            case 2: view = inflater.inflate(R.layout.item_plan_3_subscribe_2, viewGroup, false);
                break;
        }

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position % 2 * 2;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {                   
        PlansActivity.discounted_Listed_price=false;//its changes to plane original or discounted
        viewHolder.title.setText(String.format("(%s)", planList.get(i).getTitle()));
        //viewHolder.tvMonths.setText(String.valueOf(planList.get(i).getMonths()));
        String[] plan = planList.get(i).getMonthFormatted().split("\\s");
        viewHolder.tvMonths.setText(plan[0]);
        viewHolder.tv_months.setText(plan[1]);

        viewHolder.fullRupee.setText(planList.get(i).getSymbol());
        viewHolder.rupee.setText(planList.get(i).getSymbol());


        viewHolder.plan.setText(planList.get(i).getMonthFormatted());
        //viewHolder.currentPlanCurrency.setText("*Price in " +planList.get(i).getCode());

       /* if(planList.get(i).getCode().equalsIgnoreCase(""))
            viewHolder.currentPlanCurrency.setText(context.getResources().getString(R.string.inr));*/

        if(planList.get(i).getSymbol().equalsIgnoreCase(""))
        {
            viewHolder.fullRupee.setText(context.getResources().getString(R.string.currency));
            viewHolder.rupee.setText(context.getResources().getString(R.string.currency));
        }

        if(planList.get(i).getAmount()!= 0) {
            viewHolder.ll_Amount.setVisibility(View.VISIBLE);
            viewHolder.rupee.setPaintFlags(viewHolder.rupee.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.tvFullPrice.setPaintFlags(viewHolder.tvFullPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.tvPrice.setText(String.valueOf((int)planList.get(i).getListedPrice()));
            viewHolder.tvFullPrice.setText(String.valueOf((int)planList.get(i).getAmount()));
        }else
        {
            viewHolder.ll_Amount.setVisibility(View.GONE);
        }

      /*  viewHolder.noOfAcc.setText(MessageFormat.format(context.getString(R.string.no_of_accounts) + " " +"{0}", planList.get(i).getNoOfAccounts()));
        viewHolder.amount.setText(planList.get(i).getAmountWithCurrency());
        viewHolder.date.setVisibility(View.GONE);
        viewHolder.viewDetails.setOnClickListener(view -> viewFullSubscription(planList.get(i)));
        viewHolder.backAmt.setText(planList.get(i).getAmountWithCurrency());
        viewHolder.perMonth.setText(MessageFormat.format("/{0}", planList.get(i).getMonths()));*/

        /*if (selectedposition == i) {
            //viewHolder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.select_theme_green));
            viewHolder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.theme_green_transparent));
        }else {
            viewHolder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent));
        }*/
        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedposition = i;
                PlansActivity.plan = planList.get(i);
                notifyDataSetChanged();

                //if(context.prefUtils.getStringValue(PrefKeys.USER_AGE, "").equalsIgnoreCase(""))
                context.showAgePicker();
                /*else {
                    setPaymentsInterface(context, PlansActivity.plan, null);
                    context.showPaymentPicker(null);
                }*/
            }
        });

        /*viewHolder.submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedposition = i;
                PlansActivity.plan = planList.get(i);
                notifyDataSetChanged();

                //if(context.prefUtils.getStringValue(PrefKeys.USER_AGE, "").equalsIgnoreCase(""))
                    context.showAgePicker();
                *//*else {
                    setPaymentsInterface(context, PlansActivity.plan, null);
                    context.showPaymentPicker(null);
                }*//*
            }
        });*/
        checkWidth(viewHolder,i);
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public void showLoading() {
        if(listener!=null)
            listener.onLoadMore(planList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.tvMonths)
        TextView tvMonths;
        @BindView(R.id.tv_months)
        TextView tv_months;
        @BindView(R.id.ll_Amount)
        LinearLayout ll_Amount;
        @BindView(R.id.ll_full_Amount)
        LinearLayout ll_full_Amount;
        @BindView(R.id.tvPrice)
        TextView tvPrice;
        @BindView(R.id.tvFullPrice)
        TextView tvFullPrice;
        @BindView(R.id.rupee)
        TextView rupee;
        @BindView(R.id.full_rupee)
        TextView fullRupee;
        @BindView(R.id.txt_plan)
        TextView plan;
        @BindView(R.id.tv_descp)
        TextView tvDescp;

        @BindView(R.id.rl_subscription)
        RelativeLayout rl_subscription;

        /*@BindView(R.id.current_plan_currency)
        TextView currentPlanCurrency;*/
        /*@BindView(R.id.submit_btn)
        Button submit_btn;*/
        @BindView(R.id.root)
        RelativeLayout root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void checkWidth(ViewHolder viewHolder, int i)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int dp = (int) (width / Resources.getSystem().getDisplayMetrics().density);

        UiUtils.log(TAG, "Width: "+ width);
        UiUtils.log(TAG, "Width dp: "+ dp);

        if(dp<400)
        {
            int newWidth = 470;
            if(width>1300)
                newWidth = 580;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, Utils.dpToPx(20));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if(getItemViewType(i)==0) {

                if(width>1300)
                    params.setMargins(Utils.dpToPx(15), 0, 0, 75);
                else
                    params.setMargins(Utils.dpToPx(15), 0, 0, 60);

            }else
            {
                if(width>1300)
                    params.setMargins(0, 0, Utils.dpToPx(15), 75);
                else
                    params.setMargins(0,0,Utils.dpToPx(15),60);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
            }
            viewHolder.title.setLayoutParams(params);
            viewHolder.title.setGravity(Gravity.CENTER);

            viewHolder.plan.setTextSize(8f);
            viewHolder.tvDescp.setTextSize(8f);
            viewHolder.tvPrice.setTextSize(45f);
        }
        else if(dp>400 && dp<500)
        {
            int newWidth = 550;
            if(width>1300)
                newWidth = 700;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, Utils.dpToPx(20));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            LinearLayout.LayoutParams paramsAmount = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsFullAmount = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(getItemViewType(i)==0) {
                if(width>1300)
                    params.setMargins(Utils.dpToPx(15), 0, 0, 65);
                else
                    params.setMargins(Utils.dpToPx(15), 0, 0, 55);

                paramsAmount.setMargins(0,10,50,0);
                paramsFullAmount.setMargins(0,-30,40,0);

            }else
            {
                if(width>1300)
                    params.setMargins(0,0,Utils.dpToPx(15),65);
                else
                    params.setMargins(0, 0, Utils.dpToPx(15), 55);

                params.addRule(RelativeLayout.ALIGN_PARENT_END);

                paramsAmount.setMargins(80,10,0,0);
                paramsFullAmount.setMargins(70,-30,0,0);

            }
            viewHolder.title.setLayoutParams(params);
            viewHolder.title.setGravity(Gravity.CENTER);

            viewHolder.ll_Amount.setLayoutParams(paramsAmount);
            viewHolder.ll_full_Amount.setLayoutParams(paramsFullAmount);
        }
        else if(dp<500)
        {
            UiUtils.log(TAG,"Current width: dp<500");
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(550, Utils.dpToPx(22));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            LinearLayout.LayoutParams paramsAmount = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsFullAmount = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(getItemViewType(i)==0) {
                params.setMargins(Utils.dpToPx(20), 0, 0, 50);

                paramsAmount.setMargins(0,10,50,0);
                paramsFullAmount.setMargins(0,-30,40,0);
            }else
            {
                params.setMargins(0,0,Utils.dpToPx(20),50);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);

                paramsAmount.setMargins(80,10,0,0);
                paramsFullAmount.setMargins(70,-30,0,0);
            }
            viewHolder.title.setLayoutParams(params);
            viewHolder.title.setGravity(Gravity.CENTER);

            viewHolder.ll_Amount.setLayoutParams(paramsAmount);
            viewHolder.ll_full_Amount.setLayoutParams(paramsFullAmount);

        }else if(dp>500 && dp<800)
        {
            UiUtils.log(TAG,"Current width: dp>500");
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(675, Utils.dpToPx(22));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            LinearLayout.LayoutParams paramsAmount = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsFullAmount = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(getItemViewType(i)==0) {
                params.setMargins(Utils.dpToPx(25), 0, 0, 46);

                paramsAmount.setMargins(0,10,80,0);
                paramsFullAmount.setMargins(0,-30,70,0);
            }else
            {
                params.setMargins(0,0,Utils.dpToPx(25),46);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);

                paramsAmount.setMargins(80,10,0,0);
                paramsFullAmount.setMargins(70,-30,0,0);
            }
            viewHolder.title.setLayoutParams(params);
            viewHolder.title.setGravity(Gravity.CENTER);

            viewHolder.ll_Amount.setLayoutParams(paramsAmount);
            viewHolder.ll_full_Amount.setLayoutParams(paramsFullAmount);

        }
        else if(dp>800)
        {
            UiUtils.log(TAG,"Current width: dp<500");

            viewHolder.plan.setTextSize(16f);
            viewHolder.tvDescp.setTextSize(16f);

            RelativeLayout.LayoutParams paramsMargin = (RelativeLayout.LayoutParams) viewHolder.rl_subscription.getLayoutParams();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(800, Utils.dpToPx(24));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            LinearLayout.LayoutParams paramsAmount = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsFullAmount = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(getItemViewType(i)==0) {
                paramsMargin.setMarginStart(Utils.dpToPx(250));
                params.setMargins(Utils.dpToPx(40), 0, 0, 43);

                paramsAmount.setMargins(0,10,140,0);
                paramsFullAmount.setMargins(0,-30,120,0);
            }else
            {
                paramsMargin.setMarginEnd(Utils.dpToPx(250));
                params.setMargins(0,0,Utils.dpToPx(40),40);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);

                paramsAmount.setMargins(150,10,0,0);
                paramsFullAmount.setMargins(140,-30,0,0);
            }
            viewHolder.title.setLayoutParams(params);
            viewHolder.title.setGravity(Gravity.CENTER);

            viewHolder.ll_Amount.setLayoutParams(paramsAmount);
            viewHolder.ll_full_Amount.setLayoutParams(paramsFullAmount);

            viewHolder.rl_subscription.setLayoutParams(paramsMargin);
        }
    }
}
