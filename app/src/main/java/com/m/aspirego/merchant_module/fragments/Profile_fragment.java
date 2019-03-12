package com.m.aspirego.merchant_module.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.m.aspirego.LoginActivity;
import com.m.aspirego.R;
import com.m.aspirego.VolleyServiceCall;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.activities.MerchantHomeActivity;
import com.m.aspirego.merchant_module.activities.StoreEditActivity;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.activities.ChangePasswordActivity;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class Profile_fragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.iv_logout)
    ImageView iv_logout;
    @BindView(R.id.iv_edit)
    ImageView iv_edit;
    @BindView(R.id.iv_profileimage)
    CircleImageView iv_profileimage;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_mobilenumber)
    EditText et_mobilenumber;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.layout_savechamges)
    RelativeLayout layout_savechamges;
    @BindView(R.id.layout_store)
    RelativeLayout layout_store;
    @BindView(R.id.layout_changepassword)
    RelativeLayout layout_changepassword;
    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    private String merchant_id;
    private MerchantHomeActivity merchantHomeActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view=inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        ButterKnife.bind(this,view);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        init(view);
        return view;
    }

    private void init(View view)
    {
        merchantHomeActivity=(MerchantHomeActivity) getActivity();
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(getActivity());
         sessionManagement= SessionManagement.getSession(getContext());
         merchant_id=sessionManagement.getValueFromPreference(SessionManagement.USERID);
        iv_logout.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        layout_savechamges.setOnClickListener(this);
        iv_profileimage.setOnClickListener(this);
        layout_changepassword.setOnClickListener(this);
        layout_store.setOnClickListener(this);
        enableInputFields(false);

        if(connectionDetector.isConnectingToInternet())
        {
            Log.e("merchantid",""+merchant_id);
            profileService(merchant_id);
        }
        else {
            callToast("You've no internet connection. Please try again.");
        }
    }

    public boolean checkingPermissionAreEnabledOrNot() {
        int camera = ContextCompat.checkSelfPermission(getActivity(), CAMERA);
        int write = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
        return camera == PackageManager.PERMISSION_GRANTED && read==PackageManager.PERMISSION_GRANTED;
    }

    private void requestMultiplePermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, 100);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.iv_logout:
                displayLogoutDialog();
                break;
            case R.id.iv_edit:
                enableInputFields(true);
                break;
            case R.id.layout_savechamges:
                getProfileupdateFields();
                break;

            case R.id.iv_profileimage:
                if(!checkingPermissionAreEnabledOrNot())
                    requestMultiplePermission();
                else
                    picGalleryImage();
                break;

            case R.id.layout_changepassword:
                Intent changepwd=new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(changepwd);
                break;

            case R.id.layout_store:
                Intent storeintent=new Intent(getContext(), StoreEditActivity.class);
                startActivity(storeintent);
                break;
        }
    }

    private void enableInputFields(boolean isEnable)
    {
        et_name.setEnabled(isEnable);
        et_mobilenumber.setEnabled(false);
        et_email.setEnabled(isEnable);
        if(isEnable)
            layout_savechamges.setVisibility(View.VISIBLE);
        else
            layout_savechamges.setVisibility(View.GONE);
    }


    private void picGalleryImage()
    {
        CropImage.activity()
                .start(getContext(), this);
    }

    private void profileService(final String userid)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MerchantApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMerchantApis service = retrofit.create(RetrofitMerchantApis.class);
        Call<MLogin> call = service.profileService(userid);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model!=null)
                {
                    if(model.getStatus()==1)
                    {
                        et_email.setText(model.getMerchantinfo().getEmail());
                        et_name.setText(model.getMerchantinfo().getMerchantName());
                        et_mobilenumber.setText(model.getMerchantinfo().getMobileNumber());
                        if(model.getMerchantinfo().getProfile_image()!=null && model.getMerchantinfo().getProfile_image().trim().length()>0)
                        {
                            Picasso.with(getActivity()).load(MerchantApiUrls.MERCHANT_PROFILEBASEPATH+model.getMerchantinfo().getProfile_image()).placeholder(R.mipmap.profile_img)
                                    .error(R.mipmap.profile_img)
                                    .into(iv_profileimage);
                        }

                        sessionManagement.setValuetoPreference(SessionManagement.NAME,model.getMerchantinfo().getMerchantName());
                        sessionManagement.setValuetoPreference(SessionManagement.MOBILE,model.getMerchantinfo().getMobileNumber());
                        if(sessionManagement.getValueFromPreference(SessionManagement.DEVICETOKEN).equalsIgnoreCase("0"))
                          sessionManagement.setValuetoPreference(SessionManagement.DEVICETOKEN,model.getMerchantinfo().getDeviceToken());
                        sessionManagement.setValuetoPreference(SessionManagement.USERID,model.getMerchantinfo().getMerchantId());
                        sessionManagement.setValuetoPreference(SessionManagement.FACEBOOKID,model.getMerchantinfo().getFacebookId());
                        sessionManagement.setValuetoPreference(SessionManagement.GOOGLEID,model.getMerchantinfo().getGoogleId());
                        sessionManagement.setValuetoPreference(SessionManagement.PROFILEIMAGE,model.getMerchantinfo().getProfile_image());
                        sessionManagement.setValuetoPreference(SessionManagement.EMAIL,model.getMerchantinfo().getEmail());

                        enableInputFields(false);
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

    private void getProfileupdateFields()
    {
        String str_email=et_email.getText().toString().trim();
        String str_name=et_name.getText().toString().trim();
        if(str_name.length()==0)
            callToast("Please enter your name");
        else if(str_name.length()<3)
            callToast("Please enter atleast 3 digits of your name");
        else if(str_email.length()>0 && !signupEmail(str_email))
            callToast("Please enter valid emai address");
        else {
            if(connectionDetector.isConnectingToInternet())
            {
                updateProfileService(sessionManagement.getValueFromPreference(SessionManagement.USERID),str_name,str_email);
            }
            else {
                callToast("You've no internet connection. Please try again.");
            }
        }
    }
    private void updateProfileService(final String userid,String name,String email)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MerchantApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMerchantApis service = retrofit.create(RetrofitMerchantApis.class);
        Call<MLogin> call = service.updateProfile(userid,name,email);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model!=null)
                {
                    callToast(model.getResult());
                    if(model.getStatus()==1)
                        profileService(sessionManagement.getValueFromPreference(SessionManagement.USERID));
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });

    }

    public void uploadProfilepic(String path)
    {
        dialog.show();
        Bitmap bitmap= BitmapFactory.decodeFile(path);
        Map<String,String> params=new HashMap<>();
        params.put("merchant_id",sessionManagement.getValueFromPreference(SessionManagement.USERID));
        VolleyServiceCall.uploadBitmap(getActivity(), MerchantApiUrls.BASEURL+"upload_profilepic", "image", bitmap, params, new VolleyServiceCall.ServiceResponse() {
            @Override
            public void getResponse(String response) {
                dialog.dismiss();
                Gson gson=new Gson();
                MLogin model=gson.fromJson(response,MLogin.class);
                Log.e("upload response","volley is "+model.getResult());
                if(model!=null) {
                    callToast(model.getResult());
                    if(model.getStatus()==1) {
                        if (model.getMerchantinfo().getProfile_image() != null && !(model.getMerchantinfo().getProfile_image().equalsIgnoreCase("null"))) {
                            if (model.getMerchantinfo().getProfile_image().trim().length() > 0) {
                                String imagepath = MerchantApiUrls.MERCHANT_PROFILEBASEPATH + model.getMerchantinfo().getProfile_image();
                                sessionManagement.setValuetoPreference(SessionManagement.PROFILEIMAGE, imagepath);
                            }
                        }
                    }
                }

                Picasso.with(getActivity()).load(sessionManagement.getValueFromPreference(SessionManagement.PROFILEIMAGE)).placeholder(R.mipmap.profile_img)
                        .error(R.mipmap.profile_img)
                        .into(iv_profileimage);
            }

            @Override
            public void getErrorResponse(String errormessage) {
                Log.e("upload response"," failed ");
                dialog.dismiss();
                callToast(errormessage);
                Picasso.with(getActivity()).load(sessionManagement.getValueFromPreference(SessionManagement.PROFILEIMAGE)).placeholder(R.mipmap.profile_img)
                        .error(R.mipmap.profile_img)
                        .into(iv_profileimage);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String path=resultUri.getPath();
                Log.e("path is","path "+path);
                Picasso.with(getActivity()).load(new File(path)).placeholder(R.mipmap.profile_img)
                        .error(R.mipmap.profile_img)
                        .into(iv_profileimage);
                uploadProfilepic(path);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void callToast(String msg)
    {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    private boolean signupEmail(String email)
    {
        String emailPattern =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void displayLogoutDialog()
    {
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_gallery_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        RelativeLayout layout_yes=(RelativeLayout) dialog.findViewById(R.id.layout_yes);
        RelativeLayout layout_no=(RelativeLayout)dialog.findViewById(R.id.layout_no);
        TextView tv_title=(TextView)dialog.findViewById(R.id.tv_title);
        tv_title.setText("Are you sure you want to Logout?");
        TextView tv_no=(TextView)dialog.findViewById(R.id.tv_no);
        tv_no.setText("No");
        TextView tv_yes=(TextView)dialog.findViewById(R.id.tv_yes);
        tv_yes.setText("Yes");

        layout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                sessionManagement.logoutUser();
                Intent intent=new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                getActivity().finishAffinity();
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
}
