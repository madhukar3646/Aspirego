package com.m.aspirego.user_module.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.user_module.activities.AutoSearchActivity;
import com.m.aspirego.user_module.activities.CategoryListActivity;
import com.m.aspirego.user_module.activities.StoreDetailsScreen;
import com.m.aspirego.user_module.adapters.CategoriesAdapter;
import com.m.aspirego.user_module.adapters.CategoriesStoreAdapter;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.user_module.helperclasses.EndlessParentScrollListener;
import com.m.aspirego.user_module.models.CategoryModel;
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

public class Aroundme_fragment extends Fragment implements View.OnClickListener,CategoriesStoreAdapter.onStoreClickListener{

    @BindView(R.id.recyclerview_categories)
    RecyclerView recyclerview_categories;
    @BindView(R.id.recyclerview_categorieslist)
    RecyclerView recyclerview_categorieslist;
    @BindView(R.id.layout_titlecontainer)
    RelativeLayout layout_titlecontainer;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.tv_nodata)
    TextView tv_nodata;
    @BindView(R.id.nestedscrollview)
    NestedScrollView nestedscrollview;
    @BindView(R.id.layout_more)
    RelativeLayout layout_more;
    @BindView(R.id.layout_minimize)
    RelativeLayout layout_minimize;

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private CategoriesAdapter categoriesAdapter;
    private CategoriesStoreAdapter categoriesStoreAdapter;
    private List<CategoryModel.Category> categorieslist,categorieslistfilter;
    private List<CategoryStoreModel.Categorystore> categorystoreslist;
    private LinearLayoutManager layoutManager;
    private Aroundme_fragment aroundme_fragment;
    private String lattitude,longnitude;
    SessionManagement sessionManagement;
    private int listcount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View view = inflater.inflate(R.layout.fragment_aroundme, container, false);
        ButterKnife.bind(this, view);
        init(view);
        return view;
    }

    private void init(View view) {

        sessionManagement= SessionManagement.getSession(getContext());
        aroundme_fragment = this;
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector = new ConnectionDetector(getActivity());

        categorieslist = new ArrayList<>();
        categorieslistfilter=new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerview_categories.setLayoutManager(gridLayoutManager);
        categoriesAdapter = new CategoriesAdapter(aroundme_fragment, getActivity(), categorieslistfilter);
        recyclerview_categories.setAdapter(categoriesAdapter);
        recyclerview_categories.setNestedScrollingEnabled(false);

        categorystoreslist = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(false);
        recyclerview_categorieslist.setLayoutManager(layoutManager);
        categoriesStoreAdapter = new CategoriesStoreAdapter(getActivity(), this,categorystoreslist);
        recyclerview_categorieslist.setAdapter(categoriesStoreAdapter);
        recyclerview_categorieslist.setNestedScrollingEnabled(false);

        iv_search.setOnClickListener(this);

        lattitude=sessionManagement.getValueFromPreference(SessionManagement.USERLATTITUDE);
        longnitude=sessionManagement.getValueFromPreference(SessionManagement.USERLONGNITUDE);
        displayCategoriesAndStores();

        nestedscrollview.setOnScrollChangeListener(new EndlessParentScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.e("load more",""+totalItemsCount+" "+listcount);
                if(listcount!=totalItemsCount)
                {
                    if (connectionDetector.isConnectingToInternet()) {
                        categorymerchantsService("15", "" + totalItemsCount,lattitude,longnitude);
                    }
                    else {
                        callToast("You've no internet connection. Please try again.");
                    }
                }
            }
        });

        layout_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAllCategories();
            }
        });

        layout_minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               displayNineCategories();
            }
        });
    }


    private void showAllCategories()
    {
        categorieslistfilter.clear();
        categorieslistfilter.addAll(categorieslist);
        categoriesAdapter.notifyDataSetChanged();
        if(categorieslistfilter.size()>9) {
            layout_minimize.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.GONE);
        }
        else {
            layout_minimize.setVisibility(View.GONE);
            layout_more.setVisibility(View.GONE);
        }
    }

    private void displayNineCategories()
    {
        categorieslistfilter.clear();
        for(int i=0;i<categorieslist.size();i++)
        {
            if(i==9)
                break;
            categorieslistfilter.add(categorieslist.get(i));
        }
        categoriesAdapter.notifyDataSetChanged();
        if(categorieslist.size()>9)
        {
            layout_minimize.setVisibility(View.GONE);
            layout_more.setVisibility(View.VISIBLE);
        }
        else {
            layout_minimize.setVisibility(View.GONE);
            layout_more.setVisibility(View.GONE);
        }
    }

    private void displayCategoriesAndStores() {
        if (connectionDetector.isConnectingToInternet())
            categoryService();
        else
            callToast("You've no internet connection. Please try again.");
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
                categorieslist.clear();
                CategoryModel model = response.body();
                if (model != null) {
                    Log.e("categorieslist size", "size" + model.getCategories().size());
                    if (model.getCategories() != null) {
                        categorieslist.addAll(model.getCategories());
                        displayNineCategories();
                    }
                }
                categorymerchantsService("15","0", lattitude, longnitude);
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    public void getClickedCategory(String type_of_store,int position) {
        layout_titlecontainer.setVisibility(View.VISIBLE);
        Intent intent=new Intent(getActivity(), CategoryListActivity.class);
        intent.putExtra("lattitude",lattitude);
        intent.putExtra("longnitude",longnitude);
        intent.putExtra("categoryname",categorieslist.get(position).getName());
        intent.putExtra("type_of_store",type_of_store);
        startActivity(intent);
    }

   private void categorymerchantsService(String limit,String offset,String latitude, String longitude) {
        Log.e("url around_me",""+ApiUrls.BASEURL+"categorymerchants");
        Log.e("categorymerchant params"," latitude="+latitude+" longitude="+longitude);
        dialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<CategoryStoreModel> call = service.categorymerchantsService(limit,offset, latitude, longitude);
        call.enqueue(new Callback<CategoryStoreModel>() {
            @Override
            public void onResponse(Call<CategoryStoreModel> call, Response<CategoryStoreModel> response) {
                dialog.dismiss();

                CategoryStoreModel model = response.body();
                if (model != null) {
                    if (model != null) {
                        if (model.getCategorystores() != null) {
                            listcount=model.getTotal();
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
                if(t!=null) {
                    Log.e("failure", t.getMessage());
                    callToast(t.getMessage());
                }
            }
        });
    }

    private void callToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_search:
                setSearch_layout();
                break;
        }
    }

    public void setSearch_layout() {
        Intent intent=new Intent(getActivity(), AutoSearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStoreClick(CategoryStoreModel.Categorystore model) {

        Intent intent=new Intent(getActivity(), StoreDetailsScreen.class);
        Log.e("merchantid","is"+model.getMerchantId());
        intent.putExtra("merchantid",model.getMerchantId());
        startActivity(intent);
    }

}
