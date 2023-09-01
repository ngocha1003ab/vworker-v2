package com.vmedia.vworkers.offerwall;

import static com.vmedia.vworkers.utils.Constant.Sdk_key;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.vmedia.vworkers.R;
import com.vmedia.vworkers.databinding.LayoutProgressLoadingBinding;
import com.vmedia.vworkers.utils.Constant;
import com.vmedia.vworkers.utils.Fun;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJPrivacyPolicy;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;

import java.util.Hashtable;

public class O_Tapjoys extends Activity implements TJPlacementListener {
    Activity activity;
    public String TAG= "Tapjoy-offer";
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activity= O_Tapjoys.this;
        dialog=Fun.loadingProgress(activity);
        dialog.show();

        Tapjoy.setActivity(activity);
        Tapjoy.optOutAdvertisingID(this, true);
        TJPrivacyPolicy tjPrivacyPolicy = Tapjoy.getPrivacyPolicy();
        tjPrivacyPolicy.setSubjectToGDPR(true);
        tjPrivacyPolicy.setUserConsent("false");
        tjPrivacyPolicy.setBelowConsentAge(false);
        tjPrivacyPolicy.setUSPrivacy("1YYY");

        Bundle bundle1= getIntent().getExtras();

        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING,bundle1.getBoolean(Constant.TestMode)); // Disable this in production builds
        connectFlags.put(TapjoyConnectFlag.USER_ID,Constant.User_ID);
        TJPlacementListener placementListener = this;
        Tapjoy.connect(getApplicationContext(), bundle1.getString(Sdk_key), connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                Fun.log(TAG+ " onConnectSuccess: " );

                TJPlacement p = Tapjoy.getPlacement(bundle1.getString("placement"), placementListener);
                Tapjoy.setDebugEnabled(true);

                if(Tapjoy.isConnected()) {
                    p.requestContent();
                } else {
                    Fun.log(TAG+ " Tapjoy SDK must finish connecting before requesting content.: " );
                }

                if(p.isContentReady()) {
                    if(dialog.isShowing()){dialog.dismiss();}
                    p.showContent();
                }
                else {
                    p.requestContent();
                    if(dialog.isShowing()){dialog.dismiss();}
                    Toast.makeText(activity,"Preparing Offers For You.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                Fun.log(TAG+ " check__: "+p.isContentAvailable()  );

            }

            @Override
            public void onConnectFailure() {
                if(dialog.isShowing()){dialog.dismiss();}
                Fun.log(TAG+ " onConnectFailure: " );
            }
        });

    }


    @Override
    protected void onStart() {
        Tapjoy.onActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        if(dialog.isShowing()){dialog.dismiss();}
        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(dialog.isShowing()){dialog.dismiss();}
        finish();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Tapjoy.endSession();
        if(dialog.isShowing()){dialog.dismiss();}
        finish();
        super.onBackPressed();
    }

    @Override
    public void onRequestSuccess(TJPlacement tjPlacement) {
       if(tjPlacement.isContentAvailable()){
           tjPlacement.showContent();
       }
    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {

    }

    @Override
    public void onContentReady(TJPlacement tjPlacement) {
        if(tjPlacement.isContentReady()){
            tjPlacement.showContent();
        }
    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {
        tjPlacement.showContent();
    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {

    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

    }

    @Override
    public void onClick(TJPlacement tjPlacement) {

    }
}


