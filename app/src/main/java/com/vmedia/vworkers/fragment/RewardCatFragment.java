package com.vmedia.vworkers.fragment;

import static com.vmedia.vworkers.utils.Const.MIN_COIN;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmedia.vworkers.R;
import com.vmedia.vworkers.Responsemodel.RewardCatResp;
import com.vmedia.vworkers.adapters.RewardCatAdapter;
import com.vmedia.vworkers.ads.AdManager;
import com.vmedia.vworkers.databinding.FragmentRewardCatBinding;
import com.vmedia.vworkers.restApi.ApiClient;
import com.vmedia.vworkers.restApi.ApiInterface;
import com.vmedia.vworkers.utils.Pref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardCatFragment extends Fragment {

    FragmentRewardCatBinding bind;
    Activity activity;
    RewardCatAdapter adapter;
    List<RewardCatResp> list;
    AdManager adManager;
    Pref pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind=FragmentRewardCatBinding.inflate(getLayoutInflater());
        activity=getActivity();
        pref=new Pref(activity);

        list = new ArrayList<>();
        bind.rv.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new RewardCatAdapter(activity, list);
        bind.rv.setAdapter(adapter);

        getdata();

        updateProgress();

        return bind.getRoot();
    }

    private void updateProgress() {
        bind.progressBar.setMax(MIN_COIN);
        bind.progressBar.makeProgress(pref.getBalance());
        bind.tbcoin.setText(""+pref.getBalance());
    }

    private void getdata() {
        Call<List<RewardCatResp>> call = ApiClient.restAdapter(activity).create(ApiInterface.class).getRewardCategory();
        call.enqueue(new Callback<List<RewardCatResp>>() {
            @Override
            public void onResponse(Call<List<RewardCatResp>> call, Response<List<RewardCatResp>> response) {
                if (response.isSuccessful() && response.body().size()!= 0) {
                    MIN_COIN=response.body().get(0).getMin_coin();
                    updateProgress();
                    bindData(response);
                } else {
                    noResult();
                }
            }
            @Override
            public void onFailure(Call<List<RewardCatResp>> call, Throwable t) {
                noResult();
            }
        });
    }

    private void bindData(Response<List<RewardCatResp>> response) {
        for(int i=0; i<response.body().size(); i++){
            list.add(response.body().get(i));
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
    public void onResume() {
        super.onResume();
        updateProgress();
    }
}