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
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.activities.AddProductsActivity;
import com.m.aspirego.merchant_module.adapters.ProductsAdapter;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.models.ProductListResponse;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.presenter.RetrofitApis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Products_fragment extends Fragment implements ProductsAdapter.ProductsDeleteListerner,View.OnClickListener{
    @BindView(R.id.recyclerview_productslist)
    RecyclerView recyclerview_productslist;

    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    @BindView(R.id.iv_addproduct)
    ImageView iv_addproduct;

    private Dialog dialog;
    ProductsAdapter adapter;
    private List<ProductListResponse.Product> productModelList;
    private ArrayList<String> str_productslist;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    RetrofitApis retrofitApis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_products_fragment,container,false);
        ButterKnife.bind(this,view);
        retrofitApis=RetrofitApis.Factory.create(getActivity());
        iv_addproduct.setOnClickListener(this);
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(getContext());
        sessionManagement= SessionManagement.getSession(getContext());

        productModelList=new ArrayList<>();
        str_productslist=new ArrayList<>();
        recyclerview_productslist.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ProductsAdapter(getContext(),productModelList);
        adapter.setDeleteListener(Products_fragment.this);
        recyclerview_productslist.setAdapter(adapter);

        if(connectionDetector.isConnectingToInternet())
            callService(sessionManagement.getValueFromPreference(SessionManagement.USERID));
        else
            callToast("You've no internet connection. Please try again.");

        return view;
    }

    private void callService(String merchant_id) {

        Call<ProductListResponse> call= retrofitApis.products(merchant_id);
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
                        if(model.getProducts()!=null && model.getProducts().size()>0)
                        {
                            tv_nodata.setVisibility(View.GONE);
                            productModelList.clear();
                            productModelList.addAll(model.getProducts());
                            adapter.notifyDataSetChanged();

                            str_productslist.clear();
                            for(ProductListResponse.Product product:productModelList)
                                str_productslist.add(product.getProductName().toLowerCase());
                        }
                        else {
                            tv_nodata.setVisibility(View.VISIBLE);
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

    private void callToast(String msg)
    {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(getActivity(), AddProductsActivity.class);
        intent.putStringArrayListExtra("str_productslist",str_productslist);
        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==100){
                if(connectionDetector.isConnectingToInternet())
                    callService(sessionManagement.getValueFromPreference(SessionManagement.USERID));
                else
                    callToast("You've no internet connection. Please try again.");
            }

        }
    }

    @Override
    public void onProductsDelete(ProductListResponse.Product productModel) {

        displayDeleteDialog(productModel.getProductId());
    }

    private void displayDeleteDialog(final String productid)
    {
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_gallery_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        RelativeLayout layout_yes=(RelativeLayout) dialog.findViewById(R.id.layout_yes);
        RelativeLayout layout_no=(RelativeLayout)dialog.findViewById(R.id.layout_no);
        TextView tv_title=(TextView)dialog.findViewById(R.id.tv_title);
        tv_title.setText("Are you sure you want to delete this product?");
        TextView tv_no=(TextView)dialog.findViewById(R.id.tv_no);
        tv_no.setText("No");
        TextView tv_yes=(TextView)dialog.findViewById(R.id.tv_yes);
        tv_yes.setText("Yes");

        layout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                if(connectionDetector.isConnectingToInternet())
                    deleteProduct(productid);
                else
                    callToast("You've no internet connection. Please try again.");
            }
        });

        layout_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void deleteProduct(String productId) {

        RetrofitMerchantApis retrofitApis=RetrofitMerchantApis.Factory.create(getActivity());
        Call<MLogin> call= retrofitApis.deleteproduct(productId);
        dialog.show();
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if(response.isSuccessful()){
                    MLogin model=response.body();
                    if(model.getStatus()==1)
                    {
                        callService(sessionManagement.getValueFromPreference(SessionManagement.USERID));
                    }
                    else {
                        callToast(model.getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }
}
