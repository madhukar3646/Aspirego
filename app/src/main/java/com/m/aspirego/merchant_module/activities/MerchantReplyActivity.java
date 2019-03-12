package com.m.aspirego.merchant_module.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.activities.MerchantResponsesActivity;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MerchantReplyActivity extends AppCompatActivity  {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.mail)
    TextView mail;
    @BindView(R.id.delivery_option)
    CheckBox delivery_option;
    @BindView(R.id.same_ch)
    RadioButton same_ch;
    @BindView(R.id.similar_ch)
    RadioButton similar_ch;
    @BindView(R.id.product_type)
    RadioGroup product_type;
    @BindView(R.id.product_name_edt)
    EditText product_name;
    @BindView(R.id.product_price_ed)
    EditText product_price;
    @BindView(R.id.product_offered_price)
    EditText product_offre_price;
    @BindView(R.id.submit_btn)
    TextView submit;
    @BindView(R.id.image_upload_lay)
    LinearLayout image_upload_lay;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.back_btn)
    ImageView back_btn;
    String requirementid,user_name,user_email,user_mobile;

    private Dialog dialog;
    ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_reply);
        ButterKnife.bind(this);

        dialog = new Dialog(MerchantReplyActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(MerchantReplyActivity.this);
        sessionManagement= SessionManagement.getSession(MerchantReplyActivity.this);

        requirementid=getIntent().getStringExtra("requirementid");
        user_name=getIntent().getStringExtra("name");
        name.setText(user_name);
        user_mobile=getIntent().getStringExtra("mobile");
        mobile.setText(user_mobile);
        user_email=getIntent().getStringExtra("email");
        mail.setText(user_email);

        product_type.setOnCheckedChangeListener(onCheckedChangeListener);
        image_upload_lay.setVisibility(View.GONE);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i){
                case R.id.similar_ch:
                    image_upload_lay.setVisibility(View.VISIBLE);
                    break;
                case R.id.same_ch:
                    image_upload_lay.setVisibility(View.GONE);
                    break;
            }

        }
    };

    public void submitReplay(View view){
        Log.e("req_id userid","userid="+sessionManagement.getValueFromPreference(SessionManagement.USERID)+",req_id="+requirementid);
        if(validate()){
            String merchantid,req_id,productname,productprice,offerprice,deliveryoption,product_type;
            merchantid=sessionManagement.getValueFromPreference(SessionManagement.USERID);
            req_id=requirementid;
            productname=product_name.getText().toString().trim();
            offerprice=product_offre_price.getText().toString().trim();
            productprice=product_price.getText().toString().trim();
            if(delivery_option.isChecked())
                deliveryoption="available";
            else
                deliveryoption="not available";
            if(similar_ch.isChecked()) {
                product_type = "I have similar product";
                if(connectionDetector.isConnectingToInternet())
                        uploadeMerchantResponseWithImage(merchantid,req_id,productname,productprice,offerprice,deliveryoption,product_type,path);
                else
                    callToast("please check internet connection");
            }
            else if(same_ch.isChecked()) {
                product_type = "I have same product";
                if(connectionDetector.isConnectingToInternet())
                    uploadeMerchantResponse(merchantid,req_id,productname,productprice,offerprice,deliveryoption,product_type);
                else
                    callToast("please check internet connection");
            }
        }
    }

    private boolean validate() {
        String pro_name,pro_price,pro_off_price;
        pro_name=product_name.getText().toString();
        pro_off_price=product_offre_price.getText().toString();
        pro_price=product_price.getText().toString();
        if(!isValidate(pro_name))
        {
            callToast("Please enter product name");
            return false;
        }
        if(!isValidate(pro_price))
        {
            callToast("Please enter product Price");
            return false;
        }
        if(!isValidate(pro_off_price))
        {
            callToast("Please enter product offered Price");
            return false;
        }
        if(similar_ch.isChecked()){
            if(!isValidate(path))
            {
                callToast("Please select image");
                return false;
            }
        }

        return true;
    }
    private boolean isValidate(String s) {
        if(s==null)return false;
        if(s.trim().isEmpty())return false;
        return true;
    }
    private void callToast(String msg)
    {
        Toast.makeText(MerchantReplyActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    public void openGallery(View view) {
        if (!checkingPermissionAreEnabledOrNot())
            requestMultiplePermission();
        else
            picGalleryImage();
    }

    public boolean checkingPermissionAreEnabledOrNot() {
        int camera = ContextCompat.checkSelfPermission(this, CAMERA);
        int write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        return camera == PackageManager.PERMISSION_GRANTED && read==PackageManager.PERMISSION_GRANTED;
    }
    private void requestMultiplePermission() {

        ActivityCompat.requestPermissions(this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, 100);

    }

    private void picGalleryImage()
    {
        CropImage.activity()
                .start(this);
    }
    String path;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                path=resultUri.getPath();
                Log.e("path is","path "+path);
                Picasso.with(this).load(new File(path)).placeholder(R.mipmap.profile_img)
                        .error(R.mipmap.profile_img)
                        .into(image);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadeMerchantResponseWithImage(String merchant_id, String requirement_id, String product_name, String price, String offer_price, String delivery_option, String product_type,String path){
        MultipartBody.Part fileToUpload=null;
            final File file=new File(path);
            if(file.exists()) {
                RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
            }
        RequestBody mid = RequestBody.create(MediaType.parse("text/plain"),merchant_id);
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"),requirement_id);
        RequestBody pname = RequestBody.create(MediaType.parse("text/plain"),product_name);
        RequestBody p_price = RequestBody.create(MediaType.parse("text/plain"),price);
        RequestBody off_price = RequestBody.create(MediaType.parse("text/plain"),offer_price);
        RequestBody del_option = RequestBody.create(MediaType.parse("text/plain"),delivery_option);
        RequestBody p_type = RequestBody.create(MediaType.parse("text/plain"),product_type);
        Call<MLogin> call= RetrofitMerchantApis.Factory.create(this).uploadMerchantResponseWithImage(fileToUpload,mid,rid,pname,p_price,off_price,del_option,p_type);
        dialog.show();
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin body=response.body();
                if(body.getStatus()==1){
                   callToast("Response Updated Successfully");
                   finish();
                }
                else {
                    callToast(body.getResult());
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                Log.e("add offer onFailure",""+call.toString());
            }
        });
    }

    private void uploadeMerchantResponse(String merchant_id, String requirement_id, String product_name, String price, String offer_price, String delivery_option, String product_type){
        RequestBody mid = RequestBody.create(MediaType.parse("text/plain"),merchant_id);
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"),requirement_id);
        RequestBody pname = RequestBody.create(MediaType.parse("text/plain"),product_name);
        RequestBody p_price = RequestBody.create(MediaType.parse("text/plain"),price);
        RequestBody off_price = RequestBody.create(MediaType.parse("text/plain"),offer_price);
        RequestBody del_option = RequestBody.create(MediaType.parse("text/plain"),delivery_option);
        RequestBody p_type = RequestBody.create(MediaType.parse("text/plain"),product_type);
        Call<MLogin> call= RetrofitMerchantApis.Factory.create(this).uploadMerchantResponse(mid,rid,pname,p_price,off_price,del_option,p_type);
        dialog.show();
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin body=response.body();
                if(body.getStatus()==1){
                    callToast("Response Updated Successfully");
                    finish();
                }
                else {
                    callToast(body.getResult());
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                Log.e("add offer onFailure",""+call.toString());
            }
        });
    }
}