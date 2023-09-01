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
import static com.vmedia.vworkers.utils.Fun.loading;
import static com.vmedia.vworkers.utils.Fun.msgError;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.vmedia.vworkers.Responsemodel.TaskResp;
import com.vmedia.vworkers.adapters.TaskAdapter;
import com.vmedia.vworkers.ads.AdManager;
import com.vmedia.vworkers.databinding.ActivityVideoBinding;
import com.vmedia.vworkers.databinding.LayoutAlertBinding;
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

public class VideoActivity extends AppCompatActivity implements OnItemClickListener {
    ActivityVideoBinding bind;
    VideoActivity activity;
    TaskAdapter adapter;
    List<TaskResp> list;
    int item,posi;
    String id;
    AdManager adManager;
    AlertDialog alert,loading;
    LayoutAlertBinding lytAlert;
    Pref pref;
    public static boolean removeItem=false;
    public static int itemID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityVideoBinding.inflate(getLayoutInflater());
        Fun.statusbar(this);
        setContentView(bind.getRoot());

        bind.tool.title.setText(Const.TOOLBAR_TITLE);

        activity=this;
        pref=new Pref(activity);
        adManager = new AdManager(activity);
        adManager.loadBannerAd(bind.BANNER,pref.getString(banner_type),pref.getString(bannerID));
        loading = loading(activity);

        lytAlert=LayoutAlertBinding.inflate(getLayoutInflater());
        alert= Fun.Alerts(activity,lytAlert);

        list=new ArrayList<>();
        bind.rv.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new TaskAdapter(activity,list,0);
        adapter.setClickListener(this::onClick);
        bind.rv.setAdapter(adapter);

        apivideo();

        bind.tool.back.setOnClickListener(v -> {
            onBackPressed();
        });

        bind.tool.faq.setOnClickListener(view -> {
            Const.FAQ_TYPE="video";
            FaqBottomDialogFragment fragment =  FaqBottomDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(),FaqBottomDialogFragment.TAG);
        });

    }

    private void apivideo() {
        Call<List<TaskResp>> call = ApiClient.restAdapter(activity).create(ApiInterface.class).apivideo();
        call.enqueue(new Callback<List<TaskResp>>() {
            @Override
            public void onResponse(Call<List<TaskResp>> call, Response<List<TaskResp>> response) {
                if (response.isSuccessful() && response.body().size() != 0) {
                    list.clear();
                    bindData(response);
                } else {
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
    public void onClick(View view, int position) {
        this.posi=position;
        this.id=list.get(position).getId();
        itemID=position;
        try {
            if(list.get(position).getUrl().startsWith("http")){
                Intent intent = new Intent(activity,PlayTime.class);
                intent.putExtra("type","videozone");
                intent.putExtra("id",list.get(position).getId());
                intent.putExtra("url",list.get(position).getUrl());
                intent.putExtra("thumb", list.get(position).getThumb());
                intent.putExtra("title",list.get(position).getTitle());
                intent.putExtra("time",list.get(position).getTimer());
                intent.putExtra("coin",list.get(position).getPoint());
                Const.adType=list.get(position).getAd_type();
                intent.putExtra("action_type",list.get(position).getAction_type());
                startActivity(intent);
            }else{
                msgError(activity, "Url Broken");
            }
            
        }catch (Exception e){
            Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(removeItem){
            removeItem(itemID);
        }
    }

    @Override
    public void onBackPressed() {
        adManager.OnBackInterstitalAd(pref.getInt(interstitalCount),pref.getString(interstital_adunit));
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        System.out.println("on pause");
        super.onPause();
    }

    private void removeItem(int posi){
        list.remove(posi);
        adapter.notifyDataSetChanged();
        removeItem=false;
        if(list.size()<6){
            list.clear();
            apivideo();
        }
    }
}