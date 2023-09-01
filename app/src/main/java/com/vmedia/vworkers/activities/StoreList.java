package com.vmedia.vworkers.activities;

import static com.vmedia.vworkers.utils.AdsConfig.bannerID;
import static com.vmedia.vworkers.utils.AdsConfig.banner_type;
import static com.vmedia.vworkers.utils.AdsConfig.interstitalCount;
import static com.vmedia.vworkers.utils.AdsConfig.interstital_adunit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vmedia.vworkers.Responsemodel.CoinStoreResponse;
import com.vmedia.vworkers.adapters.CoinstoreAdapter;
import com.vmedia.vworkers.ads.AdManager;
import com.vmedia.vworkers.databinding.ActivityStoreListBinding;
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

public class StoreList extends AppCompatActivity {
    ActivityStoreListBinding bind;
    Activity activity;
    List<CoinStoreResponse.DataItem> storelist;
    AdManager adManager;
    CoinstoreAdapter adapter;
    Pref pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityStoreListBinding.inflate(getLayoutInflater());
        Fun.statusbar(this);
        setContentView(bind.getRoot());

        activity = this;
        pref=new Pref(activity);
        adManager = new AdManager(activity);
        adManager.loadBannerAd(bind.BANNER,pref.getString(banner_type),pref.getString(bannerID));

        storelist = new ArrayList<>();
        bind.rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CoinstoreAdapter(activity, storelist);
        bind.rv.setAdapter(adapter);

        intData();

        bind.faq.setOnClickListener(v -> {
            Const.FAQ_TYPE="coinstore";
            FaqBottomDialogFragment fragment =  FaqBottomDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(),FaqBottomDialogFragment.TAG);
        });

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        adManager.OnBackInterstitalAd(pref.getInt(interstitalCount),pref.getString(interstital_adunit));
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void intData() {
        ApiClient.restAdapter(activity).create(ApiInterface.class).StoreList().enqueue(new Callback<CoinStoreResponse>() {
            @Override
            public void onResponse(Call<CoinStoreResponse> call, Response<CoinStoreResponse> response) {
                bind.shimmerView.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body().getCode().equals("201")) {
                    bind.rv.setVisibility(View.VISIBLE);
                    storelist.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    Const.PAY_INFO=response.body().getInfo();
                } else {
                    bind.layoutNoResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CoinStoreResponse> call, Throwable t) {
                Log.e("intData_", "onFailure: "+t.getMessage() );
                bind.layoutNoResult.setVisibility(View.VISIBLE);
            }
        });
    }


}