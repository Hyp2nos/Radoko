package com.example.test.radokov2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.radokov2.adapter.ListePatientAdapter;
import com.example.test.radokov2.controller.ListePatientController;
import com.example.test.radokov2.model.Patient;

public class ListePatientActivity extends AppCompatActivity implements ListePatientAdapter.OnItemClickListener {


    private ListePatientAdapter adapter;
    RecyclerView listPatient;
    ListePatientController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_patient);
        listPatient = findViewById(R.id.liste_patient_recycle);
        controller = new ListePatientController(listPatient, this, adapter);
        controller.AfficherListe();
        controller.getAdapter().setOnItemClickListener((documentSnapshot, position) -> {
            Patient pat = documentSnapshot.toObject(Patient.class);
            String currentPatientId = documentSnapshot.getId();
            Intent toDetails = new Intent(ListePatientActivity.this, DetailActivity.class);
            toDetails.putExtra("Patient",pat);
            toDetails.putExtra("currentPatientId",currentPatientId);
            startActivity(toDetails);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        controller.getAdapter().startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        controller.getAdapter().stopListening();
    }

    @Override
    public void onItemClick(int position) {

    }
}