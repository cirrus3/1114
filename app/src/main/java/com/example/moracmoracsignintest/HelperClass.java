package com.example.moracmoracsignintest;

public class HelperClass {

    String ceoname, phonenum, storename, htpay, category, id, dataImage;

    //id 추가 =>userID

    public String getId() {
        return id;
    }

    public void setId(String id) {this.id = id;}

    public String getCeoname() {
        return ceoname;
    }

    public void setCeoname(String ceoname) {
        this.ceoname = ceoname;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getHtpay() {
        return htpay;
    }

    public void setHtpay(String htpay) {
        this.htpay = htpay;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDataImage() {
        return dataImage;
    }

    //id 추가
    public HelperClass(String ceoname, String phonenum, String storename, String htpay, String category, String id, String dataImage) {
        this.ceoname = ceoname;
        this.phonenum = phonenum;
        this.storename = storename;
        this.htpay = htpay;
        this.category = category;
        this.id = id;
        this.dataImage=dataImage;
    }

    public HelperClass() {
    }
}
