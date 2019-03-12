package com.m.aspirego.user_module.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.m.aspirego.R;
import com.m.aspirego.merchant_module.activities.ReviewsActivity;
import com.m.aspirego.merchant_module.models.MerchantPhotosResponseModel;
import com.m.aspirego.merchant_module.models.Merchantphoto;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.adapters.MarchantPhotoViewerAdapter;
import com.m.aspirego.user_module.adapters.MarchantPhotosAdapter;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.user_module.models.LoginModel;
import com.m.aspirego.user_module.models.StoreModel;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.m.aspirego.user_module.presenter.RetrofitApis;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreDetailsScreen extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.layout_backbtn)
    RelativeLayout layout_backbtn;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_call)
    ImageView iv_call;
    @BindView(R.id.iv_direction)
    ImageView iv_direction;
    @BindView(R.id.iv_bannerimage)
    ImageView iv_bannerimage;
    @BindView(R.id.tv_likescount)
    TextView tv_likescount;
    @BindView(R.id.tv_distance)
    TextView tv_distance;
    @BindView(R.id.tv_storename)
    TextView tv_storename;
    @BindView(R.id.tv_place)
    TextView tv_place;
    @BindView(R.id.tv_rating)
    TextView tv_rating;
    @BindView(R.id.tv_reviewscount)
    TextView tv_reviewscount;
    @BindView(R.id.iv_showreviews)
    ImageView iv_showreviews;
    @BindView(R.id.layout_like)
    RelativeLayout layout_like;
    @BindView(R.id.iv_likeblack)
    ImageView iv_likeblack;
    @BindView(R.id.layout_ratebtn)
    RelativeLayout layout_ratebtn;
    @BindView(R.id.layout_reviewbtn)
    RelativeLayout layout_reviewbtn;
    @BindView(R.id.layout_offers)
    RelativeLayout layout_offers;
    @BindView(R.id.tv_offers)
    TextView tv_offers;

    @BindView(R.id.recyclerview_photos)
    RecyclerView recyclerview_photos;
    @BindView(R.id.tv_timings)
    TextView tv_timings;
    @BindView(R.id.tv_contact)
    TextView tv_contact;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_products)
    TextView tv_products;
    @BindView(R.id.tv_website)
    TextView tv_website;
    @BindView(R.id.tv_textonimage)
    TextView tv_textonimage;

    private Dialog dialog,alertdialog;
    private ConnectionDetector connectionDetector;
    private SessionManagement sessionManagement;
    private String merchantid,merchant_name,lattitude,longnitude,storelattitude,storelognitude;
    private MarchantPhotosAdapter photosAdapter;
    private List<Merchantphoto> photolist=new ArrayList<>();
    private String str_rating="",userid;
    private String str_merchantmobilenumber;
    private int like_status=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details_screen);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        dialog = new Dialog(StoreDetailsScreen.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(StoreDetailsScreen.this);
         sessionManagement= SessionManagement.getSession(this);

        merchantid=getIntent().getStringExtra("merchantid");
        Log.e("merchantid","is"+merchantid);
        userid=sessionManagement.getValueFromPreference(SessionManagement.USERID);

        layout_backbtn.setOnClickListener(this);
        iv_call.setOnClickListener(this);
        iv_direction.setOnClickListener(this);
        layout_like.setOnClickListener(this);
        layout_ratebtn.setOnClickListener(this);
        layout_reviewbtn.setOnClickListener(this);
        layout_offers.setOnClickListener(this);
        tv_reviewscount.setOnClickListener(this);
        iv_showreviews.setOnClickListener(this);
        tv_products.setOnClickListener(this);

        recyclerview_photos.setLayoutManager(new GridLayoutManager(this, 4));
        photosAdapter=new MarchantPhotosAdapter(StoreDetailsScreen.this,photolist);
        recyclerview_photos.setAdapter(photosAdapter);


        lattitude=sessionManagement.getValueFromPreference(SessionManagement.USERLATTITUDE);
        longnitude=sessionManagement.getValueFromPreference(SessionManagement.USERLONGNITUDE);
        if (connectionDetector.isConnectingToInternet()) {
            storeDetailsService(userid, merchantid, lattitude, longnitude);
        } else {
            callToast("You've no internet connection. Please try again.");
        }
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/PlayfairDisplay-Regular.otf");
        tv_textonimage.setTypeface(custom_font);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.layout_backbtn:
                finish();
                break;
            case R.id.iv_call:
                if(str_merchantmobilenumber!=null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + str_merchantmobilenumber));
                    startActivity(intent);
                }
                else
                    callToast("Mobile number not available  right now.");
                break;
            case R.id.iv_direction:
                if(lattitude!=null && longnitude!=null)
                {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+lattitude+","+longnitude+"&daddr="+storelattitude+","+storelognitude));
                    startActivity(intent);
                }
                else {
                    callToast("Location not detected");
                }
                break;
            case R.id.layout_like:
                if(connectionDetector.isConnectingToInternet())
                {
                    likeMerchantService(sessionManagement.getValueFromPreference(SessionManagement.USERID),merchantid);
                }
                else {
                    callToast("You've no internet connection. Please try again.");
                }
                break;
            case R.id.layout_ratebtn:
                 ratingAndReviewsDialog(true);
                break;
            case R.id.layout_reviewbtn:
                ratingAndReviewsDialog(false);
                break;

            case R.id.layout_offers:
                Intent offers=new Intent(StoreDetailsScreen.this,MarchantOffersActivity.class);
                offers.putExtra("merchant_name",merchant_name);
                offers.putExtra("merchant_id",merchantid);
                startActivity(offers);
                break;

            case R.id.tv_reviewscount:
                gotoReviewsScreen(merchantid);
                break;
            case R.id.iv_showreviews:
                gotoReviewsScreen(merchantid);
                break;
            case R.id.tv_products:

                break;
        }
    }

    private void gotoReviewsScreen(String merchantid)
    {
        Intent intent=new Intent(StoreDetailsScreen.this, ReviewsActivity.class);
        intent.putExtra("merchant_id",merchantid);
        startActivity(intent);
    }
    private void storeDetailsService(String user_id,final String merchant_id, String latitude, String longitude)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<StoreModel> call = service.storeDetailsService(user_id,merchant_id,latitude,longitude);
        call.enqueue(new Callback<StoreModel>() {
            @Override
            public void onResponse(Call<StoreModel> call, Response<StoreModel> response) {
                if(dialog.isShowing())
                 dialog.dismiss();
                StoreModel model=response.body();
                if(model!=null)
                {
                   if(model.getStatus().equalsIgnoreCase("1"))
                   {
                       str_merchantmobilenumber=model.getStoreDetails().getMobileNumber();
                       storelattitude=model.getStoreDetails().getLatitude();
                       storelognitude=model.getStoreDetails().getLongitude();
                       merchant_name=model.getStoreDetails().getMerchantName();
                       settingResponseToFields(model);
                       merchantphotosSevice(model.getStoreDetails().getMerchantId());
                   }
                   else {
                       callToast(model.getResult());
                   }
                }
            }

            @Override
            public void onFailure(Call<StoreModel> call, Throwable t) {
                if(dialog.isShowing())
                dialog.dismiss();
                callToast(t.getMessage());
                Log.e("get response","onFailure");
            }
        });

    }

    private void merchantphotosSevice(String merchant_id) {
        dialog.show();
        Call<MerchantPhotosResponseModel> call = RetrofitMerchantApis.Factory.create(this).merchantphotos(merchant_id);
        call.enqueue(new Callback<MerchantPhotosResponseModel>() {
            @Override
            public void onResponse(Call<MerchantPhotosResponseModel> call, Response<MerchantPhotosResponseModel> response) {
                if(dialog.isShowing())
                dialog.dismiss();
                MerchantPhotosResponseModel model=response.body();
                if(model!=null)
                {
                    if(model.getStatus()==1)
                    {
                        Log.e("merchant id",""+model.getMerchantphotos().size());
                        if(model.getMerchantphotos()!=null && model.getMerchantphotos().size()>0)
                        {
                            photolist.clear();
                            photolist.addAll(model.getMerchantphotos());
                            photosAdapter.notifyDataSetChanged();
                        }
                    }
                    else {
                        callToast(model.getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<MerchantPhotosResponseModel> call, Throwable t) {
                if(dialog.isShowing())
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }
    private void likeMerchantService(final String userid,final String merchantid)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<LoginModel> call = service.likeMerchantService(userid,merchantid,""+like_status);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                dialog.dismiss();
                LoginModel model=response.body();
                if(model!=null)
                {
                    callToast(model.getResult());
                    if(model.getStatus().equalsIgnoreCase("1"))
                    {
                        storeDetailsService(userid,merchantid,lattitude,longnitude);
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

    private void addMerchantReviewService(final String userid,final String merchantid,String review)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<LoginModel> call = service.addMerchantReviewService(userid,merchantid,review);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                dialog.dismiss();
                LoginModel model=response.body();
                if(model!=null)
                {
                    callToast(model.getResult());
                    if(model.getStatus().equalsIgnoreCase("1"))
                    {
                        if(alertdialog!=null)
                        {
                            if(alertdialog.isShowing())
                                alertdialog.dismiss();
                        }
                        storeDetailsService(userid,merchantid,lattitude,longnitude);
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
    private void addMerchantRatingService(final String userid,final String merchantid,String rating)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApis service = retrofit.create(RetrofitApis.class);
        Call<LoginModel> call = service.addMerchantRatingService(userid,merchantid,rating);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                dialog.dismiss();
                LoginModel model=response.body();
                if(model!=null)
                {
                    callToast(model.getResult());
                    if(model.getStatus().equalsIgnoreCase("1"))
                    {
                        if(alertdialog!=null)
                        {
                            if(alertdialog.isShowing())
                                alertdialog.dismiss();
                        }
                        storeDetailsService(userid,merchantid,lattitude,longnitude);
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
    private void settingResponseToFields(StoreModel model)
    {
        like_status=model.getStoreDetails().getLikestatus();
        if(like_status==1) {
            iv_likeblack.setImageResource(R.mipmap.like_active);
            like_status=0;
        }
        else {
            iv_likeblack.setImageResource(R.mipmap.like_icon);
            like_status=1;
        }

       tv_title.setText(model.getStoreDetails().getMerchantName());
       tv_likescount.setText(""+model.getStoreDetails().getLikecount()+" Persons like this");
       tv_distance.setText(model.getStoreDetails().getDistance()+"Kms");
       tv_storename.setText(model.getStoreDetails().getMerchantName());
       tv_textonimage.setText(model.getStoreDetails().getMerchantName());
       tv_place.setText(model.getStoreDetails().getState());
       tv_offers.setText(""+model.getStoreDetails().getOffers()+" OFFERS");
       tv_rating.setText(""+model.getStoreDetails().getRatingcount());
       tv_reviewscount.setText(""+model.getStoreDetails().getReviewcount());
       tv_timings.setText(model.getStoreDetails().getOpenTime()+"-"+model.getStoreDetails().getCloseTime());
       tv_contact.setText(model.getStoreDetails().getMobileNumber()+"\n"+model.getStoreDetails().getEmail());
       tv_website.setText("Website: "+model.getStoreDetails().getWebsite());
       tv_address.setText(model.getStoreDetails().getAddress().replaceAll(",", "\n"));

        if(model.getStoreDetails().getMerchantBanner()!=null && !model.getStoreDetails().getMerchantBanner().equalsIgnoreCase("null") && model.getStoreDetails().getMerchantBanner().trim().length()>0) {
            iv_bannerimage.setBackgroundResource(R.drawable.roundrect_white);
            Picasso.with(StoreDetailsScreen.this).load(ApiUrls.MERCHANTBANNERS + model.getStoreDetails().getMerchantBanner()).placeholder(R.mipmap.logo_icon)
                    .error(R.mipmap.logo_icon)
                    .into(iv_bannerimage);
            tv_textonimage.setVisibility(View.GONE);
        }
        else {
            iv_bannerimage.setBackgroundResource(R.drawable.roundrect_primary);
            tv_textonimage.setVisibility(View.VISIBLE);
        }
    }

    private void callToast(String msg)
    {
        Toast.makeText(StoreDetailsScreen.this,msg,Toast.LENGTH_SHORT).show();
    }

    public void ratingAndReviewsDialog(final boolean isForRating)
    {
        str_rating="";
        alertdialog=new Dialog(StoreDetailsScreen.this);
        alertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertdialog.setContentView(R.layout.rating_reviewslayout);
        alertdialog.setCanceledOnTouchOutside(true);
        alertdialog.setCancelable(true);

        RelativeLayout layout_ratingtitle=(RelativeLayout)alertdialog.findViewById(R.id.layout_rating);
        RelativeLayout layout_ratingbar=(RelativeLayout)alertdialog.findViewById(R.id.layout_ratingbar);
        RelativeLayout layout_reviewtitle=(RelativeLayout)alertdialog.findViewById(R.id.layout_reviewtitle);
        RelativeLayout layout_reviewbox=(RelativeLayout)alertdialog.findViewById(R.id.layout_reviewbox);

        if(isForRating)
        {
            layout_ratingbar.setVisibility(View.VISIBLE);
            layout_ratingtitle.setVisibility(View.VISIBLE);
            layout_reviewtitle.setVisibility(View.GONE);
            layout_reviewbox.setVisibility(View.GONE);
        }
        else {
            layout_ratingbar.setVisibility(View.GONE);
            layout_ratingtitle.setVisibility(View.GONE);
            layout_reviewtitle.setVisibility(View.VISIBLE);
            layout_reviewbox.setVisibility(View.VISIBLE);
        }

        Button submitbtn=(Button)alertdialog.findViewById(R.id.submitbtn);
        RatingBar userrating=(RatingBar)alertdialog.findViewById(R.id.ratingbar);
        final EditText userreview=(EditText)alertdialog.findViewById(R.id.et_review);

        userrating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                str_rating=""+v;
            }
        });
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isForRating)
                {
                    if(str_rating.trim().length()==0 || str_rating==null || str_rating.equalsIgnoreCase("0")||str_rating.equalsIgnoreCase("0.0") || str_rating.equalsIgnoreCase("null"))
                        callToast("Please give your rating and submit");
                    else if(connectionDetector.isConnectingToInternet())
                    {
                        addMerchantRatingService(sessionManagement.getValueFromPreference(SessionManagement.USERID),merchantid,str_rating);
                    }
                    else {
                        callToast("You've no internet connection. Please try again.");
                    }
                }
                else {
                    String str_review = userreview.getText().toString();
                    if (str_review.trim().length() == 0)
                        callToast("Please write your review");
                    else if(connectionDetector.isConnectingToInternet())
                    {
                        addMerchantReviewService(sessionManagement.getValueFromPreference(SessionManagement.USERID),merchantid,str_review);
                    }
                    else {
                        callToast("You've no internet connection. Please try again.");
                    }
                }
            }
        });
        alertdialog.show();
        alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void displayPhotoviewerDialog(int clicked_pos)
    {
        final Dialog dialog=new Dialog(StoreDetailsScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.photoviewer_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        RecyclerView recyclerView=(RecyclerView)dialog.findViewById(R.id.recyclerview_photos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MarchantPhotoViewerAdapter photosAdapter=new MarchantPhotoViewerAdapter(StoreDetailsScreen.this,photolist);
        recyclerView.setAdapter(photosAdapter);
        recyclerView.smoothScrollToPosition(clicked_pos);

        ImageView iv_close=(ImageView)dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    }

}
