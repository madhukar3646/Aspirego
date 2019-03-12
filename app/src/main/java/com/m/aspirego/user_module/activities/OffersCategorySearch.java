package com.m.aspirego.user_module.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.user_module.adapters.CategoriesStoreAdapter;
import com.m.aspirego.user_module.adapters.OffersAdapter;
import com.m.aspirego.user_module.adapters.OffersAutoSearchAdapter;
import com.m.aspirego.user_module.models.CategoryModel;
import com.m.aspirego.user_module.models.CategoryStoreModel;
import com.m.aspirego.user_module.models.Offer;
import com.m.aspirego.user_module.models.OffersListResponce;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.m.aspirego.user_module.presenter.RetrofitApis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OffersCategorySearch extends AppCompatActivity implements OffersAutoSearchAdapter.AutoSearchClicklistener,OffersAdapter.ViewOffersDetailsListerner{

    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.recyclerview_autosearch)
    RecyclerView recyclerview_autosearch;
    @BindView(R.id.recyclerview_stores)
    RecyclerView recyclerview_stores;
    @BindView(R.id.tv_nodata)
    TextView tv_nodata;
    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;

    private List<CategoryModel.Category> autosearchlist,auto_resultlist;
    private OffersAutoSearchAdapter autoSearchAdapter;
    private String fiterString = "";
    RetrofitApis retrofitApis;

    private ArrayList<Offer> offerArrayList;
    private OffersAdapter offersAdapter;
    private String cat_id;
    private int listcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_category_search);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        retrofitApis=  RetrofitApis.Factory.create(OffersCategorySearch.this);
        dialog = new Dialog(OffersCategorySearch.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector = new ConnectionDetector(OffersCategorySearch.this);
        sessionManagement= SessionManagement.getSession(OffersCategorySearch.this);

        offerArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(OffersCategorySearch.this);
        layoutManager.setReverseLayout(false);
        recyclerview_stores.setLayoutManager(layoutManager);
        offersAdapter = new OffersAdapter(OffersCategorySearch.this, offerArrayList);
        offersAdapter.setListerner(this);
        recyclerview_stores.setAdapter(offersAdapter);

        autosearchlist=new ArrayList<>();
        auto_resultlist=new ArrayList<>();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(OffersCategorySearch.this);
        layoutManager1.setReverseLayout(false);
        autosearchlist=new ArrayList<>();
        recyclerview_autosearch.setLayoutManager(layoutManager1);
        autoSearchAdapter=new OffersAutoSearchAdapter(OffersCategorySearch.this,auto_resultlist,this);
        recyclerview_autosearch.setAdapter(autoSearchAdapter);

        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                fiterString = s.toString();
                filter(fiterString);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(connectionDetector.isConnectingToInternet())
            categoryService();
        else
            callToast("You've no internet connection. Please try again.");

        recyclerview_stores.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            offersService(cat_id,"15", "" + numItems);
                        } else {
                            callToast("You've no internet connection. Please try again.");
                        }
                    }
                }
            }
        });
    }

    private void categoryService() {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<CategoryModel> call = service.categoriesService();
        call.enqueue(new Callback<CategoryModel>() {
            @Override
            public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                dialog.dismiss();

                autosearchlist.clear();
                auto_resultlist.clear();
                CategoryModel model = response.body();
                if (model != null) {
                    Log.e("categorieslist size", "size" + model.getCategories().size());
                    if (model.getCategories() != null) {
                        autosearchlist.addAll(model.getCategories());
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    private void filter(String text) {
        if(text.trim().length()>0) {
            recyclerview_stores.setVisibility(View.GONE);
            recyclerview_autosearch.setVisibility(View.VISIBLE);
            auto_resultlist.clear();
            for (int i = 0; i < autosearchlist.size(); i++) {
                if (autosearchlist.get(i).getName().toLowerCase().startsWith(text.toLowerCase())) {
                    auto_resultlist.add(autosearchlist.get(i));
                }
            }
            autoSearchAdapter.notifyDataSetChanged();
        }
        else {
            auto_resultlist.clear();
            autoSearchAdapter.notifyDataSetChanged();
        }
    }

    private void callToast(String msg) {
        Toast.makeText(OffersCategorySearch.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAutoSearchClicked(CategoryModel.Category model) {
        if (connectionDetector.isConnectingToInternet()) {
            offerArrayList.clear();
            offersAdapter.notifyDataSetChanged();
            recyclerview_stores.setVisibility(View.VISIBLE);

            auto_resultlist.clear();
            autoSearchAdapter.notifyDataSetChanged();
            recyclerview_autosearch.setVisibility(View.GONE);
            et_search.setText("");
            cat_id=model.getId();
            offersService(cat_id,"15","0");
        }
        else
            callToast("You've no internet connection. Please try again.");
    }

    private void offersService(String category_id,String limit,String offset) {
        Call<OffersListResponce> call=retrofitApis.categoryOffers(limit,offset,sessionManagement.getValueFromPreference(SessionManagement.USERLATTITUDE),sessionManagement.getValueFromPreference(SessionManagement.USERLONGNITUDE),category_id);
        dialog.show();
        call.enqueue(new Callback<OffersListResponce>() {
            @Override
            public void onResponse(Call<OffersListResponce> call, Response<OffersListResponce> response) {
                if(dialog!=null)
                    dialog.dismiss();
                offerArrayList.clear();
                offersAdapter.notifyDataSetChanged();
                if (response.isSuccessful()){
                    OffersListResponce listResponce= response.body();
                    if(listResponce.getOffers()!=null && listResponce.getOffers().size()>0) {
                        offerArrayList.addAll(listResponce.getOffers());
                        listcount=response.body().getTotalcount();
                    }
                }
                offersAdapter.notifyDataSetChanged();
                if (offerArrayList.size() > 0)
                    tv_nodata.setVisibility(View.GONE);
                else
                    tv_nodata.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<OffersListResponce> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(recyclerview_autosearch.getVisibility()==View.VISIBLE) {
            recyclerview_autosearch.setVisibility(View.GONE);
            recyclerview_stores.setVisibility(View.VISIBLE);
            et_search.setText("");
        }
        else {
            finish();
        }
    }

    @Override
    public void onOffersClick(Offer offer) {
        Intent intent=new Intent(OffersCategorySearch.this,OfferDetailsActivity.class);
        intent.putExtra("offer",offer);
        startActivity(intent);
    }
}
