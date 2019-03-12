package com.m.aspirego.user_module.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.user_module.activities.OffersCategorySearch;
import com.m.aspirego.user_module.adapters.OffersAdapter;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.user_module.models.Offer;
import com.m.aspirego.user_module.models.OffersListResponce;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.m.aspirego.user_module.presenter.RetrofitApis;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Offers_fragment extends Fragment implements View.OnClickListener,OffersAdapter.ViewOffersDetailsListerner {

    @BindView(R.id.recyclerview_offerslist)
    RecyclerView recyclerview_offerslist;

    @BindView(R.id.back_btn)
    ImageView backBtn;

    @BindView(R.id.title_view)
    TextView titleView;

    @BindView(R.id.offer_image)
    ImageView offer_image;

    @BindView(R.id.offer_title)
    TextView offer_title;

    @BindView(R.id.offer_address)
            TextView offer_address;

    @BindView(R.id.offer_org_price)
    TextView offer_org_price;

    @BindView(R.id.offer_price)
    TextView offer_price;

    @BindView(R.id.offer_percent)
    TextView offer_percent;

    @BindView(R.id.offer_validity)
    TextView offer_validity;

    @BindView(R.id.offers_details_view)
    RelativeLayout offers_details_view;

    @BindView(R.id.tv_nodata)
     TextView tv_nodata;

    @BindView(R.id.iv_call)
    ImageView iv_call;
    @BindView(R.id.iv_search)
    ImageView iv_search;

    RetrofitApis retrofitApis;
    private Dialog dialog;
    OffersAdapter adapter;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    String str_merchantmobilenumber;
    private int listcount;
    private ArrayList<Offer> offerArrayList=new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      retrofitApis=  RetrofitApis.Factory.create(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_offers, container, false);
        ButterKnife.bind(this,view);
        backBtn.setOnClickListener(this);
        iv_call.setOnClickListener(this);
        iv_search.setVisibility(View.VISIBLE);
        iv_search.setOnClickListener(this);
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

        recyclerview_offerslist.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new OffersAdapter(getContext(),offerArrayList);
        adapter.setListerner(Offers_fragment.this);
        recyclerview_offerslist.setAdapter(adapter);

        if(connectionDetector.isConnectingToInternet())
        callService("15","0");
        else
        callToast("You've no internet connection. Please try again.");

        recyclerview_offerslist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = ((LinearLayoutManager)recyclerView.getLayoutManager());
                int pos = layoutManager.findLastCompletelyVisibleItemPosition();
                int numItems = recyclerView.getAdapter().getItemCount();
                if((pos+1)!=listcount) {
                    if ((pos + 1) >= numItems) {
                        if (connectionDetector.isConnectingToInternet()) {
                            callService("15", "" + numItems);
                        } else {
                            callToast("You've no internet connection. Please try again.");
                        }
                    }
                }
            }
        });
    }

    private void callService(String limit,String offset) {
        Call<OffersListResponce> call=retrofitApis.getOffers(limit,offset,sessionManagement.getValueFromPreference(SessionManagement.USERLATTITUDE),sessionManagement.getValueFromPreference(SessionManagement.USERLONGNITUDE));
        dialog.show();
        call.enqueue(new Callback<OffersListResponce>() {
            @Override
            public void onResponse(Call<OffersListResponce> call, Response<OffersListResponce> response) {
           if(dialog!=null)
               dialog.dismiss();
           if (response.isSuccessful()){
              OffersListResponce listResponce= response.body();
           if(listResponce.getOffers()!=null && listResponce.getOffers().size()>0) {
               offerArrayList.addAll(listResponce.getOffers());
               listcount=response.body().getTotalcount();
               tv_nodata.setVisibility(View.GONE);
           }
           else
               tv_nodata.setVisibility(View.VISIBLE);
           }
            adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<OffersListResponce> call, Throwable t) {
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
    public void onClick(View view) {
        if(view.getId()==R.id.back_btn) {
            visibleOfferDetails(View.GONE);
            recyclerview_offerslist.setVisibility(View.VISIBLE);
            titleView.setText("offers");
            iv_search.setVisibility(View.VISIBLE);
        }
        else if(view.getId()==R.id.iv_call)
        {
            if(str_merchantmobilenumber!=null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + str_merchantmobilenumber));
                startActivity(intent);
            }
            else
                callToast("Mobile number not available  right now.");
        }
        else if(view.getId()==R.id.iv_search)
        {
            Intent intent=new Intent(getActivity(), OffersCategorySearch.class);
            startActivity(intent);
        }
    }

    @Override
    public void onOffersClick(Offer offer) {
        Picasso.with(getContext()).load(ApiUrls.OFFERS_IMAGEPATH+offer.getImage()).placeholder(R.mipmap.profile_img)
                .error(R.mipmap.profile_img)
                .into(offer_image);
        if(offer.getMobile_number()!=null)
          str_merchantmobilenumber=offer.getMobile_number();
        else
            str_merchantmobilenumber=null;

        titleView.setText("offer Details");
        visibleOfferDetails(View.VISIBLE);
        iv_search.setVisibility(View.GONE);
        recyclerview_offerslist.setVisibility(View.GONE);
        offer_address.setText(offer.getAddress());
        offer_title.setText(offer.getOfferName());
        offer_org_price.setText("Orgilan price : "+getContext().getString(R.string.rs)+" "+offer.getPrice());
        offer_price.setText(getContext().getString(R.string.rs)+" "+offer.getOfferPrice());
          offer_percent.setText(offer.getDiscount()+" % "+"off");
          offer_validity.setText("offer valid from "+offer.getValidFrom()+" to "+offer.getValidTo());
    }
    public void visibleOfferDetails(int visiblility){
        offers_details_view.setVisibility(visiblility);
        backBtn.setVisibility(visiblility);
        iv_call.setVisibility(visiblility);
    }
}
