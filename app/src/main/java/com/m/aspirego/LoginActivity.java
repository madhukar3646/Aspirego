package com.m.aspirego;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.m.aspirego.firebase.Config;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.activities.MapsActivity;
import com.m.aspirego.merchant_module.activities.MerchantHomeActivity;
import com.m.aspirego.merchant_module.activities.StoreEditActivity;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.models.Merchantinfo;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.activities.ForgetPasswordActivity;
import com.m.aspirego.user_module.activities.HomeActivity;
import com.m.aspirego.user_module.models.LoginModel;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.m.aspirego.user_module.presenter.RetrofitApis;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RC_SIGN_IN =7 ;
    @BindView(R.id.layout_fb)
    RelativeLayout layout_fb;
    @BindView(R.id.layout_google)
    RelativeLayout layout_google;
    @BindView(R.id.et_mobilenumber)
    EditText et_mobilenumber;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.layout_signin)
    RelativeLayout layout_signin;
    @BindView(R.id.tv_createan_account)
    TextView tv_createan_account;
    @BindView(R.id.tv_forgotpassword)
    TextView tv_forgotpassword;
    @BindView(R.id.ch_merchantornot)
    CheckBox ch_merchantornot;

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private SessionManagement sessionManagement;

    ////social logins
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        LoginManager.getInstance().registerCallback(callbackManager,facebookCallback);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        layout_signin.setOnClickListener(this);
        tv_createan_account.setOnClickListener(this);
        tv_forgotpassword.setOnClickListener(this);
        layout_fb.setOnClickListener(this);
        layout_google.setOnClickListener(this);

        dialog = new Dialog(LoginActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(LoginActivity.this);
        sessionManagement= SessionManagement.getSession(LoginActivity.this);
        sessionManagement.setUserType(-1);

        sessionManagement.setUserType(SessionManagement.AS_USER);
        ch_merchantornot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                    sessionManagement.setUserType(SessionManagement.AS_MERCHANT);
                else
                    sessionManagement.setUserType(SessionManagement.AS_USER);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.layout_signin:

                if(sessionManagement.getUserType()>0)
                    checkLoginCreds();
                else
                    callToast("Please select user or merchant");
                break;

            case R.id.tv_createan_account:
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_forgotpassword:
                if(sessionManagement.getUserType()>0) {
                    Intent forgotpwd = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                    startActivity(forgotpwd);
                }else{
                    callToast("Please select user or merchant");
                }
                break;
            case R.id.layout_fb:
                if(sessionManagement.getUserType()>0) {
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(EMAIL));
                }else
                    callToast("Please select user or merchant");
                break;

            case R.id.layout_google:
                if(sessionManagement.getUserType()>0) {
                    googleSignIn();
                }else
                    callToast("Please select user or merchant");
                break;
        }
    }

    private void checkLoginCreds()
    {
        String mobilenumber=et_mobilenumber.getText().toString();
        String password=et_password.getText().toString();
        if(mobilenumber.trim().length()==0)
            callToast("Please enter your mobile number");
        else if(mobilenumber.trim().length()<4)
            callToast("Please enter valid mobile number");
        else if(password.trim().length()==0)
            callToast("Please enter password");
        else if(password.trim().length()<6)
            callToast("Please enter atleast 6 digits password");
        else {
            if(connectionDetector.isConnectingToInternet())
            {
                String device_token=sessionManagement.getValueFromPreference(SessionManagement.DEVICETOKEN);
                if(sessionManagement.getUserType()==SessionManagement.AS_USER)
                        loginService(mobilenumber,password,"android",device_token);
                else if(sessionManagement.getUserType()==SessionManagement.AS_MERCHANT)
                        loginAsMerchantService(mobilenumber,password,"android",device_token);
            }
            else
                callToast("You've no internet connection. Please try again.");
        }
    }
    private void loginService(final String mobile_number, String password, String device_type, String device_token)
    {
        dialog.show();
        RetrofitApis service = RetrofitApis.Factory.create(this);
        Call<LoginModel> call = service.loginService(mobile_number,password,device_type,device_token);
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

                        String userlattitude,userlongnitude;
                        userlattitude=model.getData().getLatitude();
                        userlongnitude=model.getData().getLongitude();
                        if(userlattitude!=null && userlattitude.trim().length()!=0 && userlongnitude!=null && userlongnitude.trim().length()!=0)
                        {
                            sessionManagement.setBooleanValuetoPreference(sessionManagement.ISLOGIN,true);
                            sessionManagement.setValuetoPreference(SessionManagement.USERLATTITUDE,userlattitude);
                            sessionManagement.setValuetoPreference(SessionManagement.USERLONGNITUDE,userlongnitude);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            finishAffinity();
                        }
                        else {
                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            startActivityForResult(intent,125);
                        }
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
    private void loginAsMerchantService(final String mobile_number, String password, String device_type, String device_token)
    {
        dialog.show();
        RetrofitMerchantApis service = RetrofitMerchantApis.Factory.create(this);
        Call<MLogin> call = service.loginService(mobile_number,password,device_type,device_token);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                if(response.isSuccessful())
                {
                    MLogin model=response.body();
                    if(model.getStatus()==1)
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
                        }
                        Intent intent = new Intent(LoginActivity.this, MerchantHomeActivity.class);
                        startActivity(intent);
                        finish();
                        finishAffinity();

                    }
                    else {
                        callToast(model.getResult());
                    }
                }else if(response.code()==400){
                   try{String body= response.errorBody().string();
                      Log.e("ResponseBody",""+body);
                       Gson gson=new Gson();
                       MLogin model= gson.fromJson(body,MLogin.class);
                       if(model.getStatus()==1)
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
                           }
                           Intent intent = new Intent(LoginActivity.this, MerchantHomeActivity.class);
                           startActivity(intent);
                           finish();
                           finishAffinity();

                       }
                       else {
                           callToast(model.getResult());
                       }


                   }
                   catch (IOException e){
                       e.printStackTrace();
                   }
                   catch (JsonSyntaxException e){
                       e.printStackTrace();
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
    private void callToast(String msg)
    {
        Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
            else {
                callbackManager.onActivityResult(requestCode, responseCode, intent);
                // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
                if (requestCode == RC_SIGN_IN) {
                    // The Task returned from this call is always completed, no need to attach
                    // a listener.
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                    handleSignInResult(task);
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
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            finishAffinity();
                        }
                        else {
                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
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
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GMAIL", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    FacebookCallback<LoginResult> facebookCallback=new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            getUserDetailsFromFB(loginResult.getAccessToken());
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };
    public void getUserDetailsFromFB(AccessToken accessToken) {

        GraphRequest req=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Toast.makeText(getApplicationContext(),"graph request completed",Toast.LENGTH_SHORT).show();
                if(object==null)return;
                Log.e("LoginData",object.toString());
                try{
                    String name = object.getString("name");
                    String email =  object.getString("email");
//                    String gender = object.getString("gender");
                    String id = object.getString("id");
//                    String photourl =object.getJSONObject("picture").getJSONObject("data").getString("url");
                    if(sessionManagement.getUserType()==SessionManagement.AS_MERCHANT)
                        verifySocialLoginM("facebook",id);
                    else if(sessionManagement.getUserType()==SessionManagement.AS_USER)
                        verifySocialLoginU("facebook",id);
                }catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(),"graph request error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,picture.type(large)");
        req.setParameters(parameters);
        req.executeAsync();
    }
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


    @Override
    public void onBackPressed() {
        mGoogleSignInClient.signOut();
        super.onBackPressed();

    }
    private void updateUI(GoogleSignInAccount account) {
        if(account==null)
            return;
        String ac=account.getDisplayName()+" "+account.getEmail()+" "+account.getId();
        if(sessionManagement.getUserType()==SessionManagement.AS_MERCHANT)
            verifySocialLoginM("google",account.getId());
        else if (sessionManagement.getUserType()==SessionManagement.AS_USER)
            verifySocialLoginU("google",account.getId());
        Log.e("account details",ac);
//        mGoogleSignInClient.signOut();
    }

    public void setIdPreference(String type,String id){
        if(type.equalsIgnoreCase("facebook")){
            sessionManagement.setValuetoPreference(SessionManagement.FACEBOOKID,id);
        }else if(type.equalsIgnoreCase("google")){
            sessionManagement.setValuetoPreference(SessionManagement.GOOGLEID,id);

        }else {
            sessionManagement.setValuetoPreference(SessionManagement.FACEBOOKID,"");
            sessionManagement.setValuetoPreference(SessionManagement.GOOGLEID,"");
        }
    }


    private void verifySocialLoginM(final String name, final String id) {
        if(id!=null){
            Call<MLogin> call=RetrofitMerchantApis.Factory.create(LoginActivity.this).socialLoginVerfy(name,id);
            call.enqueue(new Callback<MLogin>() {
                @Override
                public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                    if(response.isSuccessful()){
                        MLogin login=response.body();
                        if(login.getNewuser().equalsIgnoreCase("yes")){
                            Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                            intent.putExtra(SignupActivity.TYPE,name);
                            setIdPreference(name,id);
                            startActivity(intent);
                            finish();
                        }else if(login.getNewuser().equalsIgnoreCase("no")){




                            Intent intent=new Intent(LoginActivity.this,MerchantHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }

                }

                @Override
                public void onFailure(Call<MLogin> call, Throwable t) {

                }
            });
        }}

    private void verifySocialLoginU(final String name,final String id) {
        if(id!=null){
            Call<MLogin> call=RetrofitApis.Factory.create(LoginActivity.this).socialLoginVerfy(name,id);
            call.enqueue(new Callback<MLogin>() {
                @Override
                public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                    if(response.isSuccessful()){
                        MLogin login=response.body();
                        mGoogleSignInClient.signOut();
                        if(login.getNewuser().equalsIgnoreCase("yes")){
                            Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                            intent.putExtra(SignupActivity.TYPE,name);
                            setIdPreference(name,id);
                            startActivity(intent);
                            finish();
                        }else if(login.getNewuser().equalsIgnoreCase("no")){
                            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }

                }

                @Override
                public void onFailure(Call<MLogin> call, Throwable t) {

                }
            });
        }}
}
