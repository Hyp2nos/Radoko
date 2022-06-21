package com.example.test.radokov2.model;

import java.io.Serializable;

public class Patient implements Serializable {
    private String nom;
    private String prenom;
    private String date;
    private String dateNaissance;
    private String adresse;
    private String tel;
    private String genre;
    private String docteurUid;
    private String dateAjout;
    private String profilUrl;

    public Patient() {
    }

    public Patient(String nom, String prenom, String date, String dateNaissance, String docteurUid, String dateAjout, String profilUrl) {
        this.nom = nom;
        this.prenom = prenom;
        this.date = date;
        this.dateNaissance = dateNaissance;

        this.docteurUid = docteurUid;
        this.dateAjout = dateAjout;
        this.profilUrl = profilUrl;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDocteurUid() {
        return docteurUid;
    }

    public void setDocteurUid(String docteurUid) {
        this.docteurUid = docteurUid;
    }

    public String getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(String dateAjout) {
        this.dateAjout = dateAjout;
    }

    public String getProfilUrl() {
        return profilUrl;
    }

    public void setProfilUrl(String profilUrl) {
        this.profilUrl = profilUrl;
    }
}
