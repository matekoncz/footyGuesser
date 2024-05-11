package com.example.footyguesser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmField;

    private TextView messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailField = findViewById(R.id.email_input);
        passwordField = findViewById(R.id.password_input);
        confirmField = findViewById(R.id.password_confirm);
        messages = findViewById(R.id.messages);

        messages.setText("");
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view){
        if(!emailField.getText().toString().isEmpty() && !passwordField.getText().toString().isEmpty()){
            if(passwordField.getText().toString().equals(confirmField.getText().toString())){
                saveUser(view);
            } else {
                Toast.makeText(this, "The passwords don't match", Toast.LENGTH_LONG).show();
                messages.setText(R.string.passwords_do_not_match);
            }
        } else {
            Toast.makeText(this, "Please fill all inputs", Toast.LENGTH_SHORT).show();
            messages.setText(R.string.missing_inputs);
        }
    }

    private void saveUser(View view){
        Task<AuthResult> mAuthResult = mAuth.createUserWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString());
        mAuthResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("signup","user created");
                    Toast.makeText(RegisterActivity.this,"User created successfully",Toast.LENGTH_LONG).show();
                    login(view);
                }  else {
                    Toast.makeText(RegisterActivity.this, "Failed to create user", Toast.LENGTH_LONG).show();
                    String message = task.getException().getMessage();
                    messages.setText(R.string.failed_to_sign_in+" : " +message);
                }
            }
        });
    }

    private void login(View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void home(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}