package com.m.aspirego.merchant_module.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Merchantphoto {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("image")
        @Expose
        private String image;

        private boolean tempFile;

    public boolean isTempFile() {
        return tempFile;
    }

    public void setTempFile(boolean tempFile) {
        this.tempFile = tempFile;
    }

    public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

    }