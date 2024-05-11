package com.example.footyguesser;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateGuess extends AppCompatActivity {

    private EditText home;
    private EditText away;

    private FirebaseFirestore mStore;

    private CollectionReference guessesreference;

    private FirebaseAuth mAuth;

    private int id;

    private String docref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_guess);
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

        int homeGoal = getIntent().getIntExtra("hgoal",0);
        int awayGoal = getIntent().getIntExtra("agoal",0);

        home.setText(Integer.toString(homeGoal));
        away.setText(Integer.toString(awayGoal));

        id = getIntent().getIntExtra("id",0);
        docref = getIntent().getStringExtra("docref");
    }

    public void updateGuess(View view) {
        if(home.getText().toString().isEmpty()||away.getText().toString().isEmpty()){
            Toast.makeText(this, "please fill all inputs", Toast.LENGTH_LONG).show();
        } else {
            if(Integer.parseInt(home.getText().toString())>99 || Integer.parseInt(away.getText().toString())>99){
                Toast.makeText(this,"Goal number can't exceed 99",Toast.LENGTH_LONG).show();
            } else {

                Task<Void> updatehome = guessesreference.document(docref).update("homeGoals", Integer.parseInt(home.getText().toString()));
                updatehome.addOnSuccessListener(result -> {
                    Task<Void> awayGoals = guessesreference.document(docref).update("awayGoals", Integer.parseInt(away.getText().toString()));
                    awayGoals.addOnSuccessListener(unused -> {
                        String lastModified;
                        Date currentdate = new Date();
                        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                        lastModified = dateformat.format(currentdate);
                        Task<Void> updatedate = guessesreference.document(docref).update("lastModified",lastModified);
                        updatedate.addOnSuccessListener(notused -> {
                            Intent intent = new Intent(this,MatchesActivity.class);
                            startActivity(intent);
                        });
                    });
                });
            }
            }
        }

    public void cancel(View view) {
        Intent intent = new Intent(this,MatchesActivity.class);
        startActivity(intent);
    }

    public void deleteGuess(View view) {
        new AlertDialog.Builder(UpdateGuess.this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        guessesreference.document(docref).delete().addOnSuccessListener(unused->{
                            Intent intent = new Intent(UpdateGuess.this,MatchesActivity.class);
                            startActivity(intent);
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}