package com.example.test.radokov2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.radokov2.adapter.ListePatientAdapter;
import com.example.test.radokov2.model.Patient;
import com.example.test.radokov2.utils.WrapContentLinearLayoutManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RechercheActivity extends AppCompatActivity {

    ImageView search;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference visiteRef = db.collection("Patient");
    private ListePatientAdapter adapter;
    RecyclerView listRecherche;
    EditText txtSrch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        txtSrch = findViewById(R.id.chercher);
        listRecherche = findViewById(R.id.list_recherche);
        search = findViewById(R.id.img_srch);
        search.setOnClickListener(v -> {
            firebaseSearch(txtSrch.getText().toString());
            adapter.setOnItemClickListener((documentSnapshot, position) -> {
                Patient pat = documentSnapshot.toObject(Patient.class);
                String currentPatientId = documentSnapshot.getId();
                Intent toDetails = new Intent(RechercheActivity.this, DetailActivity.class);
                toDetails.putExtra("Patient",pat);
                toDetails.putExtra("currentPatientId",currentPatientId);
                startActivity(toDetails);
            });
        });

    }

    private void firebaseSearch(String str) {
        Query query = visiteRef.orderBy("nom").startAt(str).endAt(str + "~~");

        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class)
                .build();
        adapter = new ListePatientAdapter(options);
        listRecherche.setHasFixedSize(true);
        listRecherche.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listRecherche.setAdapter(adapter);
        adapter.startListening();

    }


}