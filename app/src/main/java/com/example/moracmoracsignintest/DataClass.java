package com.example.moracmoracsignintest;

public class DataClass {

    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    //img
    private String dataImage;
    private String key;

    private String id;

    public  String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataLang() {
        return dataLang;
    }

    public String getDataImage() {
        return dataImage;
    }

    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage, String id) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.id = id;
    }

    public DataClass() {

    }
}
