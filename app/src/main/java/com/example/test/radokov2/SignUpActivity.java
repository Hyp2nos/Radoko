package com.example.test.radokov2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.radokov2.controller.SingUpController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    TextView GoToLog;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    SingUpController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        progressBar = findViewById(R.id.progressBar_sign);
        progressBar.setVisibility(View.INVISIBLE);
        controller = new SingUpController(this);

        GoToLog = findViewById(R.id.go_log);
        GoToLog.setOnClickListener(v -> {
            controller.goToLogIn();
        });

        //Initialise Firebase auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(this,HomeActivity.class));
            finish();
        }

        Button btnRegister = findViewById(R.id.btn_inscrire);
        btnRegister.setOnClickListener(v -> {
            registerDocteur();
        });
    }

    private void registerDocteur() {
        progressBar.setVisibility(View.VISIBLE);

        EditText regName = findViewById(R.id.sign_name);
        EditText regEmail = findViewById(R.id.sign_email);
        EditText regAdresse = findViewById(R.id.sign_adress);
        EditText regPass = findViewById(R.id.sign_password);

        String Name = regName.getText().toString();
        String Email = regEmail.getText().toString();
        String Pass = regPass.getText().toString();
        String Adresse = regAdresse.getText().toString();

        if (Name.isEmpty() || Email.isEmpty() || Pass.isEmpty() || Adresse.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this,"Veiillez remplire tous les champs",Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> DocteurInfo = new HashMap<>();
                DocteurInfo.put("Adresse", Adresse);
                DocteurInfo.put("Email", Email);
                DocteurInfo.put("Name", Name);
                DocteurInfo.put("Uid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                db.collection("Docteur").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid().toString())
                        .set(DocteurInfo)
                        .addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUpActivity.this,"Succés d'inscription",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this,LogInActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this,"Echec lors de inscription",Toast.LENGTH_SHORT).show());
            }
            else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(SignUpActivity.this,"Echec de l'inscrption",Toast.LENGTH_SHORT).show();
            }
        });
    }
}