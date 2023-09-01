package com.vmedia.vworkers.activities;

import static com.vmedia.vworkers.utils.AdsConfig.bannerID;
import static com.vmedia.vworkers.utils.AdsConfig.banner_type;
import static com.vmedia.vworkers.utils.Fun.js;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.vmedia.vworkers.R;
import com.vmedia.vworkers.Responsemodel.DefResp;
import com.vmedia.vworkers.ads.AdManager;
import com.vmedia.vworkers.databinding.ActivityVisitBinding;
import com.vmedia.vworkers.databinding.LayoutAlertBinding;
import com.vmedia.vworkers.restApi.ApiClient;
import com.vmedia.vworkers.restApi.ApiInterface;
import com.vmedia.vworkers.utils.Const;
import com.vmedia.vworkers.utils.Fun;
import com.vmedia.vworkers.utils.Pref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitActivity extends AppCompatActivity {
    ActivityVisitBinding bind;
    VisitActivity activity;
    String url,id;
    private boolean isCountdownFinish = false;
    private boolean isCountDownStart= false;
    private CountDownTimer countDownTimer = null;
    AlertDialog alert;
    LayoutAlertBinding lytAlert;
    Pref pref;
    AdManager adManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityVisitBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(bind.getRoot());
        activity=this;

        pref=new Pref(activity);
        adManager = new AdManager(activity);
        adManager.loadBannerAd(bind.BANNER,pref.getString(banner_type),pref.getString(bannerID));

        lytAlert= LayoutAlertBinding.inflate(getLayoutInflater());
        alert= Fun.Alerts(activity,lytAlert);

        url=getIntent().getStringExtra("url");
        id = getIntent().getStringExtra("id");

        WebSettings webSettings = bind.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        bind.webView.setWebViewClient(new MyWebViewClient());
        bind.webView.loadUrl(url);

    }

    private void preparead() {

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            bind.loader.setVisibility(View.VISIBLE);
            if(url.startsWith("upi:")){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }else{
                view.loadUrl(url);
            }

            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            bind.loader.setVisibility(View.GONE);
            if (countDownTimer == null) {
                if (!isCountdownFinish) {
                    isCountDownStart = true;
                    startCountdownTimer();
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            bind.loader.setVisibility(View.GONE);
            bind.webView.loadUrl("file:///android_asset/error.html");
        }
    }

    @Override
    protected void onResume() {
        if (isCountDownStart) {
            startCountdownTimer();
        }
        super.onResume();
    }

    private void startCountdownTimer() {
        preparead();
        if (countDownTimer != null) {
            return;
        }
        countDownTimer = new CountDownTimer(Const.GAME_MINUTE * 1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              bind.timeText.setText(""+(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                isCountdownFinish = true;
                credit();
            stopTimer();

            }
        }.start();

    }

    private void stopTimer() {
        if(countDownTimer!=null){
            countDownTimer.cancel();
            countDownTimer=null;
        }
    }

    @Override
    protected void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (countDownTimer != null) {
            showAlert();
        } else {
           /* if (ads.isLoaded()) {
                ads.showInterstitial();
            }*/
            stopTimer();
            super.onBackPressed();
            finish();
        }
    }

    void showAlert(){
        alert.show();
        lytAlert.title.setText(getString(R.string.are_you_sure));
        lytAlert.msg.setText(getString(R.string.go_back_task_msg));
        lytAlert.positive.setVisibility(View.VISIBLE);
        lytAlert.positive.setText(getString(R.string.no));
        lytAlert.negative.setText(getString(R.string.yes));
        lytAlert.positive.setOnClickListener(v -> {
            alert.dismiss();
        });

        lytAlert.negative.setOnClickListener(v -> {
            /*if (ads.isLoaded()) {
                ads.showInterstitial();
            }*/
            stopTimer();
            finish();
            super.onBackPressed();
        });
    }

    private void credit() {
        ApiClient.restAdapter(activity).create(ApiInterface.class).api(js(activity,"web",pref.User_id(),id,"","")).enqueue(new Callback<DefResp>() {
            @Override
            public void onResponse(Call<DefResp> call, Response<DefResp> response) {
                if (response.isSuccessful() && response.body().getCode().equals("201")) {
                    pref.UpdateWallet(response.body().getBalance());
                }
            }

            @Override
            public void onFailure(Call<DefResp> call, Throwable t) {
                Log.e("PlayGame", "onFailure: "+t.getMessage() );
            }
        });
    }
}