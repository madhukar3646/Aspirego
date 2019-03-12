package com.m.aspirego.user_module.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MerchantResponseModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("requirements ")
    @Expose
    private List<Requirements> requirements = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Requirements> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirements> requirements) {
        this.requirements = requirements;
    }

    public class Requirements {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("merchant_id")
        @Expose
        private String merchantId;
        @SerializedName("requirement_id")
        @Expose
        private String requirementId;
        @SerializedName("product_name")
        @Expose
        private String productName;
        @SerializedName("price")
        @Expose
        private String price;
        @SerializedName("offer_price")
        @Expose
        private String offerPrice;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("merchant_name")
        @Expose
        private String merchantName;
        @SerializedName("store_number")
        @Expose
        private String storeNumber;
        @SerializedName("delivery_option")
        @Expose
        private String deliveryOption;
        @SerializedName("product_type")
        @Expose
        private String productType;
        @SerializedName("email")
        @Expose
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public String getRequirementId() {
            return requirementId;
        }

        public void setRequirementId(String requirementId) {
            this.requirementId = requirementId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getOfferPrice() {
            return offerPrice;
        }

        public void setOfferPrice(String offerPrice) {
            this.offerPrice = offerPrice;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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

        public String getDeliveryOption() {
            return deliveryOption;
        }

        public void setDeliveryOption(String deliveryOption) {
            this.deliveryOption = deliveryOption;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

    }
}
