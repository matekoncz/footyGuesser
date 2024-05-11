package com.example.footyguesser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class create_guess extends AppCompatActivity {


    private EditText home;
    private EditText away;

    private FirebaseFirestore mStore;

    private CollectionReference guessesreference;

    private FirebaseAuth mAuth;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_guess);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        guessesreference = mStore.collection("guesses");

        home = findViewById(R.id.home_goals);
        away = findViewById(R.id.away_goals);

        String homeHint = getIntent().getStringExtra("home") + " number of goals";
        home.setHint(homeHint);
        String awayhint = getIntent().getStringExtra("away") + " number of goals";
        away.setHint(awayhint);
        id = getIntent().getIntExtra("id",0);
    }

    public void cancel(View view){
        Intent intent = new Intent(this,MatchesActivity.class);
        startActivity(intent);
    }

    public void submitGuess(View view){
        if(home.getText().toString().isEmpty()||away.getText().toString().isEmpty()){
            Toast.makeText(this, "please fill all inputs", Toast.LENGTH_LONG).show();
        } else {
            if(Integer.parseInt(home.getText().toString())>99 || Integer.parseInt(away.getText().toString())>99){
                Toast.makeText(this,"Goal number can't exceed 99",Toast.LENGTH_LONG).show();
            } else {
                Guess guess = new Guess();
                guess.setAwayGoals(Integer.parseInt(away.getText().toString()));
                guess.setHomeGoals(Integer.parseInt(home.getText().toString()));
                guess.setMatchID(id);
                guess.setUser(mAuth.getCurrentUser().getEmail());
                String lastModified;
                Date currentdate = new Date();
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                lastModified = dateformat.format(currentdate);
                guess.setLastModified(lastModified);

                guessesreference.add(guess).addOnSuccessListener(documentReference -> {
                    Intent intent = new Intent(this,MatchesActivity.class);
                    startActivity(intent);
                });
            }
        }
    }
}