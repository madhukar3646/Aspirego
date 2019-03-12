package com.m.aspirego;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.GPSTracker;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.activities.OtpScreen;
import com.m.aspirego.user_module.activities.StoreDetailsScreen;
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


public class SignupActivity extends AppCompatActivity implements View.OnClickListener{
    public  static final String TYPE="type";
    @BindView(R.id.layout_signup)
    RelativeLayout layout_signup;
    @BindView(R.id.tv_alreadyhave_account)
    TextView tv_alreadyhave_account;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_mobilenumber)
    EditText et_mobilenumber;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.layout_inputfields)
    LinearLayout layout_inputfields;
    @BindView(R.id.ch_merchantornot)
    CheckBox ch_merchantornot;

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private SessionManagement sessionManagement;
    String type=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        layout_inputfields.setVisibility(View.INVISIBLE);
        type=getIntent().getStringExtra(TYPE);
        if(type==null)
            type="direct";

        tv_alreadyhave_account.setOnClickListener(this);
        layout_signup.setOnClickListener(this);
        dialog = new Dialog(SignupActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(SignupActivity.this);
         sessionManagement= SessionManagement.getSession(this);
        sessionManagement.setUserType(-1);
        visibleUserFields();

        ch_merchantornot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                  visibleMerchantFields();
                else
                   visibleUserFields();
            }
        });
    }

    private void visibleUserFields()
    {
        et_mobilenumber.setText("");
        et_name.setText("");
        et_password.setText("");
        et_name.setHint("Name");
        sessionManagement.setUserType(SessionManagement.AS_USER);
        layout_inputfields.setVisibility(View.VISIBLE);
    }
    private void visibleMerchantFields()
    {
        et_mobilenumber.setText("");
        et_name.setText("");
        et_password.setText("");
        et_name.setHint("Store Name");
        sessionManagement.setUserType(SessionManagement.AS_MERCHANT);
        layout_inputfields.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.tv_alreadyhave_account:
                Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.layout_signup:
                if(sessionManagement.getUserType()>0)
                    checkSignupValidations(et_name.getText().toString().trim(),et_mobilenumber.getText().toString().trim(),et_password.getText().toString().trim());
                 else
                    callToast("Please select User or Merchant");
                break;
        }
    }

    private void callToast(String msg)
    {
        Toast.makeText(SignupActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void checkSignupValidations(String name,String mobile_number,String password)
    {
      if(name.trim().length()==0)
          callToast("Please enter your name");
      else if(name.trim().length()<3)
          callToast("Please enter atleast 3 digits of your name");
      else if(mobile_number.trim().length()==0)
          callToast("Please enter your mobile number");
      else if(mobile_number.trim().length()<4)
          callToast("Please enter valid mobile number");
      else if(password.trim().length()==0)
          callToast("Please enter password");
      else if(password.trim().length()<6)
          callToast("Please enter atleast 6 digits password");
      else {
           if(connectionDetector.isConnectingToInternet())
           {
               String devicetoken=sessionManagement.getValueFromPreference(SessionManagement.DEVICETOKEN);
               if(sessionManagement.getUserType()==SessionManagement.AS_USER)
                        signupService(name,mobile_number,password,"android",devicetoken,type);
               else if(sessionManagement.getUserType()==SessionManagement.AS_MERCHANT)
               {
                   signupAsMerchant(name, mobile_number, password, "android", devicetoken, type);
               }
           }
           else
               callToast("You've no internet connection. Please try again.");
      }
    }
    private void signupService(String name, final String mobile_number, String password, String device_type, String device_token, String logintype)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);

        if(type.equalsIgnoreCase("direct"))
        {
        Call<LoginModel> call = service.signupService(name,mobile_number,password,device_type,device_token,logintype);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                dialog.dismiss();
                LoginModel model=response.body();
                if(model!=null)
                {
                    if(model.getStatus().equalsIgnoreCase("1"))
                    {
                        Intent otpintent=new Intent(SignupActivity.this,OtpScreen.class);
                        otpintent.putExtra("mobilenumber",mobile_number);
                        startActivity(otpintent);
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
                Log.e("get response","onFailure");
            }
        });
        } else if(type.equalsIgnoreCase("facebook")||type.equalsIgnoreCase("google")){

            Call<LoginModel> call=service.signupSocialService(name,mobile_number,password,device_type,device_token,type,sessionManagement.getValueFromPreference(SessionManagement.FACEBOOKID),sessionManagement.getValueFromPreference(SessionManagement.GOOGLEID));
                  call.enqueue(new Callback<LoginModel>() {
                      @Override
                      public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                          dialog.dismiss();
                          LoginModel model=response.body();
                          if(model!=null)
                          {
                              if(model.getStatus().equalsIgnoreCase("1"))
                              {
                                  Intent otpintent=new Intent(SignupActivity.this,OtpScreen.class);
                                  otpintent.putExtra("mobilenumber",mobile_number);
                                  startActivity(otpintent);
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
                          Log.e("get response","onFailure");
                      }
                  });
        }
    }
    private void signupAsMerchant(String name, final String mobile_number, String password, String device_type, String device_token, String logintype)
    {
        dialog.show();
        RetrofitMerchantApis service = RetrofitMerchantApis.Factory.create(this);

        if(type.equalsIgnoreCase("facebook")||type.equalsIgnoreCase("google")) {
           Call<MLogin> call = service.signupSocialService(logintype, name, mobile_number, password, device_type, device_token,sessionManagement.getValueFromPreference(SessionManagement.FACEBOOKID),sessionManagement.getValueFromPreference(SessionManagement.GOOGLEID));
           call.enqueue(new Callback<MLogin>() {
               @Override
               public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                   dialog.dismiss();
                   MLogin model = response.body();
                   if (model != null) {
                       if (model.getStatus() == 1) {
                           Intent otpintent = new Intent(SignupActivity.this, OtpScreen.class);
                           otpintent.putExtra("mobilenumber", mobile_number);
                           startActivity(otpintent);
                       } else {
                           callToast(model.getResult());
                       }
                   }
               }

               @Override
               public void onFailure(Call<MLogin> call, Throwable t) {
                   dialog.dismiss();
                   callToast(t.getMessage());
                   Log.e("get response", "onFailure");
               }
           });
       }else if(type.equalsIgnoreCase("direct")){
           Call<MLogin> call = service.signupService(logintype, name, mobile_number, password, device_type, device_token);
           call.enqueue(new Callback<MLogin>() {
               @Override
               public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                   dialog.dismiss();
                   MLogin model = response.body();
                   if (model != null) {
                       if (model.getStatus() == 1) {
                           Intent otpintent = new Intent(SignupActivity.this, OtpScreen.class);
                           otpintent.putExtra("mobilenumber", mobile_number);
                           startActivity(otpintent);
                       } else {
                           callToast(model.getResult());
                       }
                   }
               }

               @Override
               public void onFailure(Call<MLogin> call, Throwable t) {
                   dialog.dismiss();
                   callToast(t.getMessage());
                   Log.e("get response", "onFailure");
               }
           });
       }
    }
}
