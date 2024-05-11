package com.example.footyguesser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class MatchesActivity extends AppCompatActivity {
    private FirebaseFirestore mStore;
    private CollectionReference matchesreference;

    private CollectionReference guessesreference;
    private RecyclerView recycler;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_matches);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        matchesreference = mStore.collection("matches");
        guessesreference = mStore.collection("guesses");
//        Match ujmeccs = new Match();
//        ujmeccs.setId(5);
//        ujmeccs.setHomeTeam("Ujszegedi TC");
//        ujmeccs.setAwayTeam("Tiszasziget SE");
//        ujmeccs.setFinished(false);
//        ujmeccs.setDate("2024-05-24");
//        matchesreference.add(ujmeccs);

        recycler = findViewById(R.id.match_recycler);
        recycler.setLayoutManager(new GridLayoutManager(
                this, 1));

        ArrayList<Match> matches = new ArrayList<>();

        matchesreference.orderBy("date").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                Match match = doc.toObject(Match.class);
                matches.add(match);
            }
            recycler.setAdapter(new MatchAdapter(this,matches));
        });



    }

    void editGuess(Match currentmatch){
        Task<QuerySnapshot> guess = guessesreference.whereEqualTo("matchID", currentmatch.getId()).whereEqualTo("user",mAuth.getCurrentUser().getEmail()).get();
        guess.addOnSuccessListener(queryDocumentSnapshots -> {
            if(queryDocumentSnapshots.isEmpty()){
                Intent intent = new Intent(this,create_guess.class);
                intent.putExtra("home",currentmatch.getHomeTeam());
                intent.putExtra("away",currentmatch.getAwayTeam());
                intent.putExtra("id",currentmatch.getId());
                startActivity(intent);
            } else {
                QueryDocumentSnapshot doc = queryDocumentSnapshots.iterator().next();
                Guess usersguess = doc.toObject(Guess.class);
                Intent intent = new Intent(this,UpdateGuess.class);
                intent.putExtra("home",currentmatch.getHomeTeam());
                intent.putExtra("away",currentmatch.getAwayTeam());
                intent.putExtra("id",currentmatch.getId());
                intent.putExtra("hgoal",usersguess.getHomeGoals());
                intent.putExtra("agoal",usersguess.getAwayGoals());
                intent.putExtra("docref",doc.getId());
                startActivity(intent);
            }
        });
    }
}