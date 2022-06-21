package com.example.test.radokov2.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.test.radokov2.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SingUpController {
    Context ctx;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public SingUpController(Context ctx) {
        this.ctx = ctx;
    }

    public void goToLogIn(){
        Intent log = new Intent(ctx, LogInActivity.class);
        ctx.startActivity(log);
        ((Activity)ctx).finish();
    }
}
