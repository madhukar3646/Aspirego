package com.m.aspirego.merchant_module.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MLogin {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("newuser")

    @Expose
    private String newuser;

    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("merchantinfo")
    @Expose
    private Merchantinfo merchantinfo;
    @SerializedName("user_details")
    @Expose
    private Merchantinfo user_details;

    public String getNewuser() {
        return newuser;
    }

    public void setNewuser(String newuser) {
        this.newuser = newuser;
    }

    public int getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Merchantinfo getMerchantinfo() {
        if(merchantinfo!=null)
         return merchantinfo;
        if (user_details!=null)
            return user_details;
        return merchantinfo;
    }

    public void setMerchantinfo(Merchantinfo merchantinfo) {
        this.merchantinfo = merchantinfo;
    }

}