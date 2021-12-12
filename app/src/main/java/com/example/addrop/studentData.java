package com.example.addrop;

import android.net.Uri;



public class studentData {
    private String name;
    private String phone;
    private String id;
    private String email;

    private String nameLbl;
    private String phoneLbl;
    private String idLbl;



    public studentData(String name,  String phone, String id, String email) {
        this.name = name;
        this.phone = phone;
        this.id = id;
        this.email = email;
    }



    public String getNameLbl() {
        return nameLbl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNameLbl(String nameLbl) {
        this.nameLbl = nameLbl;
    }

    public String getPhoneLbl() {
        return phoneLbl;
    }

    public void setPhoneLbl(String phoneLbl) {
        this.phoneLbl = phoneLbl;
    }

    public String getIdLbl() {
        return idLbl;
    }

    public void setIdLbl(String idLbl) {
        this.idLbl = idLbl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
