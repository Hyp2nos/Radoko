package com.example.test.radokov2.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.radokov2.HomeActivity;
import com.example.test.radokov2.LogInActivity;
import com.example.test.radokov2.R;
import com.example.test.radokov2.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Landing extends AppCompatActivity {

    Button goToLog;
    TextView goToSign;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        goToLog = findViewById(R.id.btn_landing_connect);
        goToSign = findViewById(R.id.btn_landing_insciption);

        //Initialise Firebase auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        goToLog.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            finish();
        });

        goToSign.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        });
    }
}