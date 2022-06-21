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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.radokov2.adapter.PatientAdapter;
import com.example.test.radokov2.adapter.VisiteAdapter;
import com.example.test.radokov2.model.Patient;
import com.example.test.radokov2.model.Visite;
import com.example.test.radokov2.utils.WrapContentLinearLayoutManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity implements PatientAdapter.OnItemClickListener {

    Button btnAjoutConsultation;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference visiteRef = db.collection("Visite");
    private VisiteAdapter adapter;
    RecyclerView listVisite;
    String patientNom = "";
    String curentPatientId = "";

    TextView nom, prenom, date_naissance, adresse, tel, genre;
    CircleImageView profil;
    ImageView deletePatient, editPatient;
    Dialog dialog;
    Dialog dialogUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        listVisite = findViewById(R.id.historique_visite);
        nom = findViewById(R.id.pat_nom);
        prenom = findViewById(R.id.pat_prenom);
        date_naissance = findViewById(R.id.pat_date_naissance);
        adresse = findViewById(R.id.pat_adresse);
        tel = findViewById(R.id.pat_tel);
        genre = findViewById(R.id.pat_genre);
        profil = findViewById(R.id.Profil_patient);
        deletePatient = findViewById(R.id.patient_icon_delete);
        editPatient = findViewById(R.id.patient_icon_edit);
        dialog = new Dialog(this);
        dialogUpdate = new Dialog(this);

        Intent intent = getIntent();
        if (intent != null) {
            Patient pat = (Patient) intent.getSerializableExtra("Patient");
            if (pat != null) {
                nom.setText(pat.getNom());
                prenom.setText(pat.getPrenom());
                date_naissance.setText(pat.getDateNaissance());
                adresse.setText(pat.getAdresse());
                tel.setText(pat.getTel());
                genre.setText(pat.getGenre());
                patientNom = pat.getNom();
                curentPatientId = intent.getStringExtra("currentPatientId");
                Glide.with(profil.getContext())
                        .load(pat.getProfilUrl())
                        .placeholder(R.drawable.ic_patient_home)
                        .centerCrop()
                        .into(profil);
            }
        }

        btnAjoutConsultation = findViewById(R.id.ajout_consultation);
        btnAjoutConsultation.setOnClickListener(v -> {
            Intent toAjoutVisite = new Intent(this, AjoutVisiteActivity.class);
            if (intent != null) {
                String curentPatientId = intent.getStringExtra("currentPatientId");
                toAjoutVisite.putExtra("curentPatientId", curentPatientId);
            }
            toAjoutVisite.putExtra("curentPatientName", patientNom);
            startActivity(toAjoutVisite);
        });

        setUpRecyclerView();
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            Visite visite = documentSnapshot.toObject(Visite.class);
            String curentVisiteId = documentSnapshot.getId();
            Intent toVisiteDetails = new Intent(DetailActivity.this, DetailsVisiteActivity.class);
            toVisiteDetails.putExtra("Visite", visite);
            toVisiteDetails.putExtra("curentVisiteId", curentVisiteId);
            startActivity(toVisiteDetails);
        });

        deletePatient.setOnClickListener(v -> {
            deleteConfirm();
        });

        editPatient.setOnClickListener(v -> {
            updatePatient();
        });

    }

    private void updatePatient() {
        dialogUpdate.setContentView(R.layout.dialogue_update_pat);
        ProgressBar progressBar = dialogUpdate.findViewById(R.id.progress_update_pat);
        progressBar.setVisibility(View.INVISIBLE);
        dialogUpdate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialogUpdate.findViewById(R.id.close_update_pat);
        Button btnUpdate = dialogUpdate.findViewById(R.id.btn_update_pat);

        EditText nom, prenom, date, tel, adresse;
        nom = dialogUpdate.findViewById(R.id.nom_edit);
        prenom = dialogUpdate.findViewById(R.id.pnom_edit);
        date = dialogUpdate.findViewById(R.id.date_naissance_edit);
        tel = dialogUpdate.findViewById(R.id.tel_edit);
        adresse = dialogUpdate.findViewById(R.id.adresse_edit);

        nom.setText(this.nom.getText());
        prenom.setText(this.prenom.getText());
        date.setText(this.date_naissance.getText());
        adresse.setText(this.adresse.getText());
        tel.setText(this.tel.getText());

        imgClose.setOnClickListener(v -> {
            dialogUpdate.dismiss();
            Toast.makeText(DetailActivity.this, "Suppresion annuler", Toast.LENGTH_SHORT).show();
        });

        btnUpdate.setOnClickListener(v -> {
            String stNom = nom.getText().toString();
            String stPrenom = prenom.getText().toString();
            String stDate = date.getText().toString();
            String stAdresse = adresse.getText().toString();
            String stTel = tel.getText().toString();

            updateData(stNom, stPrenom, stDate, stAdresse, stTel, progressBar);
        });
        dialogUpdate.show();
    }

    private void updateData(String stNom, String stPrenom, String stDate, String stAdresse, String stTel, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Patient")
                .document(curentPatientId).update(
                "nom", stNom,
                "prenom", stPrenom,
                "adresse", stAdresse,
                "dateNaissance", stDate,
                "tel", stTel
        ).addOnSuccessListener(unused -> {
            updateVisiteName(stNom, progressBar);
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            dialogUpdate.dismiss();
            finish();
            Toast.makeText(DetailActivity.this, "Modification echoué", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateVisiteName(String stNom, ProgressBar progressBar) {
        db.collection("Visite").whereEqualTo("PatientId", curentPatientId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("Visite").document(document.getId())
                                        .update("NomPatient", stNom)
                                        .addOnSuccessListener(unused -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            dialogUpdate.dismiss();
                                            finish();
                                            Toast.makeText(DetailActivity.this, "Modification réussi", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            dialogUpdate.dismiss();
                                            finish();
                                            Toast.makeText(DetailActivity.this, "Modification echoué", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            dialogUpdate.dismiss();
                            finish();
                            Toast.makeText(DetailActivity.this, "Modification réussi", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                        finish();
                        Toast.makeText(DetailActivity.this, "Supprimer echoué", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteConfirm() {
        dialog.setContentView(R.layout.delete_pat_dialogue);
        ProgressBar progressBar = dialog.findViewById(R.id.progress_supp_pat);
        progressBar.setVisibility(View.INVISIBLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialog.findViewById(R.id.close_pat);
        Button btnSupprimer = dialog.findViewById(R.id.btn_suppr_pat);

        imgClose.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(DetailActivity.this, "Suppresion annuler", Toast.LENGTH_SHORT).show();
        });

        btnSupprimer.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            db.collection("Patient").document(curentPatientId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        deleteAllVisite(progressBar);
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                        finish();
                        Toast.makeText(DetailActivity.this, "Supprimer echoué", Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }

    private void deleteAllVisite(ProgressBar progressBar) {
        db.collection("Visite").whereEqualTo("PatientId", curentPatientId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("Visite").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        dialog.dismiss();
                                        finish();
                                        Toast.makeText(DetailActivity.this, "Suppression réussi", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        dialog.dismiss();
                                        finish();
                                        Toast.makeText(DetailActivity.this, "Supprimer echoué", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                        finish();
                        Toast.makeText(DetailActivity.this, "Supprimer echoué", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUpRecyclerView() {
        Query query = visiteRef.orderBy("Date", Query.Direction.DESCENDING).whereEqualTo("PatientId", curentPatientId);


        FirestoreRecyclerOptions<Visite> options = new FirestoreRecyclerOptions.Builder<Visite>()
                .setQuery(query, Visite.class)
                .build();
        adapter = new VisiteAdapter(options);
        listVisite.setHasFixedSize(true);
        listVisite.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listVisite.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

    }
}