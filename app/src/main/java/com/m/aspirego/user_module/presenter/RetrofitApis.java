package com.m.aspirego.user_module.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.models.ProductListResponse;
import com.m.aspirego.user_module.models.CategoryModel;
import com.m.aspirego.user_module.models.CategoryStoreModel;
import com.m.aspirego.user_module.models.FeedsListResponce;
import com.m.aspirego.user_module.models.LoginModel;
import com.m.aspirego.user_module.models.MerchantResponseModel;
import com.m.aspirego.user_module.models.OffersListResponce;
import com.m.aspirego.user_module.models.StoreModel;
import com.m.aspirego.user_module.models.TagsModel;
import com.m.aspirego.user_module.models.UserRequirementsListResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
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

public interface RetrofitApis {

        class Factory {
            public static RetrofitApis create(Context contextOfApplication) {

                // default time out is 15 seconds
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build();

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiUrls.BASEURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
                return retrofit.create(RetrofitApis.class);
            }
        }

    @FormUrlEncoded
    @POST("signup")
    Call<LoginModel> signupService(@Field("name") String name,@Field("mobile_number") String mobile_number,
    @Field("password") String password,@Field("device_type") String device_type,@Field("device_token") String device_token,@Field("type") String type);

    @FormUrlEncoded
    @POST("signup")
    Call<LoginModel> signupSocialService(@Field("name") String name,
                                   @Field("mobile_number") String mobile_number,
                                   @Field("password") String password,@Field("device_type") String device_type,
                                   @Field("device_token") String device_token,@Field("type") String type,
                                   @Field("facebook_id") String facebook_id,@Field("google_id") String google_id);


    @FormUrlEncoded
    @POST("verifyOTP")
    Call<LoginModel> verifyOTPService(@Field("mobile_number") String mobile_number,@Field("otp") String otp);

    @FormUrlEncoded
    @POST("resendOTP")
    Call<LoginModel> resendOTPService(@Field("mobile_number") String mobile_number);

    @GET("categories")
    Call<CategoryModel> categoriesService();

    @FormUrlEncoded
    @POST("login")
    Call<LoginModel> loginService(@Field("mobile_number") String mobile_number,@Field("password") String password,@Field("device_type") String device_type,@Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("locationupdate")
    Call<MLogin> locationupdateService(@Field("user_id") String user_id,@Field("latitude") String latitude,@Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("categorystores")
    Call<CategoryStoreModel> categorystoresService(@Field("limit") String limit,@Field("offset") String offset,@Field("type_of_store") String type_of_store, @Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("categorymerchants")
    Call<CategoryStoreModel> categorymerchantsService(@Field("limit") String limit,@Field("offset") String offset, @Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("profile")
    Call<LoginModel> profileService(@Field("user_id") String user_id);

    @Multipart
    @POST("upload_profilepic")
    Call<LoginModel> uploadProfilepicservice(@Part MultipartBody.Part imageFile,@Part("user_id") String userid);

    @FormUrlEncoded
    @POST("storeDetails")
    Call<StoreModel> storeDetailsService(@Field("user_id") String user_id,@Field("merchant_id") String merchant_id,@Field("latitude") String latitude,@Field("longitude") String longitude);

    @FormUrlEncoded
    @POST(ApiUrls.offers)
    Call<OffersListResponce> getOffers(@Field("limit") String limit,@Field("offset") String offset,@Field("latitude") String latitude,@Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("categoryoffers")
    Call<OffersListResponce> categoryOffers(@Field("limit") String limit,@Field("offset") String offset,@Field("latitude") String latitude,@Field("longitude") String longitude,@Field("category_id") String category_id);

    @FormUrlEncoded
    @POST("merchantOffers")
    Call<OffersListResponce> merchantOffers(@Field("merchant_id") String merchant_id);

    @GET(ApiUrls.feed)
    Call<FeedsListResponce> getFeeds();

    @FormUrlEncoded
    @POST(ApiUrls.changePassword)
    Call<LoginModel> changePassword(@Field("oldpassword") String oldpassword,@Field("newpassword") String newpassword,@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST(ApiUrls.forgotPassword)
    Call<LoginModel> forgetPassword(@Field("mobile_number") String mobile_number);

    @FormUrlEncoded
    @POST("likeMerchant")
    Call<LoginModel> likeMerchantService(@Field("user_id") String user_id,@Field("merchant_id") String merchant_id,@Field("flag") String flag);

    @FormUrlEncoded
    @POST("addMerchantReview")
    Call<LoginModel> addMerchantReviewService(@Field("user_id") String user_id,@Field("merchant_id") String merchant_id,@Field("review") String review);

    @FormUrlEncoded
    @POST("addMerchantRating")
    Call<LoginModel> addMerchantRatingService(@Field("user_id") String user_id,@Field("merchant_id") String merchant_id,@Field("rating") String rating);

    @FormUrlEncoded
    @POST("updateProfile")
    Call<LoginModel> updateProfileService(@Field("user_id") String user_id,@Field("name") String name,@Field("email") String email,@Field("gender") String gender);


    @FormUrlEncoded
    @POST("merchantTags")
    Call<TagsModel> tagsListService(@Field("user_id") String user_id,@Field("latitude") String latitude,@Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("requirements")
    Call<UserRequirementsListResponse> userRequirementsList(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("getMerchantResponse")
    Call<MerchantResponseModel> getMerchantResponse(@Field("user_id") String user_id,@Field("requirement_id") String requirement_id);

    @FormUrlEncoded
    @POST(ApiUrls.SOCIAL_LOGIN)
    Call<MLogin> socialLoginVerfy(@Field("type") String type, @Field("socialid") String socialid);

    @FormUrlEncoded
    @POST("products")
    Call<ProductListResponse> products(@Field("merchant_id") String merchant_id);

    @GET("getproducts")
    Call<ProductListResponse> getproducts();

    @FormUrlEncoded
    @POST("stores")
    Call<CategoryStoreModel> filteredStores(@Field("limit") String limit,@Field("offset") String offset,@Field("search_key") String search_key,@Field("latitude") String latitude,@Field("longitude") String longitude);
}

