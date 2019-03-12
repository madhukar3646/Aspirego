package com.m.aspirego.merchant_module.models;
import java.util.List;

public class MerchantPhotosResponseModel {

    private String result;
    private Integer status;
    private List<Merchantphoto> merchantphotos = null;

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

    public List<Merchantphoto> getMerchantphotos() {
        return merchantphotos;
    }

    public void setMerchantphotos(List<Merchantphoto> merchantphotos) {
        this.merchantphotos = merchantphotos;
    }
}
