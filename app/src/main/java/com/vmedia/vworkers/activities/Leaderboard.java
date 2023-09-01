package com.vmedia.vworkers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.vmedia.vworkers.R;
import com.vmedia.vworkers.adapters.ViewpagerAdapter;
import com.vmedia.vworkers.databinding.ActivityLeaderboardBinding;
import com.vmedia.vworkers.fragment.FaqBottomDialogFragment;
import com.vmedia.vworkers.fragment.LeaderboardFragment;
import com.vmedia.vworkers.utils.Const;
import com.vmedia.vworkers.utils.Fun;
import com.google.android.material.tabs.TabLayout;

public class Leaderboard extends AppCompatActivity {
    ActivityLeaderboardBinding bind;
    Leaderboard activity;
    private ViewpagerAdapter catadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind= ActivityLeaderboardBinding.inflate(getLayoutInflater());
        Fun.statusbar(this);
        setContentView(bind.getRoot());
        activity=this;

        viewPager = bind.catviewpager;
        tabLayout= bind.tablayout;
        catadapter= new ViewpagerAdapter(getSupportFragmentManager());
        catadapter.AddFragment(new LeaderboardFragment("0"),"test");
        catadapter.AddFragment(new LeaderboardFragment("1"),"test");
        catadapter.AddFragment(new LeaderboardFragment("2"),"test");
        viewPager.setAdapter(catadapter);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.today));
        tabLayout.getTabAt(1).setText(getString(R.string.this_week));
        tabLayout.getTabAt(2).setText(getString(R.string.all_time));

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });

        bind.faq.setOnClickListener(view -> {
            Const.FAQ_TYPE="leaderboard";
            FaqBottomDialogFragment fragment =  FaqBottomDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(),FaqBottomDialogFragment.TAG);
        });
    }

}