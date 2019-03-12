package com.m.aspirego.user_module.fragments;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.user_module.activities.MerchantResponsesActivity;
import com.m.aspirego.user_module.adapters.FeedsAdapter;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.user_module.models.UserRequirementsListResponse;
import com.m.aspirego.user_module.presenter.RetrofitApis;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Feed_fragment extends Fragment implements View.OnClickListener,FeedsAdapter.ViewFullDetailsListener {

    @BindView(R.id.recyclerview_feedslist)
    RecyclerView recyclerview_feedslist;

    @BindView(R.id.title_view)
    TextView titleView;
    FeedsAdapter adapter;
    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    RetrofitApis retrofitApis;
    private Dialog dialog;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this,view);
        init(view);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        retrofitApis=  RetrofitApis.Factory.create(context);

    }
    private void init(View view)
    {
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(getContext());
        sessionManagement= SessionManagement.getSession(getContext());
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(connectionDetector.isConnectingToInternet())
            callService();
        else
            callToast("You've no internet connection. Please try again.");
    }

    private void callToast(String msg)
    {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onViewFullDetails(UserRequirementsListResponse.Requirements feed) {
        if(feed!=null){
            Intent intent=new Intent(getActivity(), MerchantResponsesActivity.class);
            intent.putExtra("requirementid",feed.getId());
            intent.putExtra("imagepath",feed.getPhoto());
            startActivity(intent);
        }
    }

    private void callService() {
       Call<UserRequirementsListResponse> call=retrofitApis.userRequirementsList(sessionManagement.getValueFromPreference(SessionManagement.USERID));
        dialog.show();
        call.enqueue(new Callback<UserRequirementsListResponse>() {
            @Override
            public void onResponse(Call<UserRequirementsListResponse> call, Response<UserRequirementsListResponse> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if (response.isSuccessful()){
                    recyclerview_feedslist.setLayoutManager(new LinearLayoutManager(getContext()));
                    if(response.body().getRequirements()!=null && response.body().getRequirements().size()>0) {
                        tv_nodata.setVisibility(View.GONE);
                        adapter=new FeedsAdapter(getContext(),response.body().getRequirements());
                        adapter.setListener(Feed_fragment.this);
                        recyclerview_feedslist.setAdapter(adapter );
                    }
                    else
                        tv_nodata.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<UserRequirementsListResponse> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
    }
}
