package com.example.footyguesser;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
    private EditText password;
    private EditText email;
    private TextView message;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth=FirebaseAuth.getInstance();

        password=findViewById(R.id.password_input);
        email=findViewById(R.id.email_input);
        message=findViewById(R.id.login_message);
        message.setText("");
    }

    public void validateLogin(View view){
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            Toast.makeText(LoginActivity.this,"Please fill all inputs",Toast.LENGTH_LONG).show();
            message.setText(R.string.missing_inputs);
        } else{
            signInUser(view);
        }
    }

    private void signInUser(View view){
        Task<AuthResult> mAuthResult = mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString());
        mAuthResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"successful login",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this,MatchesActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this,"password and email don't match",Toast.LENGTH_LONG).show();
                    message.setText(R.string.failed_to_login);
                }
            }
        });
    }

    public void home(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}