package com.m.aspirego.user_module.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TagsModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;

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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public class Tag {

        @SerializedName("merchant_id")
        @Expose
        private String merchantId;

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        @SerializedName("tag_id")

        @Expose
        private String tagId;
        @SerializedName("tag_name")
        @Expose
        private String tagName;
        @SerializedName("distance")
        @Expose
        private String distance;

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

        @Override
        public String toString() { return tagName; }
    }
}
