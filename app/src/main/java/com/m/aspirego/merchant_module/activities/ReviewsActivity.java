package com.m.aspirego.merchant_module.activities;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.adapters.ReviewsAdapter;
import com.m.aspirego.merchant_module.models.ReviewslistResponse;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewsActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview_reviews)
    RecyclerView recyclerview_reviews;
    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    private Dialog dialog;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    ReviewsAdapter adapter;
    private String merchantid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        dialog = new Dialog(ReviewsActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(ReviewsActivity.this);
        sessionManagement= SessionManagement.getSession(ReviewsActivity.this);
        merchantid=getIntent().getStringExtra("merchant_id");

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(connectionDetector.isConnectingToInternet()) {
            serviceCall(merchantid);
        }
        else
            callToast("You've no internet connection. Please try again.");
    }


    private void serviceCall(final String merchant_id)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MerchantApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMerchantApis service = retrofit.create(RetrofitMerchantApis.class);
        Call<ReviewslistResponse> call = service.getReviewsList(merchant_id);
        call.enqueue(new Callback<ReviewslistResponse>() {
            @Override
            public void onResponse(Call<ReviewslistResponse> call, Response<ReviewslistResponse> response) {
                dialog.dismiss();
                ReviewslistResponse model=response.body();
                if(model!=null)
                {
                    if(model.getStatus()==1)
                    {
                        if(model.getReviews()!=null && model.getReviews().size()>0)
                        {
                            recyclerview_reviews.setLayoutManager(new LinearLayoutManager(ReviewsActivity.this));
                            tv_nodata.setVisibility(View.GONE);
                            adapter=new ReviewsAdapter(ReviewsActivity.this,model.getReviews());
                            recyclerview_reviews.setAdapter(adapter);
                        }
                        else {
                            tv_nodata.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                        callToast(model.getResult());
                }
            }

            @Override
            public void onFailure(Call<ReviewslistResponse> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    private void callToast(String msg)
    {
        Toast.makeText(ReviewsActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
