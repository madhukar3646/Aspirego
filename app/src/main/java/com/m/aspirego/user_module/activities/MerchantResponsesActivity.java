package com.m.aspirego.user_module.activities;

import android.app.Dialog;
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
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.user_module.adapters.MerchantResponseAdapter;
import com.m.aspirego.user_module.models.MerchantResponseModel;
import com.m.aspirego.user_module.presenter.RetrofitApis;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MerchantResponsesActivity extends AppCompatActivity {
    @BindView(R.id.list)
    RecyclerView list;

    @BindView(R.id.back_btn)
    ImageView back_btn;

    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    RetrofitApis retrofitApis;
    private Dialog dialog;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    private MerchantResponseAdapter adapter;
    String requirement_id,old_imagepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchantresponses);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        requirement_id=getIntent().getStringExtra("requirementid");
        old_imagepath=getIntent().getStringExtra("imagepath");
        retrofitApis=  RetrofitApis.Factory.create(MerchantResponsesActivity.this);
        dialog = new Dialog(MerchantResponsesActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(MerchantResponsesActivity.this);
        sessionManagement= SessionManagement.getSession(MerchantResponsesActivity.this);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(connectionDetector.isConnectingToInternet())
            callService();
        else
            callToast("You've no internet connection. Please try again.");
    }

    private void callService() {
        Log.e("userid requirementid","userid="+sessionManagement.getValueFromPreference(SessionManagement.USERID)+", requirement id="+requirement_id);
        Call<MerchantResponseModel> call=retrofitApis.getMerchantResponse(sessionManagement.getValueFromPreference(SessionManagement.USERID),requirement_id);
        dialog.show();
        call.enqueue(new Callback<MerchantResponseModel>() {
            @Override
            public void onResponse(Call<MerchantResponseModel> call, Response<MerchantResponseModel> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if (response.isSuccessful()){
                    list.setLayoutManager(new LinearLayoutManager(MerchantResponsesActivity.this));
                    if(response.body().getRequirements()!=null && response.body().getRequirements().size()>0) {
                        tv_nodata.setVisibility(View.GONE);
                        adapter=new MerchantResponseAdapter(MerchantResponsesActivity.this,old_imagepath,response.body().getRequirements());
                        list.setAdapter(adapter );
                    }
                    else
                        tv_nodata.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<MerchantResponseModel> call, Throwable t) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
    }

    private void callToast(String msg)
    {
        Toast.makeText(MerchantResponsesActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
