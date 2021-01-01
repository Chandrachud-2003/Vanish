package com.chandrachud.vanish.items;

public class numberItemFirebase {

    private String uid;
    private String number;
    private String ccc;

    public numberItemFirebase(String uid, String number, String ccc) {
        this.uid = uid;
        this.number = number;
        this.ccc = ccc;
    }

    public numberItemFirebase() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCcc() {
        return ccc;
    }

    public void setCcc(String ccc) {
        this.ccc = ccc;
    }
}
