package com.example.test.radokov2.model;

import java.io.Serializable;

public class Visite implements Serializable {
    private String NomPatient;
    private String Docteur;
    private String Adresse;
    private String Date;
    private String Maladie;
    private String Traitement;
    private String PatientId;
    private String DocteurUid;

    public Visite() {
    }

    public Visite(String nomPatient, String docteur, String adresse, String date, String maladie, String traitement, String patientId, String docteurUid) {
        NomPatient = nomPatient;
        Docteur = docteur;
        Adresse = adresse;
        Date = date;
        Maladie = maladie;
        Traitement = traitement;
        PatientId = patientId;
        DocteurUid = docteurUid;
    }

    public String getNomPatient() {
        return NomPatient;
    }

    public void setNomPatient(String nomPatient) {
        NomPatient = nomPatient;
    }

    public String getDocteur() {
        return Docteur;
    }

    public void setDocteur(String docteur) {
        Docteur = docteur;
    }

    public String getAdresse() {
        return Adresse;
    }

    public void setAdresse(String adresse) {
        Adresse = adresse;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMaladie() {
        return Maladie;
    }

    public void setMaladie(String maladie) {
        Maladie = maladie;
    }

    public String getTraitement() {
        return Traitement;
    }

    public void setTraitement(String traitement) {
        Traitement = traitement;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }

    public String getDocteurUid() {
        return DocteurUid;
    }

    public void setDocteurUid(String docteurUid) {
        DocteurUid = docteurUid;
    }
}
