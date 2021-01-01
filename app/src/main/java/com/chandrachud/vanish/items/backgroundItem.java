package com.chandrachud.vanish.items;

import java.util.ArrayList;

public class backgroundItem {

    private String phone;
    private String uid;
    private String isNotify;
    private boolean isDelete;
    private boolean isReadd;
    private ArrayList<numberItemFirebase> mNumberItemFirebases;
    private ArrayList<numberItemFirebase> readdNumbersList;
    private boolean isPremium;
    private int profileNum;
    private String ccc;

    public backgroundItem()
    {

    }

    public backgroundItem(String phone, String uid, String isNotify, boolean isDelete, boolean isReadd, ArrayList<numberItemFirebase> numberItemFirebases, ArrayList<numberItemFirebase> readdNumbersList, boolean isPremium, int profileNum, String ccc) {
        this.phone = phone;
        this.uid = uid;
        this.isNotify = isNotify;
        this.isDelete = isDelete;
        this.isReadd = isReadd;
        mNumberItemFirebases = numberItemFirebases;
        this.readdNumbersList = readdNumbersList;
        this.isPremium = isPremium;
        this.profileNum = profileNum;
        this.ccc = ccc;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String isNotify() {
        return isNotify;
    }

    public void setNotify(String notify) {
        isNotify = notify;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isReadd() {
        return isReadd;
    }

    public void setReadd(boolean readd) {
        isReadd = readd;
    }

    public ArrayList<numberItemFirebase> getNumberItemFirebases() {
        return mNumberItemFirebases;
    }

    public void setNumberItemFirebases(ArrayList<numberItemFirebase> numberItemFirebases) {
        mNumberItemFirebases = numberItemFirebases;
    }

    public ArrayList<numberItemFirebase> getReaddNumbersList() {
        return readdNumbersList;
    }

    public void setReaddNumbersList(ArrayList<numberItemFirebase> readdNumbersList) {
        this.readdNumbersList = readdNumbersList;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getProfileNum() {
        return profileNum;
    }

    public void setProfileNum(int profileNum) {
        this.profileNum = profileNum;
    }

    public String getCcc() {
        return ccc;
    }

    public void setCcc(String ccc) {
        this.ccc = ccc;
    }
}
