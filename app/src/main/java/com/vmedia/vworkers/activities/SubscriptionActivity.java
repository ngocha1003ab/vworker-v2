package com.vmedia.vworkers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.vmedia.vworkers.R;
import com.vmedia.vworkers.Responsemodel.SubscriptionResp;
import com.vmedia.vworkers.adapters.SubscriptionAdapter;
import com.vmedia.vworkers.databinding.ActivitySubscriptionBinding;
import com.vmedia.vworkers.fragment.FaqBottomDialogFragment;
import com.vmedia.vworkers.listener.OnItemClickListener;
import com.vmedia.vworkers.restApi.ApiClient;
import com.vmedia.vworkers.restApi.ApiInterface;
import com.vmedia.vworkers.utils.Const;
import com.vmedia.vworkers.utils.Fun;
import com.vmedia.vworkers.utils.Pref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity implements OnItemClickListener {
    ActivitySubscriptionBinding bind;
    Activity activity;
    Pref pref;
    List<SubscriptionResp.DataItem> items=new ArrayList<>();
    SubscriptionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySubscriptionBinding.inflate(getLayoutInflater(),null,false);
        Fun.statusbar(this);
        setContentView(bind.getRoot());

        activity = this;
        pref = new Pref(activity);


        bind.rv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        adapter=new SubscriptionAdapter(activity,items);
        adapter.setClickListener(this);
        bind.rv.setAdapter(adapter);

        getSub();

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });

        bind.faq.setOnClickListener(v -> {
            Const.FAQ_TYPE="subscription";
            FaqBottomDialogFragment fragment =  FaqBottomDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(),FaqBottomDialogFragment.TAG);
        });

    }

    private void getSub() {
        Call<SubscriptionResp> call = ApiClient.restAdapter(activity).create(ApiInterface.class).getSubscriptions();
        call.enqueue(new Callback<SubscriptionResp>() {
            @Override
            public void onResponse(Call<SubscriptionResp> call, Response<SubscriptionResp> response) {
                bind.shimmerView.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body().getData().size() > 0) {
                    bind.rv.setVisibility(View.VISIBLE);
                    items.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    bind.layoutNoResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<SubscriptionResp> call, Throwable t) {
                bind.layoutNoResult.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        SubscriptionResp.DataItem resp=items.get(position);
        Intent intent = new Intent(activity,PaymentActivity.class);
        intent.putExtra("productID",resp.getProductID());
        intent.putExtra("title",resp.getTitle());
        intent.putExtra("amount",resp.getAmount());
        intent.putExtra("type","sub");
        intent.putExtra("currency",resp.getCurrency());
        intent.putExtra("currency_posi",resp.getCurrency_posi());
        intent.putExtra("id",resp.getId());
        intent.putExtra("validity",resp.getValid_for());
        intent.putExtra("image",resp.getImage());
        startActivity(intent);
    }
}