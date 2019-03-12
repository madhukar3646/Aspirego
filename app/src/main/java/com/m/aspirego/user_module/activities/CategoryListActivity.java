package com.m.aspirego.user_module.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
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

public class CategoryListActivity extends AppCompatActivity implements CategoriesStoreAdapter.onStoreClickListener{

    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.iv_searchbtn)
    ImageView iv_searchbtn;
    @BindView(R.id.tv_nodata)
    TextView tv_nodata;
    @BindView(R.id.list)
    RecyclerView recyclerview_categorieslist;
    @BindView(R.id.tv_title)
    TextView tv_title;

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private List<CategoryStoreModel.Categorystore> categorystoreslist;
    private CategoriesStoreAdapter categoriesStoreAdapter;
    private String lattitude,longnitude,type_of_store,categoryname;
    private int listcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        Intent intent=getIntent();
        lattitude=intent.getStringExtra("lattitude");
        longnitude=intent.getStringExtra("longnitude");
        type_of_store=intent.getStringExtra("type_of_store");
        categoryname=intent.getStringExtra("categoryname");
        tv_title.setText(categoryname);

        dialog = new Dialog(CategoryListActivity.this,
            android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector = new ConnectionDetector(CategoryListActivity.this);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        iv_searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryListActivity.this, AutoSearchActivity.class);
                startActivity(intent);
            }
        });

        categorystoreslist = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(CategoryListActivity.this);
        layoutManager.setReverseLayout(false);
        recyclerview_categorieslist.setLayoutManager(layoutManager);
        categoriesStoreAdapter = new CategoriesStoreAdapter(CategoryListActivity.this, this,categorystoreslist);
        recyclerview_categorieslist.setAdapter(categoriesStoreAdapter);
        recyclerview_categorieslist.setNestedScrollingEnabled(false);

        if (connectionDetector.isConnectingToInternet()) {
            categoryStoreService("15","0",type_of_store, lattitude, longnitude);
        } else {
            callToast("You've no internet connection. Please try again.");
        }

        recyclerview_categorieslist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = ((LinearLayoutManager)recyclerView.getLayoutManager());
                int pos = layoutManager.findLastCompletelyVisibleItemPosition();
                int numItems = recyclerView.getAdapter().getItemCount();
                if((pos+1)!=listcount) {
                    if ((pos + 1) >= numItems) {
                        if (connectionDetector.isConnectingToInternet()) {
                            categoryStoreService("15", "" + numItems, type_of_store, lattitude, longnitude);
                        } else {
                            callToast("You've no internet connection. Please try again.");
                        }
                    }
                }
            }
        });
    }

    private void categoryStoreService(String limit,String offset,String type_of_store, String latitude, String longitude) {
        Log.e("url around_me",""+ ApiUrls.BASEURL+"categorystores");
        Log.e("categorystores params","type_of_store="+type_of_store+" latitude="+latitude+" longitude="+longitude);
        dialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<CategoryStoreModel> call = service.categorystoresService(limit,offset,type_of_store, latitude, longitude);
        call.enqueue(new Callback<CategoryStoreModel>() {
            @Override
            public void onResponse(Call<CategoryStoreModel> call, Response<CategoryStoreModel> response) {
                dialog.dismiss();
                CategoryStoreModel model = response.body();
                if (model != null) {
                    if (model != null) {
                        if (model.getCategorystores() != null) {
                            listcount=model.getCount();
                            categorystoreslist.addAll(model.getCategorystores());
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

    private void callToast(String msg) {
        Toast.makeText(CategoryListActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStoreClick(CategoryStoreModel.Categorystore model) {
        Intent intent=new Intent(CategoryListActivity.this, StoreDetailsScreen.class);
        Log.e("merchantid","is"+model.getMerchantId());
        intent.putExtra("merchantid",model.getMerchantId());
        startActivity(intent);
    }
}
