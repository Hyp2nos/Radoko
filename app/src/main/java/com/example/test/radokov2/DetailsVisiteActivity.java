package com.example.test.radokov2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.radokov2.model.Visite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DetailsVisiteActivity extends AppCompatActivity {

    TextView nom, docteur, adresse, date, maladie, traitement;
    ImageView deleteVisite, editVisite;
    Dialog dialog;
    Dialog dialogUpdate;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String currentID;
    String docteurUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_visite);
        nom = findViewById(R.id.visite_pat);
        docteur = findViewById(R.id.visite_fait_par);
        adresse = findViewById(R.id.visite_adresse);
        date = findViewById(R.id.visite_date);
        maladie = findViewById(R.id.visite_maladie);
        traitement = findViewById(R.id.visite_traitement);
        deleteVisite = findViewById(R.id.vsite_icon_delete);
        editVisite = findViewById(R.id.vsite_icon_edit);
        dialog = new Dialog(this);
        dialogUpdate = new Dialog(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();

        if (intent != null) {
            Visite curentVisite = (Visite) intent.getSerializableExtra("Visite");
            if (curentVisite != null) {
                nom.setText(curentVisite.getNomPatient());
                docteur.setText(curentVisite.getDocteur().split(" ")[0]);
                adresse.setText(curentVisite.getAdresse());
                date.setText(curentVisite.getDate());
                maladie.setText(curentVisite.getMaladie());
                traitement.setText(curentVisite.getTraitement());
                docteurUid = curentVisite.getDocteurUid();
                currentID = intent.getStringExtra("curentVisiteId");

            }
        }

        if (!Objects.requireNonNull(mAuth.getCurrentUser()).getUid().equals(docteurUid)) {
            deleteVisite.setVisibility(View.INVISIBLE);
            editVisite.setVisibility(View.INVISIBLE);
        }

        deleteVisite.setOnClickListener(v -> {
            dialogueDelete();
        });

        editVisite.setOnClickListener(v -> {
            updateVisite();
        });
    }

    private void updateVisite() {
        dialogUpdate.setContentView(R.layout.dialogue_update_visite);
        ProgressBar progressBar = dialogUpdate.findViewById(R.id.progress_update_visite);
        progressBar.setVisibility(View.INVISIBLE);
        dialogUpdate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialogUpdate.findViewById(R.id.close_update_visite);
        Button btnUpdate = dialogUpdate.findViewById(R.id.btn_update_visite);
        EditText maladie, traitement;
        maladie = dialogUpdate.findViewById(R.id.maladie_edit);
        traitement = dialogUpdate.findViewById(R.id.traitement_edit);

        maladie.setText(this.maladie.getText());
        traitement.setText(this.traitement.getText());

        imgClose.setOnClickListener(v -> {
            dialogUpdate.dismiss();
            Toast.makeText(DetailsVisiteActivity.this, "Modification annuler", Toast.LENGTH_SHORT).show();
        });

        btnUpdate.setOnClickListener(v -> {
            String stMaladie = maladie.getText().toString();
            String stTraitement = traitement.getText().toString();
            updateData(stMaladie, stTraitement, progressBar);
        });

        dialogUpdate.show();
    }

    private void updateData(String stMaladie, String stTraitement, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Visite")
                .document(currentID).update(
                "Maladie", stMaladie,
                "Traitement", stTraitement
        ).addOnSuccessListener(unused -> {
            progressBar.setVisibility(View.INVISIBLE);
            dialogUpdate.dismiss();
            finish();
            Toast.makeText(DetailsVisiteActivity.this, "Modification réussi", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            dialogUpdate.dismiss();
            finish();
            Toast.makeText(DetailsVisiteActivity.this, "Modification echoué", Toast.LENGTH_SHORT).show();
        });
    }

    private void dialogueDelete() {
        dialog.setContentView(R.layout.dialog_supp_visite);
        ProgressBar progressBar = dialog.findViewById(R.id.progress_supp_visite);
        progressBar.setVisibility(View.INVISIBLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialog.findViewById(R.id.close_visite);
        Button btnSupprimer = dialog.findViewById(R.id.btn_suppr_visite);

        imgClose.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(DetailsVisiteActivity.this, "Suppresion annuler", Toast.LENGTH_SHORT).show();
        });

        btnSupprimer.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            db.collection("Visite").document(currentID)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                        finish();
                        Toast.makeText(DetailsVisiteActivity.this, "Suppression réussi", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                        finish();
                        Toast.makeText(DetailsVisiteActivity.this, "Supprimer echoué", Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();

    }
}