package com.example.test.radokov2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.radokov2.controller.LogInController;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    TextView GoToSign;
    Button btnLogin;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    LogInController controller;
    EditText signEmail;
    EditText signPass;

    String Email;
    String Pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        signEmail = findViewById(R.id.email);
        signPass = findViewById(R.id.password);
        controller = new LogInController(this);
        GoToSign = findViewById(R.id.go_sign);

        GoToSign.setOnClickListener(v -> {
            controller.goToSign();
        });

        //Initialise Firebase auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            controller.goToHome();
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            Email = signEmail.getText().toString();
            Pass = signPass.getText().toString();
            controller.authentificationDocteur(Email, Pass, progressBar);
        });
    }

}