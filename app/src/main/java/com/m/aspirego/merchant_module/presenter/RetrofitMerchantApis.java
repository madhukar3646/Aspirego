package com.m.aspirego.merchant_module.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.models.MOffersResponce;
import com.m.aspirego.merchant_module.models.MerchantPhotoUpdateResponse;
import com.m.aspirego.merchant_module.models.MerchantPhotosResponseModel;
import com.m.aspirego.merchant_module.models.MerchantStoreUpdateModel;
import com.m.aspirego.merchant_module.models.RequirementsModel;
import com.m.aspirego.merchant_module.models.ReviewslistResponse;
import com.m.aspirego.user_module.presenter.ApiUrls;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by madhu on 8/2/2018.
 */

public interface RetrofitMerchantApis {

        class Factory {
            public static RetrofitMerchantApis create(Context contextOfApplication) {

                // default time out is 15 seconds
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .build();

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(MerchantApiUrls.BASEURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
                return retrofit.create(RetrofitMerchantApis.class);
            }
        }

    @FormUrlEncoded
    @POST("signup")
    Call<MLogin> signupService(@Field("type") String type,
                                   @Field("name") String name,
                                   @Field("mobile_number") String mobile_number,
                                   @Field("password") String password,
                                   @Field("device_type") String device_type,
                                   @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("signup")
    Call<MLogin> signupSocialService(@Field("type") String type,
                               @Field("name") String name,
                               @Field("mobile_number") String mobile_number,
                               @Field("password") String password,
                               @Field("device_type") String device_type,
                               @Field("device_token") String device_token,
                               @Field("facebook_id") String facebook_id,
                               @Field("google_id") String google_id);

    @FormUrlEncoded
    @POST("verifyotp")
    Call<MLogin> verifyOTPService(@Field("mobile_number") String mobile_number, @Field("otp") String otp);

    @FormUrlEncoded
    @POST("resendotp")
    Call<MLogin> resendOTPService(@Field("mobile_number") String mobile_number);

    @FormUrlEncoded
    @POST("login")
    Call<MLogin> loginService(@Field("mobile_number") String mobile_number,
                              @Field("password") String password,
                              @Field("device_type") String device_type,
                              @Field("device_token") String device_token);


    @FormUrlEncoded
    @POST("profile")
    Call<MLogin> profileService(@Field("merchant_id") String merchant_id);

    @FormUrlEncoded
    @POST("updateprofile")
    Call<MLogin> updateProfile(@Field("merchant_id") String merchant_id,@Field("merchant_name") String merchant_name,@Field("email") String email);

    @FormUrlEncoded
    @POST(MerchantApiUrls.changePassword)
    Call<MLogin> changePassword(@Field("oldpassword") String oldpassword,@Field("newpassword") String newpassword,@Field("merchant_id") String user_id);

    @FormUrlEncoded
    @POST(ApiUrls.forgotPassword)
    Call<MLogin> forgetPassword(@Field("mobile_number") String mobile_number);


    @FormUrlEncoded
    @POST("requirements")
    Call<RequirementsModel> getRequirements(@Field("merchant_id") String merchant_id);

    @FormUrlEncoded
    @POST("applyloan")
    Call<MLogin> applyloanService(@Field("merchant_id") String merchant_id,@Field("mobile_number") String mobile_number,@Field("email") String email,@Field("amount") String amount);
    @FormUrlEncoded
    @POST(MerchantApiUrls.offers)
    Call<MOffersResponce> getOffers(@Field("merchant_id") String mechant_id);
    @Multipart
    @POST(MerchantApiUrls.addoffer)
    Call<MLogin> addOffer(@Part MultipartBody.Part file,
                                @Part("merchant_id") RequestBody merchant_id,
                                @Part("offer_name") RequestBody offer_name,
                                @Part("price") RequestBody price,
                                @Part("offer_price") RequestBody offer_price,
                                @Part("discount") RequestBody discount,
                                @Part("valid_from") RequestBody valid_from,
                                @Part("valid_to") RequestBody valid_to,
                                @Part("category_id") RequestBody category_id);
    @Multipart
    @POST(MerchantApiUrls.editoffer)
    Call<MLogin> editoffer(@Part MultipartBody.Part file,
                                 @Part("merchant_id") RequestBody merchant_id,
                                 @Part("offer_name") RequestBody offer_name,
                                 @Part("price") RequestBody price,
                                 @Part("offer_price") RequestBody offer_price,
                                 @Part("discount") RequestBody discount,
                                 @Part("valid_from") RequestBody valid_from,
                                 @Part("valid_to") RequestBody valid_to,
                                 @Part("offer_id") RequestBody offer_id,
                                 @Part("category_id") RequestBody category_id
    );
    @FormUrlEncoded
    @POST(MerchantApiUrls.SOCIAL_LOGIN)
    Call<MLogin> socialLoginVerfy(@Field("type") String type, @Field("socialid") String socialid);

    @Multipart
    @POST("merchantresponseReq")
    Call<MLogin> uploadMerchantResponseWithImage(@Part MultipartBody.Part file,
                           @Part("merchant_id") RequestBody merchant_id,
                           @Part("requirement_id") RequestBody requirement_id,
                           @Part("product_name") RequestBody product_name,
                           @Part("price") RequestBody price,
                           @Part("offer_price") RequestBody offer_price,
                           @Part("delivery_option") RequestBody delivery_option,
                           @Part("product_type") RequestBody product_type);

    @Multipart
    @POST("merchantresponseReq")
    Call<MLogin> uploadMerchantResponse(@Part("merchant_id") RequestBody merchant_id,
                                        @Part("requirement_id") RequestBody requirement_id,
                                        @Part("product_name") RequestBody product_name,
                                        @Part("price") RequestBody price,
                                        @Part("offer_price") RequestBody offer_price,
                                        @Part("delivery_option") RequestBody delivery_option,
                                        @Part("product_type") RequestBody product_type);

    @FormUrlEncoded
    @POST("getstoreinfo")
    Call<MerchantStoreUpdateModel> getstoreinfo(@Field("merchant_id") String merchant_id);

    @Multipart
    @POST("updatestore")
    Call<MerchantStoreUpdateModel> updateStoreWithImage(@Part MultipartBody.Part file,
                                               @Part("merchant_id") RequestBody merchant_id,
                                               @Part("store_number") RequestBody store_number,
                                               @Part("open_time") RequestBody open_time,
                                               @Part("close_time") RequestBody close_time,
                                               @Part("address") RequestBody address,
                                               @Part("state") RequestBody state,
                                               @Part("tags") RequestBody tags,
                                                        @Part("door_number") RequestBody door_number,
                                                        @Part("website") RequestBody website,
                                                        @Part("type_of_store") RequestBody type_of_store, @Part("latitude") RequestBody latitude,
                                                        @Part("longitude") RequestBody longitude);

    @Multipart
    @POST("updatestore")
    Call<MerchantStoreUpdateModel> updateStore(@Part("merchant_id") RequestBody merchant_id,
                                               @Part("store_number") RequestBody store_number,
                                               @Part("open_time") RequestBody open_time,
                                               @Part("close_time") RequestBody close_time,
                                               @Part("address") RequestBody address,
                                               @Part("state") RequestBody state,
                                               @Part("tags") RequestBody tags,
                                               @Part("door_number") RequestBody door_number,
                                               @Part("website") RequestBody website,
                                               @Part("type_of_store") RequestBody type_of_store,
                                               @Part("latitude") RequestBody latitude,
                                               @Part("longitude") RequestBody longitude);

    @FormUrlEncoded
    @POST("reviews")
    Call<ReviewslistResponse> getReviewsList(@Field("merchant_id") String merchant_id);

    @Multipart
    @POST("uploadmerchantphoto")
    Call<MLogin> uploadmerchantphoto(@Part MultipartBody.Part file,
                          @Part("merchant_id") RequestBody merchant_id);

    @FormUrlEncoded
    @POST("merchantphotos")
    Call<MerchantPhotosResponseModel> merchantphotos(@Field("merchant_id") String merchant_id);

    @Multipart
    @POST("updatemerchantphoto")
    Call<MerchantPhotoUpdateResponse> updatemerchantphoto(@Part MultipartBody.Part file,
                                                          @Part("id") RequestBody id);

    @FormUrlEncoded
    @POST("deletemerchantphoto")
    Call<MLogin> deletemerchantphoto(@Field("id") String id);

    @FormUrlEncoded
    @POST("deleteproduct")
    Call<MLogin> deleteproduct(@Field("product_id") String product_id);

    @FormUrlEncoded
    @POST("addproduct")
    Call<MLogin> addproduct(@Field("merchant_id") String merchant_id,@Field("product_name") String product_name);

}

