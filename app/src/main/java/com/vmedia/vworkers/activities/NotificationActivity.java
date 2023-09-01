package com.vmedia.vworkers.activities;

import static com.vmedia.vworkers.utils.AdsConfig.ADMOB;
import static com.vmedia.vworkers.utils.AdsConfig.CUSTOM;
import static com.vmedia.vworkers.utils.AdsConfig.FB;
import static com.vmedia.vworkers.utils.AdsConfig.STARTAPP;
import static com.vmedia.vworkers.utils.AdsConfig.bannerID;
import static com.vmedia.vworkers.utils.AdsConfig.banner_type;
import static com.vmedia.vworkers.utils.AdsConfig.interstitalCount;
import static com.vmedia.vworkers.utils.AdsConfig.interstital_adunit;
import static com.vmedia.vworkers.utils.AdsConfig.nativeType;
import static com.vmedia.vworkers.utils.AdsConfig.native_count;
import static com.vmedia.vworkers.utils.Fun.js;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vmedia.vworkers.R;
import com.vmedia.vworkers.Responsemodel.DefResp;
import com.vmedia.vworkers.Responsemodel.DefListResp;
import com.vmedia.vworkers.adapters.NotiAdapter;
import com.vmedia.vworkers.ads.AdManager;
import com.vmedia.vworkers.databinding.ActivityNotificationBinding;
import com.vmedia.vworkers.restApi.ApiClient;
import com.vmedia.vworkers.restApi.ApiInterface;
import com.vmedia.vworkers.utils.Fun;
import com.vmedia.vworkers.utils.Pref;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding bind;
    NotificationActivity activity;
    NotiAdapter adapter;
    List<DefListResp> list;
    AdManager adManager;
    int item;
    Pref pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityNotificationBinding.inflate(getLayoutInflater());
        Fun.statusbar(this);
        setContentView(bind.getRoot());
        activity=this;
        pref=new Pref(activity);

        bind.tool.title.setText(getText(R.string.notification));
        adManager = new AdManager(activity);
        adManager.loadBannerAd(bind.BANNER,pref.getString(banner_type),pref.getString(bannerID));

        list=new ArrayList<>();
        bind.rv.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new NotiAdapter(activity,list);
        bind.rv.setAdapter(adapter);

        if (Fun.isConnected(this)){
            getdata();
        }

        bind.tool.back.setOnClickListener(v -> {
            onBackPressed();
        });

        bind.tool.faq.setVisibility(View.GONE);


    }

    private void getdata(){
        Call<List<DefListResp>> call = ApiClient.restAdapter(activity).create(ApiInterface.class).getNoti(pref.User_id());
        call.enqueue(new Callback<List<DefListResp>>() {
            @Override
            public void onResponse(Call<List<DefListResp>> call, Response<List<DefListResp>> response) {
                if(response.isSuccessful() && response.body().size()!=0){
                    bindData(response);
                    readNoti();
                } else {
                    noResult();
                }
            }
            @Override
            public void onFailure(Call<List<DefListResp>> call, Throwable t) {
            }
        });
    }

    private void readNoti() {
        try {
            ApiClient.restAdapter(activity).create(ApiInterface.class).api(js(activity,"notification", pref.User_id(), "","","")).enqueue(new Callback<DefResp>() {
                @Override
                public void onResponse(Call<DefResp> call, Response<DefResp> response) {
                    if (response.isSuccessful() && response.body().getCode().equals("201")) {
                        pref.setIntData(pref.NOTI,0);
                        Log.e("readNoti", "onResponse: "+response.body().getMsg() );
                    }
                }
                @Override
                public void onFailure(Call<DefResp> call, Throwable t) {
                }
            });
        }catch (Exception e){

        }
    }

    private void bindData(Response<List<DefListResp>> response) {
        for(int i=0; i<response.body().size(); i++){
            list.add(response.body().get(i));
            item++;
            if (item == pref.getInt(native_count)) {
                item = 0;
                if (pref.getString(nativeType).equals(FB)) {
                    list.add(new DefListResp().setViewType(3));
                } else if (pref.getString(nativeType).equals(ADMOB)) {
                    list.add(new DefListResp().setViewType(4));
                } else if (pref.getString(nativeType).equals(STARTAPP)) {
                    list.add(new DefListResp().setViewType(5));
                } else if (pref.getString(nativeType).equals(CUSTOM)) {
                    list.add(new DefListResp().setViewType(6));
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        adManager.OnBackInterstitalAd(pref.getInt(interstitalCount),pref.getString(interstital_adunit));
        super.onBackPressed();
    }
}