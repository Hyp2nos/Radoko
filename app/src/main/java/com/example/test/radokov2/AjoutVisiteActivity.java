package com.example.test.radokov2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AjoutVisiteActivity extends AppCompatActivity {

    EditText maladie, traitement;
    Button btnAjoutVisite;
    ProgressBar progressBar;
    String curentPatientId = "";
    String docteurNom = "";
    String patientNom = "";
    String adresseDocteur = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_visite);

        maladie = findViewById(R.id.ajout_visite_maladie);
        traitement = findViewById(R.id.ajout_visite_traitement);
        progressBar = findViewById(R.id.ajout_visite_progress);
        progressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        if (intent != null) {
            curentPatientId = intent.getStringExtra("curentPatientId");
            patientNom = intent.getStringExtra("curentPatientName");
        }
        btnAjoutVisite = findViewById(R.id.btn_ajout_visite);
        btnAjoutVisite.setOnClickListener(v -> {
            AjoutVisite();
        });


    }

    private void AjoutVisite() {
        progressBar.setVisibility(View.VISIBLE);
        String stMaladie = maladie.getText().toString();
        String stTraitement = traitement.getText().toString();

        if (stMaladie.isEmpty() || stTraitement.isEmpty()) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Veuillez remplire tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh-mm", Locale.FRENCH);
        String strDate = dateFormat.format(new Date());

        Map<String, Object> visite = new HashMap<>();
        visite.put("NomPatient", patientNom);
        visite.put("Docteur", docteurNom);
        visite.put("Adresse", adresseDocteur);
        visite.put("Date", strDate);
        visite.put("Maladie", stMaladie);
        visite.put("Traitement", stTraitement);
        visite.put("PatientId", curentPatientId);
        visite.put("DocteurUid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        //Visite visite = new Visite("TestClass", "TestDoc", "Lot II F 33 BIS RE TEST", "22-05-2004", stMaladie, stTraitement, "dfghjkjjhgf");

        db.collection("Docteur").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot docteur = task.getResult();
                if (docteur.exists()) {
                    visite.replace("Docteur", docteur.getString("Name"));
                    visite.replace("Adresse", docteur.getString("Adresse"));
                    db.collection("Visite")
                            .add(visite)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(AjoutVisiteActivity.this, "Succés de l'ajout", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AjoutVisiteActivity.this, "Echec de l'ajout", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            });
                } else {
                    docteurNom = "Docteur";
                }
            } else {
                docteurNom = "Docteur";
            }
        });

    }
}