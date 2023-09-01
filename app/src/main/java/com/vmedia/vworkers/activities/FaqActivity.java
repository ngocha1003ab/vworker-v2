package com.vmedia.vworkers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.vmedia.vworkers.Responsemodel.FaqResp;
import com.vmedia.vworkers.ads.AdManager;
import com.vmedia.vworkers.databinding.ActivityFaqBinding;
import com.vmedia.vworkers.restApi.ApiClient;
import com.vmedia.vworkers.restApi.ApiInterface;
import com.vmedia.vworkers.R;
import com.vmedia.vworkers.utils.Fun;
import com.vmedia.vworkers.adapters.FaqAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqActivity extends AppCompatActivity {
    ActivityFaqBinding bind;
    Activity activity;
    FaqAdapter adapter;
    List<FaqResp> list;
    AdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind =ActivityFaqBinding.inflate(getLayoutInflater());
        Fun.statusbar(this);
        setContentView(bind.getRoot());
        activity=this;

        adManager = new AdManager(activity);

        list=new ArrayList<>();
        bind.rv.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new FaqAdapter(activity,list);
        bind.rv.setAdapter(adapter);

        if (Fun.isConnected(this)){
            getdata();
        }

        bind.tool.back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void getdata(){
        Call<List<FaqResp>> call = ApiClient.restAdapter(activity).create(ApiInterface.class).Faqs(getIntent().getStringExtra("type"));
        call.enqueue(new Callback<List<FaqResp>>() {
            @Override
            public void onResponse(Call<List<FaqResp>> call, Response<List<FaqResp>> response) {
                if(response.isSuccessful() && response.body().size()!=0){
                    bindData(response);
                } else {
                    noResult();
                }
            }
            @Override
            public void onFailure(Call<List<FaqResp>> call, Throwable t) {
            }
        });
    }

    private void bindData(Response<List<FaqResp>> response) {
        for(int i=0; i<response.body().size(); i++){
            list.add(response.body().get(i));
        }
        bind.shimmerView.setVisibility(View.GONE);
        bind.rv.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void noResult() {
        bind.shimmerView.setVisibility(View.GONE);
        bind.layoutNoResult.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}