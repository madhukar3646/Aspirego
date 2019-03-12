package com.m.aspirego.merchant_module.fragments;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.activities.ReviewsActivity;
import com.m.aspirego.merchant_module.adapters.RequirementsAdapter;
import com.m.aspirego.merchant_module.models.RequirementsModel;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Requirement_fragment extends Fragment {

    @BindView(R.id.recyclerview_requirements)
    RecyclerView recyclerview_requirements;
    @BindView(R.id.iv_review)
    ImageView iv_review;
    private RequirementsAdapter requirementsAdapter;
    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    private List<RequirementsModel.Requirement> requirements;
    private List<RequirementsModel.MerchantTag> merchanttags;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_requirement_fragment, container, false);
        ButterKnife.bind(this, view);
        init(view);
        return view;
    }

    private void init(View view)
    {
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector = new ConnectionDetector(getActivity());
        sessionManagement=SessionManagement.getSession(getActivity());

        requirements = new ArrayList<>();
        merchanttags=new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(false);
        recyclerview_requirements.setLayoutManager(layoutManager);
        requirementsAdapter = new RequirementsAdapter(getActivity(), requirements,merchanttags);
        recyclerview_requirements.setAdapter(requirementsAdapter);

        if(connectionDetector.isConnectingToInternet())
        {
            getRequirementsService(sessionManagement.getValueFromPreference(SessionManagement.USERID));
        }
        else {
            callToast("You've no internet connection. Please try again.");
        }

        iv_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ReviewsActivity.class);
                intent.putExtra("merchant_id",sessionManagement.getValueFromPreference(SessionManagement.USERID));
                startActivity(intent);
            }
        });

    }

    private void getRequirementsService(String merchantid) {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MerchantApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMerchantApis service = retrofit.create(RetrofitMerchantApis.class);
        Call<RequirementsModel> call = service.getRequirements(merchantid);
        call.enqueue(new Callback<RequirementsModel>() {
            @Override
            public void onResponse(Call<RequirementsModel> call, Response<RequirementsModel> response) {
                dialog.dismiss();
                RequirementsModel model = response.body();
                if (model != null) {
                    if(model.getStatus()!=1)
                        callToast(model.getResult());
                   else if(model.getRequirements()!=null)
                    {
                        if(model.getMerchantTags()!=null)
                        {
                            merchanttags.clear();
                            merchanttags.addAll(model.getMerchantTags());
                        }
                        requirements.clear();
                        requirements.addAll(model.getRequirements());
                        requirementsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<RequirementsModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    private void callToast(String msg)
    {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }
}
