package com.example.test.radokov2.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.test.radokov2.HomeActivity;
import com.example.test.radokov2.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogInController {

    Context ctx;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public LogInController(Context ctx) {
        this.ctx = ctx;
    }

    public void goToSign() {
        Intent sign = new Intent(ctx, SignUpActivity.class);
        ctx.startActivity(sign);
        ((Activity) ctx).finish();
    }

    public void goToHome() {
        Intent Home = new Intent(ctx, HomeActivity.class);
        ctx.startActivity(Home);
        ((Activity) ctx).finish();
    }

    public void authentificationDocteur(String email, String pass, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        if (email.isEmpty() || pass.isEmpty()) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(ctx, "Veuillez remplire tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ctx, "Connexion réussi", Toast.LENGTH_SHORT).show();
                goToHome();
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ctx, "Echec de la connexion", Toast.LENGTH_SHORT).show();
                return;
            }
        });

    }
}
