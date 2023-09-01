package com.vmedia.vworkers.offerwall;

import static com.vmedia.vworkers.utils.Constant.*;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.adgatemedia.sdk.classes.AdGateMedia;
import com.adgatemedia.sdk.model.Conversion;
import com.adgatemedia.sdk.network.OnConversionsReceived;
import com.adgatemedia.sdk.network.OnOfferWallLoadFailed;
import com.vmedia.vworkers.utils.Fun;

import java.util.HashMap;
import java.util.List;

public class O_AdGateMedia extends Activity {
    Activity activity;
    public String TAG= "AdGateMedia-offer";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        activity= O_AdGateMedia.this;

        dialog= Fun.loadingProgress(activity);
        dialog.show();

        Bundle data=getIntent().getExtras();
        System.out.println("string_dtata "+data.toString());

        final HashMap<String, String> subids = new HashMap<String, String>();
        subids.put("s2",data.getString(User_ID));
        AdGateMedia.initializeSdk(this);
        AdGateMedia adGateMedia = AdGateMedia.getInstance();
        adGateMedia.loadOfferWall(activity,
                data.getString(AppID),
                data.getString(User_ID),
                subids,
                () -> AdGateMedia.getInstance().showOfferWall(activity,
                        () -> {
                            if(dialog.isShowing()){dialog.dismiss();}
                            finish();

                        }),
                new OnOfferWallLoadFailed() {
                    @Override
                    public void onOfferWallLoadFailed(String reason) {
                        Toast.makeText(activity, ""+reason, Toast.LENGTH_SHORT).show();
                        if(dialog.isShowing()){dialog.dismiss();}
                        finish();
                    }
                });

        adGateMedia.getConversions(data.getString("appid"),data.getString(User_ID), subids, new OnConversionsReceived() {
            @Override
            public void onSuccess(List<Conversion> conversions) {
                for (Conversion conversion : conversions)  {

                }
            }

            @Override
            public void onError(String message) {
                // Fired when any error occurs
            }
        });


    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //session end
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(dialog.isShowing()){dialog.dismiss();}
        finish();
        super.onDestroy();
    }
}


