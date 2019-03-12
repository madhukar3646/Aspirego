package com.m.aspirego.merchant_module.fragments;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.activities.EditOfferActivity;
import com.m.aspirego.merchant_module.adapters.MarchantOffersAdapter;
import com.m.aspirego.merchant_module.models.MOffersResponce;
import com.m.aspirego.user_module.models.Offer;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.activities.OfferDetailsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class Offers_fragment extends Fragment implements MarchantOffersAdapter.ViewOffersDetailsListerner,View.OnClickListener {
    @BindView(R.id.recyclerview_offerslist)
    RecyclerView recyclerview_offerslist;

    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    @BindView(R.id.add_offer)
    ImageView add_offer;

    private Dialog dialog;
    MarchantOffersAdapter adapter;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_offers_fragment,container,false);
        ButterKnife.bind(this,view);
        add_offer.setOnClickListener(this);
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(getContext());
        sessionManagement= SessionManagement.getSession(getContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(connectionDetector.isConnectingToInternet())
            callService();
        else
            callToast("You've no internet connection. Please try again.");
    }

    private void callService() {
        Call<MOffersResponce> call=RetrofitMerchantApis.Factory.create(getContext()).getOffers(sessionManagement.getValueFromPreference(SessionManagement.USERID));
        dialog.show();
        call.enqueue(new Callback<MOffersResponce>() {
            @Override
            public void onResponse(Call<MOffersResponce> call, Response<MOffersResponce> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if (response.isSuccessful()){
                    MOffersResponce listResponce= response.body();
                    recyclerview_offerslist.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter=new MarchantOffersAdapter(getContext(),listResponce.getOffers());
                    recyclerview_offerslist.setAdapter(adapter);
                    adapter.setListerner(Offers_fragment.this);
                    if(response.body().getOffers()!=null && response.body().getOffers().size()>0)
                        tv_nodata.setVisibility(View.GONE);
                    else
                        tv_nodata.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<MOffersResponce> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
    }

    private void callToast(String msg)
    {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOffersClick(Offer offer) {
        Intent intent=new Intent(getActivity(),OfferDetailsActivity.class);
        intent.putExtra("offer",offer);
        startActivity(intent);

    }

    @Override
    public void onEditOffer(Offer offer) {
        Intent intent=new Intent(getActivity(),EditOfferActivity.class);
        intent.putExtra(EditOfferActivity.ADD_OFFER,false);
        intent.putExtra(EditOfferActivity.ISEDIT,offer);
        startActivityForResult(intent,100);

    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(getActivity(),EditOfferActivity.class);
        intent.putExtra(EditOfferActivity.ADD_OFFER,true);
        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==100){
                callService();
            }

        }
    }
}
