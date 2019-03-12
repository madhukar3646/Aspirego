package com.m.aspirego.merchant_module.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MerchantStoreUpdateModel {

    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("storeinfo")
    @Expose
    private Storeinfo storeinfo;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Storeinfo getStoreinfo() {
        return storeinfo;
    }

    public void setStoreinfo(Storeinfo storeinfo) {
        this.storeinfo = storeinfo;
    }

    public class Storeinfo {

        @SerializedName("merchant_id")
        @Expose
        private String merchantId;
        @SerializedName("merchant_name")
        @Expose
        private String merchantName;
        @SerializedName("store_number")
        @Expose
        private String storeNumber;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("open_time")
        @Expose
        private String openTime;
        @SerializedName("close_time")
        @Expose
        private String closeTime;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("merchant_banner")
        @Expose
        private String merchantBanner;
        @SerializedName("door_number")
        @Expose
        private String door_number;

        @SerializedName("website")
        @Expose
        private String website;

        @SerializedName("type_of_store")
        @Expose
        private String type_of_store;

        @SerializedName("latitude")
        @Expose
        private String latitude;

        @SerializedName("longitude")
        @Expose
        private String longitude;

        @SerializedName("tags")
        @Expose

        private List<Tag> tags = null;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getType_of_store() {
            return type_of_store;
        }

        public void setType_of_store(String type_of_store) {
            this.type_of_store = type_of_store;
        }

        public String getDoor_number() {
            return door_number;
        }

        public void setDoor_number(String door_number) {
            this.door_number = door_number;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public String getMerchantName() {
            return merchantName;
        }

        public void setMerchantName(String merchantName) {
            this.merchantName = merchantName;
        }

        public String getStoreNumber() {
            return storeNumber;
        }

        public void setStoreNumber(String storeNumber) {
            this.storeNumber = storeNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getOpenTime() {
            return openTime;
        }

        public void setOpenTime(String openTime) {
            this.openTime = openTime;
        }

        public String getCloseTime() {
            return closeTime;
        }

        public void setCloseTime(String closeTime) {
            this.closeTime = closeTime;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getMerchantBanner() {
            return merchantBanner;
        }

        public void setMerchantBanner(String merchantBanner) {
            this.merchantBanner = merchantBanner;
        }

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }

    }

    public class Tag {

        @SerializedName("merchant_id")
        @Expose
        private String merchantId;
        @SerializedName("tag_id")
        @Expose
        private String tagId;
        @SerializedName("tag_name")
        @Expose
        private String tagName;

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public String getTagId() {
            return tagId;
        }

        public void setTagId(String tagId) {
            this.tagId = tagId;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

    }
}
