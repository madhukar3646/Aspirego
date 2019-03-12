
package com.m.aspirego.user_module.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Offer implements Parcelable{

    @SerializedName("merchant_name")
    @Expose
    private String merchantName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("offer_id")
    @Expose
    private String offerId;
    @SerializedName("offer_name")
    @Expose
    private String offerName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("offer_price")
    @Expose
    private String offerPrice;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("valid_from")
    @Expose
    private String validFrom;
    @SerializedName("valid_to")
    @Expose
    private String validTo;

    @SerializedName("mobile_number")
    @Expose
    private String mobile_number;

    @SerializedName("category_id")
    @Expose
    private String category_id;

    @SerializedName("category_name")
    @Expose
    private String category_name;

    protected Offer(Parcel in) {
        merchantName = in.readString();
        address = in.readString();
        offerId = in.readString();
        offerName = in.readString();
        image = in.readString();
        merchantId = in.readString();
        description = in.readString();
        price = in.readString();
        offerPrice = in.readString();
        discount = in.readString();
        validFrom = in.readString();
        validTo = in.readString();
        mobile_number=in.readString();
        category_id=in.readString();
        category_name=in.readString();
    }

    public static final Creator<Offer> CREATOR = new Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(merchantName);
        parcel.writeString(address);
        parcel.writeString(offerId);
        parcel.writeString(offerName);
        parcel.writeString(image);
        parcel.writeString(merchantId);
        parcel.writeString(description);
        parcel.writeString(price);
        parcel.writeString(offerPrice);
        parcel.writeString(discount);
        parcel.writeString(validFrom);
        parcel.writeString(validTo);
        parcel.writeString(mobile_number);
        parcel.writeString(category_id);
        parcel.writeString(category_name);
    }
}
