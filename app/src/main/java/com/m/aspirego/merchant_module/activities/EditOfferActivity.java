package com.m.aspirego.merchant_module.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.m.aspirego.R;
import com.m.aspirego.VolleyServiceCall;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.adapters.CategoryDropDownAdapter;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.models.CategoryModel;
import com.m.aspirego.user_module.models.LoginModel;
import com.m.aspirego.user_module.models.Offer;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.m.aspirego.user_module.presenter.RetrofitApis;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class EditOfferActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    public static final String ADD_OFFER = "add_new";
    public static final String ISEDIT = "is_edit";
    @BindView(R.id.offer_image)
    ImageView offer_image;

    @BindView(R.id.back_btn)
    ImageView back_btn;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_upload)
    TextView tv_upload;

    @BindView(R.id.product_name_edt)
    EditText product_name_edt;

    @BindView(R.id.price_edt)
    EditText price_edt;

    @BindView(R.id.offer_edt)
    EditText offer_edt;

    @BindView(R.id.from_edt)
    EditText from_edt;

    @BindView(R.id.to_edt)
    EditText to_edt;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.submit_btn)
    TextView submit_btn;
    boolean addNew=false;
    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    private SessionManagement sessionManagement;
    private String galley_imagepath=null;
    Offer offer;
    private List<CategoryModel.Category> categorieslist;
    private CategoryDropDownAdapter categoryDropDownAdapter;
    private String cat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer);
        ButterKnife.bind(this);
        from_edt.setOnClickListener(this);
        to_edt.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        tv_upload.setOnClickListener(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
       addNew=getIntent().getBooleanExtra(ADD_OFFER,false);

        dialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(this);
        sessionManagement=SessionManagement.getSession(this);
       if(!addNew){
               tv_title.setText("EDIT OFFER");
                offer = getIntent().getParcelableExtra(ISEDIT);
                cat_id=offer.getCategory_id();
               if (offer != null)
                   setValue(offer);

          }

        categorieslist=new ArrayList<>();
        CategoryModel.Category model=new CategoryModel().getCategoryObject();
        model.setName("--Select Category--");
        categorieslist.add(model);
        categoryDropDownAdapter=new CategoryDropDownAdapter(EditOfferActivity.this,categorieslist);
        spinner.setAdapter(categoryDropDownAdapter);
        spinner.setOnItemSelectedListener(this);
        if(connectionDetector.isConnectingToInternet())
            categoryService();
        else
            callToast("You've no internet connection. Please try again.");
    }

    private void setValue(Offer offer) {
        product_name_edt.setText(offer.getOfferName());
        price_edt.setText(offer.getPrice());
        offer_edt.setText(offer.getOfferPrice());
        Picasso.with(this).load(MerchantApiUrls.OFFERS_IMAGEPATH+offer.getImage()).into(offer_image);
        from_edt.setText(offer.getValidFrom());
        to_edt.setText(offer.getValidTo());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit_btn:
                if(!addNew&&offer!=null){
                     editOffer();
                }else if (addNew){
                   addnewOffer();
                }
                break;
            case R.id.tv_upload:
                if(!checkingPermissionAreEnabledOrNot())
                    requestMultiplePermission();
                else
                    picGalleryImage();
                break;
            case R.id.from_edt:
                openCalender((EditText) view);
                break;
            case R.id.to_edt:
                openCalender((EditText) view);
                break;
            case R.id.back_btn:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    private void addnewOffer() {
        String userid, offername, price, offerprice, discount, from, to;
        userid=sessionManagement.getValueFromPreference(SessionManagement.USERID);
        offername=product_name_edt.getText().toString();
        price=price_edt.getText().toString();
        offerprice=offer_edt.getText().toString();
        discount=getDiscount(price,offerprice);
        from=from_edt.getText().toString();
        to=to_edt.getText().toString();
         if(!isValiddate(offername))
         {
             callToast("please enter offername");return;
         }
        if(!isValiddate(price))
        {
            callToast("please enter price");return;
        }
        if(!isValiddate(offerprice))
        {
            callToast("please enter offerprice");return;
        }
        if(!isValiddate(userid))
        {
            callToast("please enter from date");return;
        }
        if(!isValiddate(userid))
        {
            callToast("please enter to date");return;
        }

        if(galley_imagepath==null){
            callToast("please select an image for offer");
            return;
        }

        if(connectionDetector.isConnectingToInternet()){
            {
//                addnewOffer(userid,offername,price,offerprice,discount,from,to);
                uploadeNewOffer(userid,offername,price,offerprice,discount,from,to,cat_id);
            }
        }else
            callToast("please check internet connection");


    }

    private boolean isValiddate(String s) {
        if(s==null)return false;
        if(s.trim().isEmpty())return false;
        return true;
    }

    private void editOffer() {
        String userid, offername, price, offerprice, discount, from, to;
        userid=sessionManagement.getValueFromPreference(SessionManagement.USERID);
        offername=product_name_edt.getText().toString();
        price=price_edt.getText().toString();
        offerprice=offer_edt.getText().toString();
        discount=getDiscount(price,offerprice);
        from=from_edt.getText().toString();
        to=to_edt.getText().toString();
        if(!isValiddate(offername))
        {
            callToast("please enter offername");
            return;
        }
        if(!isValiddate(price))
        {
            callToast("please enter price");
            return;
        }
        if(!isValiddate(offerprice))
        {
            callToast("please enter offerprice");
            return;
        }
        if(!isValiddate(userid))
        {
            callToast("please enter from date");
            return;
        }
        if(!isValiddate(userid))
        {
            callToast("please enter to date");
            return;
        }


        if(connectionDetector.isConnectingToInternet()){
             uploadeditOffer(userid,offername,price,offerprice,discount,from,to,offer.getOfferId(),cat_id);
        }else
            callToast("please check internet connection");

    }


    int mYear,mMonth,mDay;
    public void openCalender(final EditText view) {
        // TODO Auto-generated method stub
        // To show current date in the datepicker
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, selectedyear);
                myCalendar.set(Calendar.MONTH, selectedmonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                String myFormat = "dd MMM yyyy"; //Change as you need
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                Date date=myCalendar.getTime();
                String sDate=sdf.format(date);
                view.setText(sDate);
                mDay = selectedday;
                mMonth = selectedmonth;
                mYear = selectedyear;
            }
        }, mYear, mMonth, mDay);
        //mDatePicker.setTitle("Select date");
        mDatePicker.show();
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
    
    public void addnewOffer(String userid,String offername,String price,String offerprice,String discount,String from,String to)
    {
        dialog.show();
        Bitmap bitmap=null;
        if(galley_imagepath!=null)
            bitmap= BitmapFactory.decodeFile(galley_imagepath);

        Map<String,String> params=new HashMap<>();
        params.put("user_id",userid);
        params.put("offer_name",offername);
        params.put("price",price);
        params.put("offer_price",offerprice);
        params.put("discount",discount);
        params.put("valid_from",from);
        params.put("valid_to",to);
        VolleyServiceCall.uploadBitmap(this, MerchantApiUrls.BASEURL+"addoffer", "image", bitmap, params, new VolleyServiceCall.ServiceResponse() {
            @Override
            public void getResponse(String response) {
                dialog.dismiss();
                Gson gson=new Gson();
                LoginModel model=gson.fromJson(response,LoginModel.class);
                Log.e("upload response","volley is "+model.getResult());
                if(model!=null) {
                    if(model.getStatus().equalsIgnoreCase("1")) {
                    setResult(RESULT_OK);
                    finish();
                    }
                }
            }

            @Override
            public void getErrorResponse(String errormessage) {
                Log.e("upload response"," failed ");
                dialog.dismiss();
                callToast(errormessage);
            }
        });

    }
    public void editOffer(String userid,String offername,String price,String offerprice,String discount,String from,String to,String offer_id)
    {
        dialog.show();
        Bitmap bitmap=null;
        if(galley_imagepath!=null)
            bitmap= BitmapFactory.decodeFile(galley_imagepath);

        Map<String,String> params=new HashMap<>();
        params.put("user_id",userid);
        params.put("offer_name",offername);
        params.put("price",price);
        params.put("offer_price",offerprice);
        params.put("discount",discount);
        params.put("valid_from",from);
        params.put("valid_to",to);

        params.put("offer_id",offer_id);
        VolleyServiceCall.uploadBitmap(this, MerchantApiUrls.BASEURL+"editOffer", "image", bitmap, params, new VolleyServiceCall.ServiceResponse() {
            @Override
            public void getResponse(String response) {
                dialog.dismiss();
                Gson gson=new Gson();
                LoginModel model=gson.fromJson(response,LoginModel.class);
                Log.e("upload response","volley is "+model.getResult());
                if(model!=null) {
                    if(model.getStatus().equalsIgnoreCase("1")) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }

            @Override
            public void getErrorResponse(String errormessage) {
                Log.e("upload response"," failed ");
                dialog.dismiss();
                callToast(errormessage);
            }
        });

    }

    private String getDiscount(String original_price, String offer_price) {
        String discount=null;
        try {
            Integer org=Integer.valueOf(original_price);
            Integer off=Integer.valueOf(offer_price);
            Integer dis=(off*100)/org;
            discount=String.valueOf(100-dis);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

    return discount;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String path=resultUri.getPath();
                Log.e("path is","path "+path);
                Picasso.with(EditOfferActivity.this).load(new File(path)).placeholder(R.mipmap.profile_img)
                        .error(R.mipmap.profile_img)
                        .into(offer_image);
                galley_imagepath=path;
               // uploadProfilepic(path);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void callToast(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
void uploadeNewOffer(String userid, String offername, String price, String offerprice, String discount, String from, String to,String cat_id){
    MultipartBody.Part fileToUpload=null;
    if(galley_imagepath!=null&&!galley_imagepath.isEmpty()){
        final File file=new File(galley_imagepath);

        if(file.exists()) {
            RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                 fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
           }}
            RequestBody mid = RequestBody.create(MediaType.parse("text/plain"),userid);
            RequestBody offname = RequestBody.create(MediaType.parse("text/plain"),offername);
            RequestBody pri = RequestBody.create(MediaType.parse("text/plain"),price);
            RequestBody ofpr = RequestBody.create(MediaType.parse("text/plain"),offerprice);
            RequestBody dis = RequestBody.create(MediaType.parse("text/plain"),discount);
            RequestBody fromdate = RequestBody.create(MediaType.parse("text/plain"),from);
            RequestBody todate = RequestBody.create(MediaType.parse("text/plain"),to);
            RequestBody categoryid = RequestBody.create(MediaType.parse("text/plain"),cat_id);
    Call<MLogin> call=RetrofitMerchantApis.Factory.create(this).addOffer(fileToUpload,mid,offname,pri,ofpr,dis,fromdate,todate,categoryid);
    dialog.show();
    call.enqueue(new Callback<MLogin>() {
        @Override
        public void onResponse(Call<MLogin> call, Response<MLogin> response) {
            dialog.dismiss();
            MLogin body=response.body();
            if(body.getStatus()==1){
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        public void onFailure(Call<MLogin> call, Throwable t) {
            dialog.dismiss();

            Log.e("add offer onFailure",""+call.toString());
        }
    });



}
    void uploadeditOffer(String userid, String offername, String price, String offerprice, String discount, String from, String to, String offer_id,String cat_id){
        MultipartBody.Part fileToUpload=null;
        if(galley_imagepath!=null&&!galley_imagepath.isEmpty()){
            final File file=new File(galley_imagepath);

            if(file.exists()) {
                RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
            }
        }
            else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            fileToUpload = MultipartBody.Part.createFormData("image", "", attachmentEmpty);
        }
        RequestBody mid = RequestBody.create(MediaType.parse("text/plain"),userid);
        RequestBody offname = RequestBody.create(MediaType.parse("text/plain"),offername);
        RequestBody pri = RequestBody.create(MediaType.parse("text/plain"),price);
        RequestBody ofpr = RequestBody.create(MediaType.parse("text/plain"),offerprice);
        RequestBody dis = RequestBody.create(MediaType.parse("text/plain"),discount);
        RequestBody fromdate = RequestBody.create(MediaType.parse("text/plain"),from);
        RequestBody todate = RequestBody.create(MediaType.parse("text/plain"),to);
        RequestBody fid = RequestBody.create(MediaType.parse("text/plain"),offer_id);
        RequestBody category_id = RequestBody.create(MediaType.parse("text/plain"),cat_id);
        Call<MLogin> call=RetrofitMerchantApis.Factory.create(this).editoffer(fileToUpload,mid,offname,pri,ofpr,dis,fromdate,todate,fid,category_id);
        dialog.show();
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin body=response.body();
                if(body.getStatus()==1){
                    setResult(RESULT_OK);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();

                Log.e("onFailure",""+t.toString());
            }
        });
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
                setSpinnerItem(categorieslist);
                categoryDropDownAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    private void setSpinnerItem(List<CategoryModel.Category> categorieslist)
    {
       for(int i=0;i<categorieslist.size();i++)
       {
           if(categorieslist.get(i).getId()!=null)
           {
               Log.e("ids "+categorieslist.get(i).getId(),"id "+cat_id);
               if (categorieslist.get(i).getId().equalsIgnoreCase(cat_id)) {
                   spinner.setSelection(i);
                   break;
               }
           }
       }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(i>0)
        {
            cat_id=categorieslist.get(i).getId();
            Log.e("add offer","cat_id "+cat_id);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
