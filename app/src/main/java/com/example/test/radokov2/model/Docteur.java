package com.example.test.radokov2.model;

public class Docteur {

    private String Name;
    private String Adresse;
    private String Email;
    private String Uid;


    public Docteur() {
    }

    public Docteur(String name, String adresse, String email, String uid) {
        Name = name;
        Adresse = adresse;
        Email = email;
        Uid = uid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAdresse() {
        return Adresse;
    }

    public void setAdresse(String adresse) {
        Adresse = adresse;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
