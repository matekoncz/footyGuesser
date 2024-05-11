package com.example.footyguesser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateField;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder>{

    private ArrayList<Match> matches;
    private Context context;
    private int last_position = -1;

    private FirebaseFirestore mStore;
    private CollectionReference guessesreference;

    MatchAdapter(Context context, ArrayList<Match> matches) {
        this.matches = matches;
        this.context = context;
        this.mStore = FirebaseFirestore.getInstance();
        this.guessesreference = mStore.collection("guesses");
    }


    @NonNull
    @Override
    public MatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.match_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MatchAdapter.ViewHolder holder, int position) {
        Match currentMatch = matches.get(position);
        holder.bindTo(currentMatch);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // Member Variables for the TextViews
        private TextView opponents;
        private TextView homechance;
        private TextView drawchance;
        private TextView awaychance;
        private TextView date;

        private Button my_guess;

        ViewHolder(View matchView) {
            super(matchView);

            // Initialize the views.
            homechance = matchView.findViewById(R.id.home_chance);
            drawchance = matchView.findViewById(R.id.draw_chance);
            awaychance = matchView.findViewById(R.id.away_chance);
            date = matchView.findViewById(R.id.date);
            opponents = matchView.findViewById(R.id.teams);
            my_guess = matchView.findViewById(R.id.my_guess_button);
        }

        void bindTo(Match currentMatch){
            currentMatch.setHomeGoals(0);
            currentMatch.setAwayGoals(0);
            Guessgroup guessgroup = new Guessgroup();

            Task<AggregateQuerySnapshot> homeavgTask = guessesreference.whereEqualTo("matchID", currentMatch.getId()).aggregate(AggregateField.average("homeGoals")).get(AggregateSource.SERVER);


            homeavgTask.addOnCompleteListener(task -> {
                Double homeGoals = task.getResult().get(AggregateField.average("homeGoals"));
                if(homeGoals==null){
                    homeGoals= (double) 0;
                }
                currentMatch.setHomeGoals(homeGoals.intValue());
                Task<AggregateQuerySnapshot> awayavgTask = guessesreference.whereEqualTo("matchID", currentMatch.getId()).aggregate(AggregateField.average("awayGoals")).get(AggregateSource.SERVER);
                awayavgTask.addOnCompleteListener(newtask -> {
                    Double awayGoals = newtask.getResult().get(AggregateField.average("awayGoals"));
                    if(awayGoals==null){
                        awayGoals= (double) 0;
                    }
                    currentMatch.setAwayGoals(awayGoals.intValue());

                    guessesreference.whereEqualTo("matchID", currentMatch.getId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            Guess guess = doc.toObject(Guess.class);
                            if((guess.getHomeGoals()-guess.getAwayGoals())>0){
                                guessgroup.setWon(guessgroup.getWon()+1);
                            } else {
                                if((guess.getAwayGoals()-guess.getHomeGoals())>0){
                                    guessgroup.setLost(guessgroup.getLost()+1);
                                } else {
                                    guessgroup.setDrew(guessgroup.getDrew()+1);
                                }
                            }
                        }
                        int count = guessgroup.getDrew()+ guessgroup.getLost()+guessgroup.getWon();
                        float homepercentage = 0;
                        float awaypercentage = 0;
                        float drawpercentage = 0;

                        if(count!=0){
                            homepercentage = ((float) guessgroup.getWon() /count)*100;
                            awaypercentage = ((float) guessgroup.getLost() /count)*100;
                            drawpercentage = ((float) guessgroup.getDrew() /count)*100;
                        }



                        opponents.setText(currentMatch.getHomeTeam()+" - "+currentMatch.getAwayTeam());
                        homechance.setText(currentMatch.getHomeTeam()+" wins:\n"+homepercentage+"% from "+guessgroup.getWon()+" guesses. Goals guessed: "+currentMatch.getHomeGoals());
                        awaychance.setText(currentMatch.getAwayTeam()+" wins:\n"+awaypercentage+"% from "+guessgroup.getLost()+" guesses. Goals guessed "+currentMatch.getAwayGoals());
                        drawchance.setText("draw:\n"+drawpercentage+"% from "+guessgroup.getDrew()+" guesses");

                    });

                });
            });



            date.setText(currentMatch.getDate());
            my_guess.setOnClickListener(view->{
                ((MatchesActivity)context).editGuess(currentMatch);
            });
        }
    }

    class Guessgroup{
        private int won=0;
        private int lost=0;
        private int drew=0;

        public int getWon() {
            return won;
        }

        public void setWon(int won) {
            this.won = won;
        }

        public int getLost() {
            return lost;
        }

        public void setLost(int lost) {
            this.lost = lost;
        }

        public int getDrew() {
            return drew;
        }

        public void setDrew(int drew) {
            this.drew = drew;
        }
    }
}
