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
import com.m.aspirego.merchant_module.models.ProductListResponse;
import com.m.aspirego.user_module.adapters.AutoSearchAdapterDemo;
import com.m.aspirego.user_module.adapters.CategoriesStoreAdapter;
import com.m.aspirego.user_module.models.CategoryStoreModel;
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

public class AutoSearchActivity extends AppCompatActivity implements CategoriesStoreAdapter.onStoreClickListener,AutoSearchAdapterDemo.AutoSearchClicklistener{

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
    private List<CategoryStoreModel.Categorystore> categorystoreslist;
    private CategoriesStoreAdapter categoriesStoreAdapter;
    private Dialog dialog;
    private ConnectionDetector connectionDetector;

    private List<ProductListResponse.Product> autosearchlist,auto_resultlist;
    private AutoSearchAdapterDemo autoSearchAdapter;
    private String fiterString = "";
    private int listcount;
    SessionManagement sessionManagement;
    private String lattitude,longnitude,searchkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_search);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        dialog = new Dialog(AutoSearchActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector = new ConnectionDetector(AutoSearchActivity.this);
        sessionManagement= SessionManagement.getSession(getApplicationContext());
        lattitude=sessionManagement.getValueFromPreference(SessionManagement.USERLATTITUDE);
        longnitude=sessionManagement.getValueFromPreference(SessionManagement.USERLONGNITUDE);

        categorystoreslist = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(AutoSearchActivity.this);
        layoutManager.setReverseLayout(false);
        recyclerview_stores.setLayoutManager(layoutManager);
        categoriesStoreAdapter = new CategoriesStoreAdapter(AutoSearchActivity.this, this,categorystoreslist);
        recyclerview_stores.setAdapter(categoriesStoreAdapter);

        autosearchlist=new ArrayList<>();
        auto_resultlist=new ArrayList<>();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(AutoSearchActivity.this);
        layoutManager1.setReverseLayout(false);
        autosearchlist=new ArrayList<>();
        recyclerview_autosearch.setLayoutManager(layoutManager1);
        autoSearchAdapter=new AutoSearchAdapterDemo(AutoSearchActivity.this,auto_resultlist,this);
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
            callSuggestionsService();
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
                            categoryStoreService("15", "" + numItems,lattitude,longnitude,searchkey);
                        } else {
                            callToast("You've no internet connection. Please try again.");
                        }
                    }
                }
            }
        });
    }

    private void callSuggestionsService() {
        RetrofitApis retrofitApis=RetrofitApis.Factory.create(AutoSearchActivity.this);
        Call<ProductListResponse> call= retrofitApis.getproducts();
        dialog.show();
        call.enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if(response.isSuccessful()){
                    ProductListResponse model=response.body();
                    if(model.getStatus()==1)
                    {
                        autosearchlist.clear();
                        auto_resultlist.clear();
                        if(model.getProducts()!=null && model.getProducts().size()>0)
                        {
                            autosearchlist.addAll(model.getProducts());
                        }
                    }
                    else {
                        callToast(model.getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }
    private void categoryStoreService(String limit,String offset,String lattitude,String longnitude,String search_key) {
        dialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<CategoryStoreModel> call = service.filteredStores(limit,offset,search_key,lattitude,longnitude);
        call.enqueue(new Callback<CategoryStoreModel>() {
            @Override
            public void onResponse(Call<CategoryStoreModel> call, Response<CategoryStoreModel> response) {
                dialog.dismiss();

                CategoryStoreModel model = response.body();
                if (model != null) {
                    if (model != null) {
                        if (model.getFilteredstores() != null) {
                            listcount=model.getCount();
                            categorystoreslist.addAll(model.getFilteredstores());
                        }
                    }
                }
                categoriesStoreAdapter.notifyDataSetChanged();
                if (categorystoreslist.size() > 0)
                    tv_nodata.setVisibility(View.GONE);
                else
                    tv_nodata.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<CategoryStoreModel> call, Throwable t) {
                dialog.dismiss();
                categorystoreslist.clear();
                categoriesStoreAdapter.notifyDataSetChanged();
                Log.e("failure",t.getMessage());
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
                if (autosearchlist.get(i).getProductName().toLowerCase().startsWith(text.toLowerCase())) {
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
        Toast.makeText(AutoSearchActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStoreClick(CategoryStoreModel.Categorystore model) {
        Intent intent=new Intent(AutoSearchActivity.this, StoreDetailsScreen.class);
        Log.e("merchantid","is"+model.getMerchantId());
        intent.putExtra("merchantid",model.getMerchantId());
        startActivity(intent);
        finish();
    }

    @Override
    public void onAutoSearchClicked(ProductListResponse.Product model) {

        if (connectionDetector.isConnectingToInternet()) {
            recyclerview_stores.setVisibility(View.VISIBLE);
            auto_resultlist.clear();
            autoSearchAdapter.notifyDataSetChanged();
            recyclerview_autosearch.setVisibility(View.GONE);
            et_search.setText("");
            categorystoreslist.clear();
            categoriesStoreAdapter.notifyDataSetChanged();
            searchkey=model.getProductName();
            categoryStoreService("15","0",lattitude,longnitude,searchkey);
        }
        else
            callToast("You've no internet connection. Please try again.");
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
}
