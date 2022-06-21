package com.example.test.radokov2;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AjoutActivity extends AppCompatActivity {

    ImageView imageView;
    EditText nom, prenom, dateNaissance, adresse, tel;
    RadioGroup genre;
    Button btnAjouter;
    ProgressBar progressBar;
    String stGenre = "Femme";
    Uri filepath;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout);

        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.add_photo);
        imageView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 2);
        });

        nom = findViewById(R.id.ajout_nom);
        prenom = findViewById(R.id.ajout_prenom);
        dateNaissance = findViewById(R.id.date_naissance);
        adresse = findViewById(R.id.addresse);
        tel = findViewById(R.id.telephone);
        genre = findViewById(R.id.radioGenre);
        genre.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioHomme:
                    stGenre = "Homme";
                    break;
                case R.id.radioFemme:
                    stGenre = "Femme";
                    break;
                default:
                    stGenre = "Femme";
            }
        });
        progressBar = findViewById(R.id.progress_ajout_patient);
        progressBar.setVisibility(View.INVISIBLE);

        btnAjouter = findViewById(R.id.btn_valider);
        btnAjouter.setOnClickListener(v -> {
            uploadToStorage();
        });


    }

    private void AjoutPatient(Uri uri) {
        String stNom = nom.getText().toString();
        String stPrenom = prenom.getText().toString();
        String stDateNaissance = dateNaissance.getText().toString();
        String stAdresse = adresse.getText().toString();
        String stTel = tel.getText().toString();

        if (stNom.isEmpty() || stAdresse.isEmpty() || stDateNaissance.isEmpty() || stTel.isEmpty() || stPrenom.isEmpty()) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Veiillez remplire tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.FRENCH);
        String strDate = dateFormat.format(new Date());

        Map<String, Object> patient = new HashMap<>();
        patient.put("nom", stNom);
        patient.put("prenom", stPrenom);
        patient.put("dateNaissance", stDateNaissance);
        patient.put("adresse", stAdresse);
        patient.put("tel", stTel);
        patient.put("genre", stGenre);
        patient.put("docteurUid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        patient.put("dateAjout", strDate);
        patient.put("profilUrl", uri.toString());

        db.collection("Patient")
                .add(patient)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AjoutActivity.this, "Succés de l'ajout", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(AjoutActivity.this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AjoutActivity.this, "Echec de l'ajout", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                });

    }

    private void uploadToStorage() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (filepath != null) {
            StorageReference fileRef = storage.getReference().child(System.currentTimeMillis() + "." + getFileExtension(filepath));
            fileRef.putFile(filepath)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(this::AjoutPatient);
                    })
                    .addOnProgressListener(snapshot -> {

                    }).addOnFailureListener(e -> {
                Toast.makeText(AjoutActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();

            });
        } else {
            Toast.makeText(AjoutActivity.this, "Veuillez ajouter un image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri filepath) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(filepath));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            filepath = data.getData();
            imageView.setImageURI(filepath);
        }
    }
}