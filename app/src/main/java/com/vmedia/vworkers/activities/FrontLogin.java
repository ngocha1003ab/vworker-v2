package com.vmedia.vworkers.activities;

import static com.vmedia.vworkers.utils.Const.randomCode;
import static com.vmedia.vworkers.utils.Fun.d;
import static com.vmedia.vworkers.utils.Fun.generateRandomString;
import static com.vmedia.vworkers.utils.Fun.sec;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vmedia.vworkers.R;
import com.vmedia.vworkers.Responsemodel.LoginResp;
import com.vmedia.vworkers.databinding.ActivityLoginBinding;
import com.vmedia.vworkers.databinding.LayoutAlertBinding;
import com.vmedia.vworkers.utils.Const;
import com.vmedia.vworkers.utils.Fun;
import com.vmedia.vworkers.utils.Pref;
import com.vmedia.vworkers.restApi.ApiClient;
import com.vmedia.vworkers.restApi.ApiInterface;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrontLogin extends AppCompatActivity {
    ActivityLoginBinding binding;
    Activity activity;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    AlertDialog loadingDialog,alert;
    Pref pref;
    LayoutAlertBinding lytAlert;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity=FrontLogin.this;

        loadingDialog= Fun.loading(activity);
        lytAlert=LayoutAlertBinding.inflate(getLayoutInflater());
        alert= Fun.Alerts(activity,lytAlert);
        pref = new Pref(activity);

        if(pref.isUserLogin() && pref.getString(pref.ATYPE).equals("email")){
            binding.email.setText(pref.getString(pref.EMAIL));
            binding.password.setText(pref.getString(pref.PASSWORD));
            LoginUser(pref.getString(pref.EMAIL), pref.getString(pref.PASSWORD));
        }

        binding.forgetpas.setOnClickListener(v -> {
            startActivity(new Intent(activity,ForgotPassword.class));
            Animatoo.animateSlideUp(activity);
        });

        binding.terms.setOnClickListener(v -> {
            Fun.launchCustomTabs(activity,Pref.getBaseURI(activity)+"privacy-policy");
        });

        binding.tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(activity,FrontSignup.class));
        });

        binding.signin.setOnClickListener(v -> {
            if(!Fun.isConnected(this)){
                Fun.msgError(activity,getString(R.string.no_internet));
            }

            if(!validateData()){
                return;
            }else {
                LoginUser(binding.email.getText().toString().trim(),binding.password.getText().toString().trim());
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        binding.loginGoogle.setOnClickListener(v -> {
            signIn();
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            LoginUserGoogle(acct.getDisplayName(), acct.getEmail(), String.valueOf(acct.getPhotoUrl()), acct.getId());

        } catch (ApiException e) {
            Fun.log("Login Activtiy signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void LoginUserGoogle(String displayName, String email, String photoUrl, String id) {
        OSDeviceState device = OneSignal.getDeviceState();
        showLoading();
        String data=d(activity,displayName,email,null,id,"google",device.getUserId());
        String secure=sec(activity,photoUrl,Const.checkAppStatus);

        if(data==null || secure==null){
            dismisLoading();
            Fun.msgError(activity,"Something went wrong 00");
        }

        Call<LoginResp> call = ApiClient.restAdapter(activity).create(ApiInterface.class).Signup(data,secure);
        call.enqueue(new Callback<LoginResp>() {
            @Override
            public void onResponse(Call<LoginResp> call, Response<LoginResp> response) {
                dismisLoading();
                try {
                    if (response.isSuccessful() && response.body().getCode().equals("201")) {
                        randomCode = generateRandomString(response.body().getResp(),response.body().getUser().getRefferalId());
                        pref.saveUserData(response.body(),"","google");
                        startActivity(new Intent(activity, MainActivity.class));
                    }else{
                        showAlert(response.body().getMessage());
                    }
                }catch (Exception e){

                }
            }
            @Override
            public void onFailure(Call<LoginResp> call, Throwable t) {
                dismisLoading();
            }
        });
    }

    private void LoginUser(String email, String password) {
        showLoading();
        Call<LoginResp> call = ApiClient.restAdapter(activity).create(ApiInterface.class).Login(d(activity,"",email,password,null,"email",""),sec(activity,null,Const.checkAppStatus));
        call.enqueue(new Callback<LoginResp>() {
            @Override
            public void onResponse(Call<LoginResp> call, Response<LoginResp> response) {
                dismisLoading();
               try {
                   if(response.isSuccessful() && response.body().getCode().equals("201")){
                       randomCode = generateRandomString(response.body().getResp(),response.body().getUser().getRefferalId());
                       pref.setIntData(pref.WALLET,response.body().getUser().getBalance());
                        pref.saveUserData(response.body(),password,"email");
                        Intent intent = new Intent(FrontLogin.this, MainActivity.class);
                        startActivity(intent);
                   }else{
                       showAlert(response.body().getMessage());
                   }
               }catch (Exception e){
                   Toast.makeText(FrontLogin.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
               }
            }
            @Override
            public void onFailure(Call<LoginResp> call, Throwable t) {
                dismisLoading();
            }
        });
    }

    void showAlert(String msg){
        alert.show();
        lytAlert.negative.setText(getString(R.string.close));
        lytAlert.title.setText(getString(R.string.log_in)+getString(R.string.error));
        lytAlert.msg.setText(msg);
        lytAlert.negative.setOnClickListener(v -> {
            alert.dismiss();
        });
    }

    private boolean validateData() {
        boolean valid= true;
        if(TextUtils.isEmpty(binding.email.getText().toString().trim())){
            binding.email.setError(getResources().getString(R.string.error_field_required));
            valid=false;
        }
        else if(!binding.email.getText().toString().trim().matches(emailPattern)){
            binding.email.setError(getResources().getString(R.string.error_invalid_email));
            valid=false;
        }

        if(TextUtils.isEmpty(binding.password.getText().toString().trim())){
            binding.password.setError(getResources().getString(R.string.error_invalid_password));
            valid=false;
        }
        return valid;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideLeft(activity);
    }

    void showLoading(){
        loadingDialog.show();
    }

    void dismisLoading(){
        if(loadingDialog.isShowing())
            loadingDialog.dismiss();
    }
}
