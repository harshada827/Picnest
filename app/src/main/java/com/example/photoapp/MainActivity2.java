package com.example.photoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity {

    private EditText username, password;
    private Button loginBtn , signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2); // your XML file

        // Firebase instance
        mAuth = FirebaseAuth.getInstance();

        // UI elements
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signup);

        // Login button click
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity2.this, MainActivity4.class));
            }
        });
    }

    private void loginUser() {
        String email = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            username.setError("Enter Email");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            password.setError("Enter Password");
            return;
        }

        // Firebase login
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity2.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            // Go to next screen after login
                            startActivity(new Intent(MainActivity2.this, MainActivity3.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity2.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}