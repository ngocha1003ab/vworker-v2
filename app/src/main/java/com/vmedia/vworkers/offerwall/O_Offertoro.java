package com.vmedia.vworkers.offerwall;

import static com.vmedia.vworkers.utils.Constant.AppID;
import static com.vmedia.vworkers.utils.Constant.SecureKey;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.vmedia.vworkers.utils.Fun;
import com.vmedia.vworkers.utils.Pref;
import com.offertoro.sdk.OTOfferWallSettings;
import com.offertoro.sdk.interfaces.OfferWallListener;
import com.offertoro.sdk.sdk.OffersInit;


public class O_Offertoro extends Activity {
    private AlertDialog dialog;
    private OfferWallListener offerWallListener;
    private OffersInit offersInit;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        dialog= Fun.loadingProgress(this);
        dialog.show();

        Bundle data =getIntent().getExtras();
        try {
            OTOfferWallSettings.getInstance().configInit(data.getString(AppID),data.getString(SecureKey),Pref.User_id(this));
            offersInit = OffersInit.getInstance();
            offerWallListener = new OfferWallListener() {
                @Override
                public void onOTOfferWallInitSuccess() {
                    if (dialog.isShowing()){ dialog.dismiss();}
                    offersInit.showOfferWall(O_Offertoro.this);
                }

                @Override
                public void onOTOfferWallInitFail(String s) {
                    if (dialog.isShowing()){ dialog.dismiss();}
                    uiToast("" + s);
                    finish();
                }

                @Override
                public void onOTOfferWallOpened() {

                }

                @Override
                public void onOTOfferWallCredited(double v, double v1) {

                }

                @Override
                public void onOTOfferWallClosed() {
                    finish();
                }
            };
            offersInit.create(this);
            offersInit.setOfferWallListener(offerWallListener);
        } catch (Exception e) {
            if (dialog.isShowing()){ dialog.dismiss();}
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }


    private void uiToast(final String toast) {
        runOnUiThread(() -> Toast.makeText(O_Offertoro.this, toast, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onDestroy() {
        offerWallListener = null;
        offersInit = null;
        super.onDestroy();
    }
}