package com.m.aspirego.user_module.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.m.aspirego.LoginActivity;
import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.activities.MapsActivity;
import com.m.aspirego.merchant_module.activities.MerchantHomeActivity;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.models.Merchantinfo;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.models.LoginModel;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.m.aspirego.user_module.presenter.RetrofitApis;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class OtpScreen extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.et_otp)
    EditText et_otp;
    @BindView(R.id.layout_confirmotp)
    RelativeLayout layout_confirmotp;
    @BindView(R.id.layout_resendotp)
    RelativeLayout layout_resendotp;
    @BindView(R.id.layout_enternumber)
    RelativeLayout layout_enternumber;
    @BindView(R.id.tv_mobilenumber)
    TextView tv_mobilenumber;
    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private String mobilenumber;
    private SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_screen);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        layout_enternumber.setOnClickListener(this);
        layout_resendotp.setOnClickListener(this);
        layout_confirmotp.setOnClickListener(this);
        mobilenumber=getIntent().getStringExtra("mobilenumber");
         tv_mobilenumber.setText(mobilenumber);

        dialog = new Dialog(OtpScreen.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
         sessionManagement= SessionManagement.getSession(OtpScreen.this);
        connectionDetector=new ConnectionDetector(OtpScreen.this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.layout_confirmotp:

                String otp=et_otp.getText().toString().trim();
                if(otp.length()<6)
                    callToast("Please enter 6 digit otp");
                else{
                    if(connectionDetector.isConnectingToInternet()) {
                        if (sessionManagement.getUserType() == SessionManagement.AS_USER)
                            verifyOtpService(mobilenumber, otp);
                        else if (sessionManagement.getUserType() == SessionManagement.AS_MERCHANT)
                            verifyOtpMerchantService(mobilenumber, otp);
                    }
                    else
                        callToast("You've no internet connection. Please try again.");
                }

                break;
            case R.id.layout_resendotp:
                if(connectionDetector.isConnectingToInternet()) {
                    if (sessionManagement.getUserType() == SessionManagement.AS_USER)
                        resendOtpService(mobilenumber);
                    else if (sessionManagement.getUserType() == SessionManagement.AS_MERCHANT)
                       resendOtpMerchantService(mobilenumber);
                }
                else
                    callToast("You've no internet connection. Please try again.");
                break;
            case R.id.layout_enternumber:
                finish();
                break;
        }
    }

    private void callToast(String msg)
    {
        Toast.makeText(OtpScreen.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void verifyOtpService(final String mobile_number, String otp)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<LoginModel> call = service.verifyOTPService(mobile_number,otp);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                dialog.dismiss();
                LoginModel model=response.body();
                if(model!=null)
                {
                    if(model.getStatus().equalsIgnoreCase("1"))
                    {
                        sessionManagement.setValuetoPreference(SessionManagement.NAME,model.getData().getName());
                        sessionManagement.setValuetoPreference(SessionManagement.MOBILE,model.getData().getMobileNumber());
                        sessionManagement.setValuetoPreference(SessionManagement.DEVICETOKEN,model.getData().getDeviceToken());
                        sessionManagement.setValuetoPreference(SessionManagement.USERID,model.getData().getUserId());
                        sessionManagement.setValuetoPreference(SessionManagement.FACEBOOKID,model.getData().getFacebookId());
                        sessionManagement.setValuetoPreference(SessionManagement.GOOGLEID,model.getData().getGoogleId());
                        sessionManagement.setValuetoPreference(SessionManagement.PROFILEIMAGE,model.getData().getProfileImage());
                        sessionManagement.setValuetoPreference(SessionManagement.EMAIL,model.getData().getEmail());
                        sessionManagement.setValuetoPreference(SessionManagement.GENDER,model.getData().getGender());

                        Intent intent = new Intent(OtpScreen.this, MapsActivity.class);
                        startActivityForResult(intent,125);
                    }
                    else {
                        callToast(model.getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });

    }
    private void verifyOtpMerchantService(final String mobile_number, String otp)
    {
        dialog.show();
        RetrofitMerchantApis retrofitMerchantApis =RetrofitMerchantApis.Factory.create(this);
        Call<MLogin> call = retrofitMerchantApis.verifyOTPService(mobile_number,otp);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model!=null)
                {
                    Merchantinfo merchantinfo= model.getMerchantinfo();
                    if(merchantinfo!=null) {
                        sessionManagement.setValuetoPreference(SessionManagement.NAME, merchantinfo.getMerchantName());
                        sessionManagement.setValuetoPreference(SessionManagement.MOBILE, merchantinfo.getMobileNumber());
                        sessionManagement.setValuetoPreference(SessionManagement.DEVICETOKEN, merchantinfo.getDeviceToken());
                        sessionManagement.setValuetoPreference(SessionManagement.USERID, merchantinfo.getMerchantId());
                        sessionManagement.setValuetoPreference(SessionManagement.FACEBOOKID, merchantinfo.getFacebookId());
                        sessionManagement.setValuetoPreference(SessionManagement.GOOGLEID, merchantinfo.getGoogleId());
                        sessionManagement.setValuetoPreference(SessionManagement.PROFILEIMAGE, merchantinfo.getProfile_image());
                        sessionManagement.setValuetoPreference(SessionManagement.EMAIL, merchantinfo.getEmail());
                        sessionManagement.setBooleanValuetoPreference(sessionManagement.ISLOGIN, true);
                        Intent intent=new Intent(OtpScreen.this,MerchantHomeActivity.class);
                        startActivity(intent);
                        finish();
                        finishAffinity();
                    }
                    else {
                        callToast(model.getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });

    }



    private void resendOtpService(final String mobile_number)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<LoginModel> call = service.resendOTPService(mobile_number);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                dialog.dismiss();
                LoginModel model=response.body();
                if(model!=null)
                {
                    callToast(model.getResult());
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });

    }


    private void resendOtpMerchantService(final String mobile_number)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MerchantApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMerchantApis service = retrofit.create(RetrofitMerchantApis.class);
        Call<MLogin> call = service.resendOTPService(mobile_number);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model!=null)
                {
                    callToast(model.getResult());
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {

        if(responseCode==RESULT_OK){
            if(requestCode==125){
                LatLng latLng=intent.getParcelableExtra("latlng");
                Log.e("LatLng",""+latLng.toString());
                if(latLng!=null)
                {
                    if (connectionDetector.isConnectingToInternet()) {
                        locationUpDateService(sessionManagement.getValueFromPreference(SessionManagement.USERID), "" + latLng.latitude, "" + latLng.longitude);
                    } else
                        callToast("You've no internet connection. Please try again.");
                }
            }
        }
    }

    private void locationUpDateService(final String userid, final String latitude, final String longitude)
    {
        dialog.show();
        RetrofitApis service = RetrofitApis.Factory.create(this);
        Call<MLogin> call = service.locationupdateService(userid,latitude,longitude);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model!=null)
                {
                    if(model.getStatus()==1)
                    {
                        if(latitude!=null && latitude.trim().length()!=0 && longitude!=null && longitude.trim().length()!=0)
                        {
                            sessionManagement.setBooleanValuetoPreference(sessionManagement.ISLOGIN,true);
                            sessionManagement.setValuetoPreference(SessionManagement.USERLATTITUDE,latitude);
                            sessionManagement.setValuetoPreference(SessionManagement.USERLONGNITUDE,longitude);
                            Intent intent = new Intent(OtpScreen.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            finishAffinity();
                        }
                        else {
                            Intent intent = new Intent(OtpScreen.this, MapsActivity.class);
                            startActivityForResult(intent,125);
                        }
                    }
                    else {
                        callToast(model.getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
                Log.e("get response","onFailure");
            }
        });
    }
}
