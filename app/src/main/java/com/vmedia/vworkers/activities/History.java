package com.vmedia.vworkers.activities;

import static com.vmedia.vworkers.utils.AdsConfig.bannerID;
import static com.vmedia.vworkers.utils.AdsConfig.banner_type;
import static com.vmedia.vworkers.utils.AdsConfig.interstitalCount;
import static com.vmedia.vworkers.utils.AdsConfig.interstital_adunit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.vmedia.vworkers.R;
import com.vmedia.vworkers.adapters.ViewpagerAdapter;
import com.vmedia.vworkers.ads.AdManager;
import com.vmedia.vworkers.databinding.ActivityHistoryBinding;
import com.vmedia.vworkers.fragment.FaqBottomDialogFragment;
import com.vmedia.vworkers.utils.Const;
import com.vmedia.vworkers.utils.Fun;
import com.vmedia.vworkers.utils.Pref;
import com.google.android.material.tabs.TabLayout;

public class History extends AppCompatActivity {
    ActivityHistoryBinding bind;
    private ViewpagerAdapter catadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    AdManager adManager;
    Pref pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityHistoryBinding.inflate(getLayoutInflater());
        Fun.statusbar(this);
        setContentView(bind.getRoot());

        pref=new Pref(this);
        adManager = new AdManager(this);
        adManager.loadBannerAd(bind.BANNER,pref.getString(banner_type),pref.getString(bannerID));

        bind.tool.title.setText(getString(R.string.history));

        viewPager = bind.catviewpager;
        tabLayout= bind.tablayout;
        catadapter= new ViewpagerAdapter(getSupportFragmentManager());
        catadapter.AddFragment(new CoinHistory(),"test");
        catadapter.AddFragment(new RewardHistory(),"test");
        viewPager.setAdapter(catadapter);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.coin_history));
        tabLayout.getTabAt(1).setText(getString(R.string.reward_history));

        bind.tool.back.setOnClickListener(v -> {
            onBackPressed();
        });

        bind.tool.faq.setOnClickListener(view -> {
            Const.FAQ_TYPE="history";
            FaqBottomDialogFragment fragment =  FaqBottomDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(),FaqBottomDialogFragment.TAG);
        });
    }

    @Override
    public void onBackPressed() {
        adManager.OnBackInterstitalAd(pref.getInt(interstitalCount),pref.getString(interstital_adunit));
        super.onBackPressed();
    }
}