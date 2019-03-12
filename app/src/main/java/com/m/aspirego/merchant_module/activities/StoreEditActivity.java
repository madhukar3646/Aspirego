package com.m.aspirego.merchant_module.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.adapters.CategoryDropDownAdapter;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.models.MerchantStoreUpdateModel;
import com.m.aspirego.merchant_module.models.TagsFormat;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.models.CategoryModel;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.m.aspirego.user_module.presenter.RetrofitApis;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class StoreEditActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    @BindView(R.id.iv_back_button)
    ImageView iv_back_button;
    @BindView(R.id.iv_edit)
    ImageView iv_edit;
    @BindView(R.id.et_storecontactnumber)
    EditText et_storecontactnumber;
    @BindView(R.id.et_oprntime)
    EditText et_oprntime;
    @BindView(R.id.et_closingtime)
    EditText et_closingtime;
    @BindView(R.id.tv_storeaddress)
    TextView tv_storeaddress;
    @BindView(R.id.et_doornumber)
    EditText et_doornumber;
    @BindView(R.id.et_state)
    EditText et_state;
    @BindView(R.id.iv_storebanner)
    ImageView iv_storebanner;
    @BindView(R.id.layout_save)
    RelativeLayout layout_save;
    @BindView(R.id.tv_multiphotos)
    TextView tv_multiphotos;
    @BindView(R.id.layout_tag1)
    RelativeLayout layout_tag1;
    @BindView(R.id.layout_tag2)
    RelativeLayout layout_tag2;
    @BindView(R.id.layout_tag3)
    RelativeLayout layout_tag3;
    @BindView(R.id.layout_tag4)
    RelativeLayout layout_tag4;
    @BindView(R.id.layout_tag5)
    RelativeLayout layout_tag5;

    @BindView(R.id.et_tag1)
    EditText et_tag1;
    @BindView(R.id.et_tag2)
    EditText et_tag2;
    @BindView(R.id.et_tag3)
    EditText et_tag3;
    @BindView(R.id.et_tag4)
    EditText et_tag4;
    @BindView(R.id.et_tag5)
    EditText et_tag5;
    @BindView(R.id.iv_addtags)
    ImageView iv_addtags;
    @BindView(R.id.et_website)
    EditText et_website;
    @BindView(R.id.spinner)
    Spinner spinner;
    private LatLng latLng;
    private List<TagsFormat> tagsFormatList=new ArrayList<>();

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    private String merchant_id,cat_id;
    private String gallery_path;
    private List<CategoryModel.Category> categorieslist;
    private CategoryDropDownAdapter categoryDropDownAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_edit);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        dialog = new Dialog(StoreEditActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(StoreEditActivity.this);
        sessionManagement= SessionManagement.getSession(StoreEditActivity.this);
        merchant_id=sessionManagement.getValueFromPreference(SessionManagement.USERID);

        tv_multiphotos.setOnClickListener(this);
        iv_addtags.setOnClickListener(this);
        et_closingtime.setOnClickListener(this);
        et_oprntime.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        iv_back_button.setOnClickListener(this);
        iv_storebanner.setOnClickListener(this);
        layout_save.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);

        layout_tag1.setVisibility(View.GONE);
        layout_tag2.setVisibility(View.GONE);
        layout_tag3.setVisibility(View.GONE);
        layout_tag4.setVisibility(View.GONE);
        layout_tag5.setVisibility(View.GONE);
        disableInputFields(false);

        categorieslist=new ArrayList<>();
        CategoryModel.Category model=new CategoryModel().getCategoryObject();
        model.setName("--Select Category--");
        categorieslist.add(model);
        categoryDropDownAdapter=new CategoryDropDownAdapter(StoreEditActivity.this,categorieslist);
        spinner.setAdapter(categoryDropDownAdapter);
        if(connectionDetector.isConnectingToInternet())
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
                CategoryModel.Category title=new CategoryModel().getCategoryObject();
                title.setName("--Select Category--");
                categorieslist.add(title);

                CategoryModel model = response.body();
                if (model != null) {
                    Log.e("categorieslist size", "size" + model.getCategories().size());
                    if (model.getCategories() != null) {
                        categorieslist.addAll(model.getCategories());
                    }
                }
                categoryDropDownAdapter.notifyDataSetChanged();
                getstoreinfoService(merchant_id);
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    private void setValuesToInputFields(MerchantStoreUpdateModel model)
    {
        String contactnumber,opentime,closetime,doornumber,address,state,image_path,website,cat_id;
        contactnumber=model.getStoreinfo().getStoreNumber();
        opentime=model.getStoreinfo().getOpenTime();
        closetime=model.getStoreinfo().getCloseTime();
        doornumber=model.getStoreinfo().getDoor_number();
        address=model.getStoreinfo().getAddress();
        state=model.getStoreinfo().getState();
        image_path=model.getStoreinfo().getMerchantBanner();
        website=model.getStoreinfo().getWebsite();
        cat_id=model.getStoreinfo().getType_of_store();
        String lat=model.getStoreinfo().getLatitude();
        String longni=model.getStoreinfo().getLongitude();
        double lattitude=0.0;
        double longnitude=0.0;
        if(lat!=null && longni!=null)
        {
            try {
                lattitude=Double.valueOf(lat);
                longnitude=Double.valueOf(longnitude);
            }
            catch (Exception e)
            {
                lattitude=0.0;
                longnitude=0.0;
            }
        }
        latLng=new LatLng(lattitude,longnitude);

        if(website!=null)
            et_website.setText(website);
        if(cat_id!=null)
        {
            this.cat_id=cat_id;
            for(int i=1;i<categorieslist.size();i++)
            {
                CategoryModel.Category cat=categorieslist.get(i);
                if(cat.getId().equalsIgnoreCase(this.cat_id)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
        gallery_path=null;
        if(model.getStoreinfo().getTags()!=null && model.getStoreinfo().getTags().size()>0)
        {
            tagsFormatList.clear();
            List<MerchantStoreUpdateModel.Tag> list=model.getStoreinfo().getTags();
            for(MerchantStoreUpdateModel.Tag tag:list)
            {
               TagsFormat tagsFormat=new TagsFormat();
               tagsFormat.setTagId(tag.getTagId());
               tagsFormat.setTagName(tag.getTagName());
               tagsFormatList.add(tagsFormat);
            }
        }

        if(isValidate(contactnumber))
            et_storecontactnumber.setText(contactnumber);
        if(isValidate(opentime))
            et_oprntime.setText(opentime);
        if(isValidate(closetime))
            et_closingtime.setText(closetime);
        if(isValidate(doornumber))
            et_doornumber.setText(doornumber);
        if(isValidate(address))
            tv_storeaddress.setText(address);
        if(isValidate(state))
            et_state.setText(state);
        if(tagsFormatList.size()>0) {
            et_tag1.setText(tagsFormatList.get(0).getTagName());
            layout_tag1.setVisibility(View.VISIBLE);
        }
        if(tagsFormatList.size()>1) {
            et_tag2.setText(tagsFormatList.get(1).getTagName());
            layout_tag2.setVisibility(View.VISIBLE);
        }
        if(tagsFormatList.size()>2) {
            et_tag3.setText(tagsFormatList.get(2).getTagName());
            layout_tag3.setVisibility(View.VISIBLE);
        }
        if(tagsFormatList.size()>3) {
            et_tag4.setText(tagsFormatList.get(3).getTagName());
            layout_tag4.setVisibility(View.VISIBLE);
        }
        if(tagsFormatList.size()>4) {
            et_tag5.setText(tagsFormatList.get(4).getTagName());
            layout_tag5.setVisibility(View.VISIBLE);
        }
        if(isValidate(image_path))
            Picasso.with(StoreEditActivity.this).load(ApiUrls.MERCHANTBANNERS+image_path).placeholder(R.mipmap.logo_icon)
                    .error(R.mipmap.logo_icon)
                    .into(iv_storebanner);

        gallery_path=null;
        disableInputFields(false);
    }

    private void checkInputValidations()
    {
      String contactnumber,opentime,closetime,doornumber,address,state,tag1,tag2,tag3,tag4,tag5,tags,str_website;
      contactnumber=et_storecontactnumber.getText().toString().trim();
      opentime=et_oprntime.getText().toString().trim();
      closetime=et_closingtime.getText().toString().trim();
      doornumber=et_doornumber.getText().toString().trim();
      address=tv_storeaddress.getText().toString().trim();
      state=et_state.getText().toString().trim();
      tag1=et_tag1.getText().toString().trim();
      tag2=et_tag2.getText().toString().trim();
      tag3=et_tag3.getText().toString().trim();
      tag4=et_tag4.getText().toString().trim();
      tag5=et_tag5.getText().toString().trim();
      str_website=et_website.getText().toString().trim();
      if(str_website==null)
          str_website="";
      if(!isValidate(contactnumber))
          callToast("Please enter store contact number");
      else if(contactnumber.length()<10)
          callToast("Please enter valid mobile number");
      else if(!isValidate(opentime))
          callToast("Please select opening time");
      else if(!isValidate(closetime))
          callToast("Please select closing time");
      else if(!isValidate(doornumber))
          callToast("Please enter door number");
      else if(!isValidate(address))
          callToast("Please select address");
      else if(cat_id.equalsIgnoreCase("-1"))
            callToast("Please select category");
      else if(!isValidate(state))
          callToast("Please enter state");
      else if(!isValidate(tag1) && !isValidate(tag2) && !isValidate(tag3) && !isValidate(tag4) && !isValidate(tag5))
          callToast("Please add atleast one tag");
      else {
          tags=getTagsData(tag1,tag2,tag3,tag4,tag5);
          if(connectionDetector.isConnectingToInternet()) {
              if(gallery_path!=null)
                 updateStoreWithImage(merchant_id,contactnumber,opentime,closetime,address,state,tags,gallery_path,doornumber,str_website,cat_id,""+latLng.latitude,""+latLng.longitude);
              else
                  updateStore(merchant_id,contactnumber,opentime,closetime,address,state,tags,doornumber,str_website,cat_id,""+latLng.latitude,""+latLng.longitude);
          }
          else
              callToast("You've no internet connection. Please try again.");
      }
    }

    private String getTagsData(String tag1,String tag2,String tag3,String tag4,String tag5)
    {
        String tagsdata;
        TagsFormat format;
        if(tag1.length()>0)
        {
            if(tagsFormatList.size()>0)
                tagsFormatList.get(0).setTagName(tag1);
            else {
                format=new TagsFormat();
                format.setTagId("");
                format.setTagName(tag1);
                tagsFormatList.add(format);
            }
        }
        if(tag2.length()>0)
        {
            if(tagsFormatList.size()>1)
                tagsFormatList.get(1).setTagName(tag2);
            else {
                format=new TagsFormat();
                format.setTagId("");
                format.setTagName(tag2);
                tagsFormatList.add(format);
            }
        }
        if(tag3.length()>0)
        {
            if(tagsFormatList.size()>2)
                tagsFormatList.get(2).setTagName(tag3);
            else {
                format=new TagsFormat();
                format.setTagId("");
                format.setTagName(tag3);
                tagsFormatList.add(format);
            }
        }
        if(tag4.length()>0)
        {
            if(tagsFormatList.size()>3)
                tagsFormatList.get(3).setTagName(tag4);
            else {
                format=new TagsFormat();
                format.setTagId("");
                format.setTagName(tag4);
                tagsFormatList.add(format);
            }
        }
        if(tag5.length()>0)
        {
            if(tagsFormatList.size()>4)
                tagsFormatList.get(4).setTagName(tag5);
            else {
                format=new TagsFormat();
                format.setTagId("");
                format.setTagName(tag5);
                tagsFormatList.add(format);
            }
        }
        tagsdata=new Gson().toJson(tagsFormatList);
        return tagsdata;
    }

    private boolean isValidate(String val)
    {
        if(val==null)
            return false;
        else if(val.length()==0 || val.equalsIgnoreCase("null"))
            return false;
        else
            return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.tv_multiphotos:
                Intent intent=new Intent(StoreEditActivity.this,StorePicsActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_addtags:
                if(layout_tag1.getVisibility()==View.GONE)
                    layout_tag1.setVisibility(View.VISIBLE);
                else {
                    if(layout_tag5.getVisibility()==View.VISIBLE)
                        callToast("Maximum tags only 5.");
                    if(layout_tag4.getVisibility()==View.VISIBLE)
                        layout_tag5.setVisibility(View.VISIBLE);
                    if(layout_tag3.getVisibility()==View.VISIBLE)
                        layout_tag4.setVisibility(View.VISIBLE);
                    if(layout_tag2.getVisibility()==View.VISIBLE)
                        layout_tag3.setVisibility(View.VISIBLE);
                    if(layout_tag1.getVisibility()==View.VISIBLE)
                        layout_tag2.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.et_oprntime:
                timePickerDialog((EditText) view);
                break;
            case R.id.et_closingtime:
                timePickerDialog((EditText) view);
                break;

            case R.id.iv_back_button:
                finish();
                break;

            case R.id.iv_edit:
               disableInputFields(true);
                break;

            case R.id.iv_storebanner:
                if(!checkingPermissionAreEnabledOrNot())
                    requestMultiplePermission();
                else
                    picGalleryImage();

                break;

            case R.id.layout_save:
                checkInputValidations();
                break;
        }
    }

    private void disableInputFields(boolean isEnable)
    {
        et_storecontactnumber.setEnabled(isEnable);
        et_oprntime.setEnabled(isEnable);
        et_oprntime.setClickable(isEnable);
        et_closingtime.setEnabled(isEnable);
        et_closingtime.setClickable(isEnable);
        et_website.setEnabled(isEnable);
        et_doornumber.setEnabled(isEnable);
        tv_storeaddress.setClickable(isEnable);
        et_state.setEnabled(isEnable);
        et_tag1.setEnabled(isEnable);
        et_tag2.setEnabled(isEnable);
        et_tag3.setEnabled(isEnable);
        et_tag4.setEnabled(isEnable);
        et_tag5.setEnabled(isEnable);
        iv_storebanner.setEnabled(isEnable);
        spinner.setEnabled(isEnable);
    }

    public void timePickerDialog(final EditText view) {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar calendar=Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                calendar.set(Calendar.MINUTE,selectedMinute);

                //little h uses 12 hour format and big H uses 24 hour format
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm:a");

                //format takes in a Date, and Time is a sublcass of Date
                String s = simpleDateFormat.format(calendar.getTime());
                view.setText( s);
                //view.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void callToast(String msg)
    {
        Toast.makeText(StoreEditActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    public void getLocation(View view) {
        Intent intent=new Intent(StoreEditActivity.this,MapsActivity.class);
        startActivityForResult(intent,125);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==125){
                latLng=data.getParcelableExtra("latlng");
                String add=data.getStringExtra("add");
                tv_storeaddress.setText(""+add);
                Log.e("LatLng",""+latLng.toString());
                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(addresses.size()>0)
                    {
                        et_state.setText(addresses.get(0).getAdminArea());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    String path=resultUri.getPath();
                    Log.e("path is","path "+path);
                    Picasso.with(StoreEditActivity.this).load(new File(path)).placeholder(R.mipmap.profile_img)
                            .error(R.mipmap.profile_img)
                            .into(iv_storebanner);
                    gallery_path=path;
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
    }

    public boolean checkingPermissionAreEnabledOrNot() {
        int camera = ContextCompat.checkSelfPermission(StoreEditActivity.this, CAMERA);
        int write = ContextCompat.checkSelfPermission(StoreEditActivity.this, WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(StoreEditActivity.this, READ_EXTERNAL_STORAGE);
        return camera == PackageManager.PERMISSION_GRANTED && read==PackageManager.PERMISSION_GRANTED;
    }

    private void requestMultiplePermission() {

        ActivityCompat.requestPermissions(StoreEditActivity.this, new String[]
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

    private void getstoreinfoService(final String merchantid)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MerchantApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMerchantApis service = retrofit.create(RetrofitMerchantApis.class);
        Call<MerchantStoreUpdateModel> call = service.getstoreinfo(merchantid);
        call.enqueue(new Callback<MerchantStoreUpdateModel>() {
            @Override
            public void onResponse(Call<MerchantStoreUpdateModel> call, Response<MerchantStoreUpdateModel> response) {
                dialog.dismiss();
                MerchantStoreUpdateModel model=response.body();
                if(model!=null)
                {
                    if(model.getStatus()==1) {
                      setValuesToInputFields(model);
                    }
                    else
                        callToast(model.getResult());
                }
                else {
                    callToast("No data Found");
                }
            }

            @Override
            public void onFailure(Call<MerchantStoreUpdateModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    private void updateStoreWithImage(String merchant_id, String store_number, String open_time, String close_time, String address, String state, String tags,String image_path,String door_number,String website,String typeofstore,String lat,String longn){
        Log.e("update store","lat= "+lat+" long= "+longn);
        MultipartBody.Part fileToUpload=null;
        final File file=new File(image_path);
        if(file.exists()) {
            RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
        }
        RequestBody mid = RequestBody.create(MediaType.parse("text/plain"),merchant_id);
        RequestBody st_no = RequestBody.create(MediaType.parse("text/plain"),store_number);
        RequestBody o_time = RequestBody.create(MediaType.parse("text/plain"),open_time);
        RequestBody c_time = RequestBody.create(MediaType.parse("text/plain"),close_time);
        RequestBody addr = RequestBody.create(MediaType.parse("text/plain"),address);
        RequestBody st = RequestBody.create(MediaType.parse("text/plain"),state);
        RequestBody tagsdata = RequestBody.create(MediaType.parse("text/plain"),tags);
        RequestBody dr_no = RequestBody.create(MediaType.parse("text/plain"),door_number);
        RequestBody str_website = RequestBody.create(MediaType.parse("text/plain"),website);
        RequestBody str_catid = RequestBody.create(MediaType.parse("text/plain"),typeofstore);
        RequestBody str_lat = RequestBody.create(MediaType.parse("text/plain"),lat);
        RequestBody str_long = RequestBody.create(MediaType.parse("text/plain"),longn);
        Call<MerchantStoreUpdateModel> call= RetrofitMerchantApis.Factory.create(this).updateStoreWithImage(fileToUpload,mid,st_no,o_time,c_time,addr,st,tagsdata,dr_no,str_website,str_catid,str_lat,str_long);
        dialog.show();
        call.enqueue(new Callback<MerchantStoreUpdateModel>() {
            @Override
            public void onResponse(Call<MerchantStoreUpdateModel> call, Response<MerchantStoreUpdateModel> response) {
                dialog.dismiss();
                MerchantStoreUpdateModel model=response.body();
                if(model.getStatus()==1){
                    callToast("Updated Successfully");
                    setValuesToInputFields(model);
                }
                else {
                    callToast(model.getResult());
                }
            }

            @Override
            public void onFailure(Call<MerchantStoreUpdateModel> call, Throwable t) {
                dialog.dismiss();
                Log.e("add offer onFailure",""+call.toString());
            }
        });
    }

    private void updateStore(String merchant_id, String store_number, String open_time, String close_time, String address, String state, String tags,String door_number,String website,String typeofstore,String lat,String longn){
        Log.e("update store","lat= "+lat+" long= "+longn);
        RequestBody mid = RequestBody.create(MediaType.parse("text/plain"),merchant_id);
        RequestBody st_no = RequestBody.create(MediaType.parse("text/plain"),store_number);
        RequestBody o_time = RequestBody.create(MediaType.parse("text/plain"),open_time);
        RequestBody c_time = RequestBody.create(MediaType.parse("text/plain"),close_time);
        RequestBody addr = RequestBody.create(MediaType.parse("text/plain"),address);
        RequestBody st = RequestBody.create(MediaType.parse("text/plain"),state);
        RequestBody tagsdata = RequestBody.create(MediaType.parse("text/plain"),tags);
        RequestBody dr_no = RequestBody.create(MediaType.parse("text/plain"),door_number);
        RequestBody str_website = RequestBody.create(MediaType.parse("text/plain"),website);
        RequestBody str_catid = RequestBody.create(MediaType.parse("text/plain"),typeofstore);
        RequestBody str_lat = RequestBody.create(MediaType.parse("text/plain"),lat);
        RequestBody str_long = RequestBody.create(MediaType.parse("text/plain"),longn);
        Call<MerchantStoreUpdateModel> call= RetrofitMerchantApis.Factory.create(this).updateStore(mid,st_no,o_time,c_time,addr,st,tagsdata,dr_no,str_website,str_catid,str_lat,str_long);
        dialog.show();
        call.enqueue(new Callback<MerchantStoreUpdateModel>() {
            @Override
            public void onResponse(Call<MerchantStoreUpdateModel> call, Response<MerchantStoreUpdateModel> response) {
                dialog.dismiss();
                MerchantStoreUpdateModel model=response.body();
                if(model.getStatus()==1){
                    callToast("Updated Successfully");
                    setValuesToInputFields(model);
                }
                else {
                    callToast(model.getResult());
                }
            }

            @Override
            public void onFailure(Call<MerchantStoreUpdateModel> call, Throwable t) {
                dialog.dismiss();
                Log.e("add offer onFailure",""+call.toString());
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(i>0)
        {
            cat_id=categorieslist.get(i).getId();
        }
        else
            cat_id="-1";
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
