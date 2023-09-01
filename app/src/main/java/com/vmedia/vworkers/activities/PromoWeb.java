package com.vmedia.vworkers.activities;

import static com.vmedia.vworkers.utils.AdsConfig.ADMOB;
import static com.vmedia.vworkers.utils.AdsConfig.CUSTOM;
import static com.vmedia.vworkers.utils.AdsConfig.FB;
import static com.vmedia.vworkers.utils.AdsConfig.STARTAPP;
import static com.vmedia.vworkers.utils.AdsConfig.interstitalCount;
import static com.vmedia.vworkers.utils.AdsConfig.interstital_adunit;
import static com.vmedia.vworkers.utils.AdsConfig.nativeType;
import static com.vmedia.vworkers.utils.AdsConfig.native_count;
import static com.vmedia.vworkers.utils.Fun.js;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.vmedia.vworkers.Responsemodel.TaskResp;
import com.vmedia.vworkers.adapters.PromoTaskAdapter;
import com.vmedia.vworkers.ads.AdManager;
import com.vmedia.vworkers.databinding.ActivityPromoWebBinding;
import com.vmedia.vworkers.fragment.FaqBottomDialogFragment;
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

public class PromoWeb extends AppCompatActivity  {
    ActivityPromoWebBinding bind;
    Activity activity;
    Pref pref;
    List<TaskResp> list;
    PromoTaskAdapter adapter;
    int item;
    AdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind =ActivityPromoWebBinding.inflate(getLayoutInflater());
        Fun.statusbar(this);
        setContentView(bind.getRoot());
        activity=this;
        pref =new Pref(activity);

        adManager = new AdManager(activity);

        list=new ArrayList<>();
        bind.rv.setLayoutManager(new LinearLayoutManager(this));
        adapter=new PromoTaskAdapter(activity,list);
        bind.rv.setAdapter(adapter);

        bind.add.setOnClickListener(view -> {
            Intent intent = new Intent(this,CreatePromo.class);
            intent.putExtra("type","web");
            startActivity(intent);
        });

        getData();

        bind.tool.back.setOnClickListener(v -> {
            onBackPressed();
        });

        bind.buy.setOnClickListener(v -> {
            startActivity(new Intent(activity, StoreList.class));
        });

        bind.tool.faq.setOnClickListener(view -> {
            Const.FAQ_TYPE="promo";
            FaqBottomDialogFragment fragment =  FaqBottomDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(),FaqBottomDialogFragment.TAG);
        });
    }


    private void getData() {
        ApiClient.restAdapter(activity).create(ApiInterface.class).apiPromo(js(activity,"PromoData", pref.User_id(), "","web","")).enqueue(new Callback<List<TaskResp>>() {
            @Override
            public void onResponse(Call<List<TaskResp>> call, Response<List<TaskResp>> response) {
                if(response.isSuccessful() && response.body().size()>0){
                    bindData(response);
                }else{
                    noResult();
                }
            }

            @Override
            public void onFailure(Call<List<TaskResp>> call, Throwable t) {
                noResult();
            }
        });
    }

    private void bindData(Response<List<TaskResp>> response) {
        for(int i=0; i<response.body().size(); i++){
            list.add(response.body().get(i));
            item++;
            if (item == pref.getInt(native_count)) {
                item = 0;
                if (pref.getString(nativeType).equals(FB)) {
                    list.add(new TaskResp().setViewType(3));
                } else if (pref.getString(nativeType).equals(ADMOB)) {
                    list.add(new TaskResp().setViewType(4));
                } else if (pref.getString(nativeType).equals(STARTAPP)) {
                    list.add(new TaskResp().setViewType(5));
                } else if (pref.getString(nativeType).equals(CUSTOM)) {
                    list.add(new TaskResp().setViewType(6));
                }
            }
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
    public void onBackPressed() {
        adManager.OnBackInterstitalAd(pref.getInt(interstitalCount),pref.getString(interstital_adunit));
        super.onBackPressed();
    }
}