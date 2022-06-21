package com.example.test.radokov2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.radokov2.adapter.PatientAdapter;
import com.example.test.radokov2.model.Patient;
import com.example.test.radokov2.utils.WrapContentLinearLayoutManager;
import com.example.test.radokov2.view.Landing;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements PatientAdapter.OnItemClickListener {

    LinearLayout btnPatient;
    LinearLayout btnSearch;
    LinearLayout btnAjout;
    ImageView Logout;

    RecyclerView list;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference visiteRef = db.collection("Patient");
    private PatientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        list = findViewById(R.id.home_list);


        btnPatient = findViewById(R.id.btn_patient);
        btnPatient.setOnClickListener(v -> {
            Intent Patient = new Intent(getApplicationContext(), ListePatientActivity.class);
            startActivity(Patient);
        });

        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(v -> {
            Intent Search = new Intent(getApplicationContext(), RechercheActivity.class);
            startActivity(Search);
        });

        btnAjout = findViewById(R.id.btn_ajout);
        btnAjout.setOnClickListener(v -> {
            Intent Ajout = new Intent(getApplicationContext(), AjoutActivity.class);
            startActivity(Ajout);
        });

        setUpRecyclerView();

        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            Patient pat = documentSnapshot.toObject(Patient.class);
            String currentPatientId = documentSnapshot.getId();
            Intent toDetails = new Intent(HomeActivity.this, DetailActivity.class);
            toDetails.putExtra("Patient", pat);
            toDetails.putExtra("currentPatientId", currentPatientId);
            startActivity(toDetails);
        });
        Logout = findViewById(R.id.logout);
        Logout.setOnClickListener(v -> {
            LogoutDocteur();
        });


    }

    private void setUpRecyclerView() {
        Query query = visiteRef.orderBy("dateAjout").limit(3).whereEqualTo("docteurUid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class)
                .build();
        adapter = new PatientAdapter(options);
        list.setHasFixedSize(true);
        list.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list.setAdapter(adapter);
    }

    private void LogoutDocteur() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Landing.class));
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        //adapter.notifyDataSetChanged();
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