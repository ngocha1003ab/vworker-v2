package com.vmedia.vworkers.fragment;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmedia.vworkers.R;
import com.vmedia.vworkers.activities.FaqActivity;
import com.vmedia.vworkers.activities.History;
import com.vmedia.vworkers.activities.Leaderboard;
import com.vmedia.vworkers.activities.NotificationActivity;
import com.vmedia.vworkers.activities.ProfileActivity;
import com.vmedia.vworkers.activities.SettingActivity;
import com.vmedia.vworkers.databinding.FragmentProfileBinding;
import com.vmedia.vworkers.databinding.LayoutAlertBinding;
import com.vmedia.vworkers.restApi.WebApi;
import com.vmedia.vworkers.support.SupportActivity;
import com.vmedia.vworkers.utils.Pref;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

public class Profile extends Fragment {
    FragmentProfileBinding bind;
    Activity activity;
    Pref pref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind=FragmentProfileBinding.inflate(getLayoutInflater());

        activity=getActivity();
        pref=new Pref(activity);


        bind.username.setText(pref.getString(pref.NAME));
        bind.coins.setText(String.valueOf(pref.getBalance()));
        bind.promocoin.setText(String.valueOf(pref.getInt(pref.PROMO_WALLET)));

        ChipNavigationBar chipNavigationBar= getActivity().findViewById(R.id.navigation);
        chipNavigationBar.setItemSelected(R.id.navigation_profile,true);

        bind.cvAccount.setOnClickListener(view -> {
            startActivity(new Intent(activity, ProfileActivity.class));
        });


        if (pref.getString(pref.PROFILE) != null) {
            if (pref.getString(pref.PROFILE).equals("userpro.png")) {
                bind.profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
            } else if (pref.getString(pref.PROFILE).startsWith("http")) {
                Picasso.get().load(pref.getString(pref.PROFILE)).error(R.drawable.ic_user).fit().into(bind.profile);
            } else {
                Picasso.get().load(Pref.getBaseURI(activity)+WebApi.Api.USERTHUMB + pref.getString(pref.PROFILE)).error(R.drawable.ic_user).fit().into(bind.profile);
            }
        } else {
            bind.profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
        }


        bind.cvFaq.setOnClickListener(v -> {
            startActivity(new Intent(activity, FaqActivity.class).putExtra("type","faq"));
        });

        bind.cvHistory.setOnClickListener(v -> {
            startActivity(new Intent(activity, History.class));
        });

        bind.cvLeaderboard.setOnClickListener(v -> {
            startActivity(new Intent(activity, Leaderboard.class));
        });

        bind.cvSupport.setOnClickListener(v -> {
            startActivity(new Intent(activity, SupportActivity.class));
        });

        bind.cvNotification.setOnClickListener(v -> {
            startActivity(new Intent(activity, NotificationActivity.class));
        });

        bind.cvSetting.setOnClickListener(v -> {
            startActivity(new Intent(activity, SettingActivity.class));
        });


        return bind.getRoot();
    }


}