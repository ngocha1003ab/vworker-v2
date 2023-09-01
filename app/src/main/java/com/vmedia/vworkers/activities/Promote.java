package com.vmedia.vworkers.activities;

import static com.vmedia.vworkers.utils.AdsConfig.interstitalCount;
import static com.vmedia.vworkers.utils.AdsConfig.interstital_adunit;
import static com.vmedia.vworkers.utils.Fun.js;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.vmedia.vworkers.Responsemodel.DefResp;
import com.vmedia.vworkers.ads.AdManager;
import com.vmedia.vworkers.databinding.ActivityPromoteBinding;
import com.vmedia.vworkers.fragment.FaqBottomDialogFragment;
import com.vmedia.vworkers.restApi.ApiClient;
import com.vmedia.vworkers.restApi.ApiInterface;
import com.vmedia.vworkers.utils.Const;
import com.vmedia.vworkers.utils.Fun;
import com.vmedia.vworkers.utils.Pref;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Promote extends AppCompatActivity  {
    ActivityPromoteBinding bind;
    Activity activity;
    Pref session;
    AlertDialog loadingDialog;
    AdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityPromoteBinding.inflate(getLayoutInflater());
        Fun.statusbar(this);
        setContentView(bind.getRoot());

        activity=Promote.this;
        session=new Pref(activity);
        loadingDialog= Fun.loading(activity);

        adManager = new AdManager(activity);

        getData();

        bind.video.setOnClickListener(view -> {
            startActivity(new Intent(this, PromoVideo.class));
        });

        bind.web.setOnClickListener(view -> {
            startActivity(new Intent(this, PromoWeb.class));
        });

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });

        bind.buy.setOnClickListener(v -> {
            startActivity(new Intent(activity, StoreList.class));
        });

        bind.faq.setOnClickListener(v -> {
            Const.FAQ_TYPE="promo";
            FaqBottomDialogFragment fragment =  FaqBottomDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(),FaqBottomDialogFragment.TAG);
        });


    }

    private void getData() {
        showLoading();
        Objects.requireNonNull(ApiClient.restAdapter(activity)).create(ApiInterface.class).api(js(activity,"PromoConfig", "", "","","")).enqueue(new Callback<DefResp>() {
            @Override
            public void onResponse(Call<DefResp> call, Response<DefResp> response) {
                dismisLoading();
                if (response.isSuccessful() && response.body().getCode().equals("201")) {
                    Const.MAX_PROMOTE =response.body().getData1();
                    Const.VIDEO_PROMO_COIN=response.body().getData2();

                    bind.coins.setText(""+response.body().getBalance());
                    session.setIntData(session.PROMO_WALLET,response.body().getBalance());
                }
            }

            @Override
            public void onFailure(Call<DefResp> call, Throwable t) {
                dismisLoading();
                Toast.makeText(activity, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void showLoading(){
        loadingDialog.show();
    }

    void dismisLoading(){
        if(loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        adManager.OnBackInterstitalAd(session.getInt(interstitalCount),session.getString(interstital_adunit));
        super.onBackPressed();
    }
}