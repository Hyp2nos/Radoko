package com.example.test.radokov2.controller;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.radokov2.adapter.ListePatientAdapter;
import com.example.test.radokov2.model.Patient;
import com.example.test.radokov2.utils.WrapContentLinearLayoutManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class ListePatientController {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference visiteRef = db.collection("Patient");
    RecyclerView listPatient;
    Context ctx;
    ListePatientAdapter adapter;

    public ListePatientController(RecyclerView listPatient, Context ctx, ListePatientAdapter adapter) {
        this.listPatient = listPatient;
        this.ctx = ctx;
        this.adapter = adapter;
    }

    public void AfficherListe() {
        Query query = visiteRef.orderBy("nom").whereEqualTo("docteurUid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class)
                .build();
        adapter = new ListePatientAdapter(options);
        listPatient.setHasFixedSize(true);
        listPatient.setLayoutManager(new WrapContentLinearLayoutManager(ctx,LinearLayoutManager.VERTICAL,false));
        listPatient.setAdapter(adapter);
    }

    public ListePatientAdapter getAdapter() {
        return adapter;
    }
}
