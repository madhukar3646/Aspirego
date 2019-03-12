package com.m.aspirego.merchant_module.activities;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.adapters.ProductsAddAdapter;
import com.m.aspirego.merchant_module.models.AddProduct;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.models.ProductListResponse;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.presenter.RetrofitApis;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductsActivity extends AppCompatActivity implements ProductsAddAdapter.OnDeleteItemListener{

    @BindView(R.id.autoCompleteTextView)
    AutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.recyclerview_products)
    RecyclerView recyclerview_products;
    @BindView(R.id.iv_add)
    ImageView iv_add;
    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.layout_addproducts)
    RelativeLayout layout_addproducts;
    private ArrayList<String> productslist;
    private ArrayList<String> availablelist;
    private ProductsAddAdapter addAdapter;
    private Dialog dialog;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        dialog = new Dialog(AddProductsActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(AddProductsActivity.this);
        sessionManagement= SessionManagement.getSession(AddProductsActivity.this);

        productslist=new ArrayList<>();
        availablelist=new ArrayList<>();
        availablelist.addAll(getIntent().getStringArrayListExtra("str_productslist"));
        recyclerview_products.setLayoutManager(new LinearLayoutManager(AddProductsActivity.this));
        addAdapter=new ProductsAddAdapter(AddProductsActivity.this,this,productslist);
        recyclerview_products.setAdapter(addAdapter);

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_name=autoCompleteTextView.getText().toString();
                if(str_name.trim().length()>0)
                {
                  productslist.add(str_name);
                  addAdapter.notifyDataSetChanged();
                  recyclerview_products.smoothScrollToPosition(productslist.size()-1);
                  autoCompleteTextView.setText("");
                  hideSoftKeyboard(AddProductsActivity.this);
                }
                else {
                  callToast("Please enter product name");
                }
            }
        });

        layout_addproducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(productslist.size()>0)
                {
                    ArrayList<AddProduct> list = new ArrayList<>();
                    for (int i = 0; i < productslist.size(); i++) {
                        if (!availablelist.contains(productslist.get(i).toLowerCase())) {
                            AddProduct addProduct = new AddProduct();
                            addProduct.setProductName(productslist.get(i));
                            list.add(addProduct);
                        }
                    }

                    if(list.size()>0)
                    {
                        String products=new Gson().toJson(list);
                        Log.e("products add","are "+products);
                        if(connectionDetector.isConnectingToInternet())
                            addProducts(sessionManagement.getValueFromPreference(SessionManagement.USERID),products);
                        else
                            callToast("You've no internet connection. Please try again.");
                    }
                    else {
                        callToast("These Products already added");
                        productslist.clear();
                        addAdapter.notifyDataSetChanged();
                        setResult(RESULT_OK);
                        finish();
                    }
                }
                else {
                  callToast("Please enter product names and add");
                }
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
    }

    private void callSuggestionsService() {
        RetrofitApis retrofitApis=RetrofitApis.Factory.create(AddProductsActivity.this);
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
                        ArrayList<String> suggestionslist=new ArrayList<>();
                        if(model.getProducts()!=null && model.getProducts().size()>0)
                        {
                            for(ProductListResponse.Product product:model.getProducts())
                                suggestionslist.add(product.getProductName());
                        }
                        setAutoCompleteTextViewAdapter(suggestionslist.toArray(new String[suggestionslist.size()]));
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

    private void setAutoCompleteTextViewAdapter(String[] suggestions)
    {
        final ArrayAdapter<String> auto_adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, suggestions);
        autoCompleteTextView.setThreshold(1);//will start working from first character
        autoCompleteTextView.setAdapter(auto_adapter);
    }

    private void addProducts(String merchantid, String productname) {

        RetrofitMerchantApis retrofitApis=RetrofitMerchantApis.Factory.create(AddProductsActivity.this);
        Call<MLogin> call= retrofitApis.addproduct(merchantid,productname);
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
                        callToast("Products added successfully.");
                        productslist.clear();
                        addAdapter.notifyDataSetChanged();
                        setResult(RESULT_OK);
                        finish();
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

    @Override
    public void onDeleteclick(int pos) {
         productslist.remove(pos);
         addAdapter.notifyDataSetChanged();
    }

    private void callToast(String msg)
    {
        Toast.makeText(AddProductsActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
