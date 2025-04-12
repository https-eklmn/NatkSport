package com.example.natksport.Stat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natksport.ActionLog;
import com.example.natksport.Match;
import com.example.natksport.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatNastolTenis extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewNumber;
    private TextView textViewMatchesPlayed;
    private TextView textViewTwoPointsScored;
    private TextView textViewTwoPointsMissed;
    private TextView textViewThreePointsScored;
    private TextView textViewThreePointsMissed;
    private Spinner matchSelector;

    private List<Match> matchList = new ArrayList<>();
    private Match selectedMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_stat_nastol_tenis);

        String playerName = getIntent().getStringExtra("playerName");
        String playerNumber = getIntent().getStringExtra("playerNumber");
        String playerId = getIntent().getStringExtra("playerId");

        textViewName = findViewById(R.id.textViewPlayerName);
        textViewNumber = findViewById(R.id.textViewPlayerNumber);
        textViewMatchesPlayed = findViewById(R.id.MatchesPlayed);
        textViewTwoPointsScored = findViewById(R.id.textViewTwoPointsScored);
        textViewTwoPointsMissed = findViewById(R.id.textViewTwoPointsMissed);
        textViewThreePointsScored = findViewById(R.id.textViewThreePointsScored);
        textViewThreePointsMissed = findViewById(R.id.textViewThreePointsMissed);
        matchSelector = findViewById(R.id.spinnerMatchSelector);

        textViewName.setText(playerName);
        textViewNumber.setText(playerNumber);

        fetchMatches();

        matchSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMatch = matchList.get(position);

                if (position == 0) {
                    showGraphs();
                    fetchPlayerStats(playerId);
                } else {
                    hideGraphs();
                    fetchPlayerStatsForMatch(playerId, selectedMatch.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fetchPlayerStats(playerId);
        fetchPlayerStatsGraph(playerId);
    }
    private void showGraphs() {
        findViewById(R.id.graph).setVisibility(View.VISIBLE);
        findViewById(R.id.graphThreePoints).setVisibility(View.VISIBLE);

    }
    private void hideGraphs() {
        findViewById(R.id.graph).setVisibility(View.GONE);
        findViewById(R.id.graphThreePoints).setVisibility(View.GONE);

    }
    private void fetchMatches() {
        DatabaseReference databaseMatches = FirebaseDatabase.getInstance().getReference("matches");
        databaseMatches.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchList.clear();
                matchList.add(new Match("all", "Все Матчи", 5));

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Match match = snapshot.getValue(Match.class);
                    if (match != null && match.getSportId() == 5) {
                        matchList.add(match);
                    }
                }
                updateMatchSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateMatchSpinner() {
        List<String> matchDescriptions = new ArrayList<>();
        for (Match match : matchList) {
            matchDescriptions.add(match.getDescription());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matchDescriptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchSelector.setAdapter(adapter);
    }

    private void fetchPlayerStats(String playerId) {
        DatabaseReference databaseActions = FirebaseDatabase.getInstance().getReference("matchActions");
        databaseActions.orderByChild("playerId").equalTo(playerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int twoPointsScored = 0;
                int twoPointsMissed = 0;
                int threePointsScored = 0;
                int threePointsMissed = 0;

                Set<String> uniqueMatches = new HashSet<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog actionLog = snapshot.getValue(ActionLog.class);
                    if (actionLog != null) {
                        String action = actionLog.getAction();
                        String matchId = actionLog.getMatchId();

                        uniqueMatches.add(matchId);

                        switch (action) {
                            case "Подача":
                                twoPointsScored++;
                                break;
                            case "Подача безрезультативный":
                                twoPointsMissed++;
                                break;
                            case "Контратака":
                                threePointsScored++;
                                break;
                            case "Контратака безрезультативный":
                                threePointsMissed++;
                                break;
                        }
                    }
                }

                updateStats(twoPointsScored, twoPointsMissed,  threePointsScored, threePointsMissed, uniqueMatches.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void fetchPlayerStatsForMatch(String playerId, String matchId) {
        DatabaseReference databaseActions = FirebaseDatabase.getInstance().getReference("matchActions");
        databaseActions.orderByChild("playerId").equalTo(playerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int twoPointsScored = 0;
                int twoPointsMissed = 0;
                int threePointsScored = 0;
                int threePointsMissed = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog actionLog = snapshot.getValue(ActionLog.class);
                    if (actionLog != null && actionLog.getMatchId().equals(matchId)) {
                        String action = actionLog.getAction();

                        switch (action) {
                            case "Подача":
                                twoPointsScored++;
                                break;
                            case "Подача безрезультативный":
                                twoPointsMissed++;
                                break;
                            case "Контратака":
                                threePointsScored++;
                                break;
                            case "Контратака безрезультативный":
                                threePointsMissed++;
                                break;

                        }
                    }
                }

                updateStats(twoPointsScored, twoPointsMissed,  threePointsScored, threePointsMissed, 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateStats(int twoPointsScored, int twoPointsMissed,  int threePointsScored,
                             int threePointsMissed,  int matchesPlayed) {
        textViewMatchesPlayed.setText("Матчей сыграно: " + matchesPlayed);
        textViewTwoPointsScored.setText("Количество очков с подачи: " + twoPointsScored);
        textViewTwoPointsMissed.setText("Количество мячей улетевших в аут с подачи: " + twoPointsMissed);
        textViewThreePointsScored.setText("Количество забитых мячей с контратаки: " + threePointsScored);
        textViewThreePointsMissed.setText("Количество мячей улетевших в аут с контратаки: " + threePointsMissed);

        updatePercentages(twoPointsScored, twoPointsMissed, threePointsScored, threePointsMissed);
    }

    private void updatePercentages(int twoPointsScored, int twoPointsMissed, int threePointsScored, int threePointsMissed) {
        updateTwoPointsPercentage(twoPointsScored, twoPointsMissed);
        updateThreePointsPercentage(threePointsScored, threePointsMissed);
    }

    private void updateTwoPointsPercentage(int twoPointsScored, int twoPointsMissed) {
        int totalTwoPoints = twoPointsScored + twoPointsMissed;
        int twoPointsPercentage = totalTwoPoints > 0 ? (twoPointsScored * 100 / totalTwoPoints) : 0;
        TextView textViewTwoPointsPercentageValue = findViewById(R.id.textViewTwoPointsPercentageValue);
        textViewTwoPointsPercentageValue.setText(twoPointsPercentage + "%");


        View viewTwoPointsPercentage = findViewById(R.id.viewTwoPointsPercentage);
        int totalWidthTwoPoints = ((LinearLayout) findViewById(R.id.linearLayoutTwoPointsPercentage)).getWidth();
        int filledWidthTwoPoints = (int) (totalWidthTwoPoints * (twoPointsPercentage / 100.0));
        LinearLayout.LayoutParams paramsTwoPoints = new LinearLayout.LayoutParams(filledWidthTwoPoints, 10);
        viewTwoPointsPercentage.setLayoutParams(paramsTwoPoints);
    }

    private void updateThreePointsPercentage(int threePointsScored, int threePointsMissed) {
        int totalThreePoints = threePointsScored + threePointsMissed;
        int threePointsPercentage = totalThreePoints > 0 ? (threePointsScored * 100 / totalThreePoints) : 0;
        TextView textViewThreePointsPercentageValue = findViewById(R.id.textViewThreePointsPercentageValue);
        textViewThreePointsPercentageValue.setText(threePointsPercentage + "%");


        View viewThreePointsPercentage = findViewById(R.id.viewThreePointsPercentage);
        int totalWidthThreePoints = ((LinearLayout) findViewById(R.id.linearLayoutThreePointsPercentage)).getWidth();
        int filledWidthThreePoints = (int) (totalWidthThreePoints * (threePointsPercentage / 100.0));
        LinearLayout.LayoutParams paramsThreePoints = new LinearLayout.LayoutParams(filledWidthThreePoints, 10);
        viewThreePointsPercentage.setLayoutParams(paramsThreePoints);
    }

    private void fetchPlayerStatsGraph(String playerId) {
        DatabaseReference databaseActions = FirebaseDatabase.getInstance().getReference("matchActions");
        databaseActions.orderByChild("playerId").equalTo(playerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> twoPointsPercentages = new ArrayList<>();
                List<Integer> threePointsPercentages = new ArrayList<>();


                LinkedHashMap<String, int[]> matchStats = new LinkedHashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog actionLog = snapshot.getValue(ActionLog.class);
                    if (actionLog != null) {
                        String matchId = actionLog.getMatchId();
                        matchStats.putIfAbsent(matchId, new int[6]);

                        int[] stats = matchStats.get(matchId);

                        switch (actionLog.getAction()) {
                            case "Подача":
                                stats[0]++;
                                break;
                            case "Подача безрезультативный":
                                stats[1]++;
                                break;
                            case "Контратака":
                                stats[2]++;
                                break;
                            case "Контратака безрезультативный":
                                stats[3]++;
                                break;

                        }
                    }
                }


                for (Map.Entry<String, int[]> entry : matchStats.entrySet()) {
                    int[] stats = entry.getValue();
                    int totalTwoPoints = stats[0] + stats[1];
                    int twoPointsPercentage = totalTwoPoints > 0 ? (stats[0] * 100 / totalTwoPoints) : 0;
                    twoPointsPercentages.add(twoPointsPercentage);

                    int totalThreePoints = stats[2] + stats[3];
                    int threePointsPercentage = totalThreePoints > 0 ? (stats[2] * 100 / totalThreePoints) : 0;
                    threePointsPercentages.add(threePointsPercentage);
                }

                updateGraphs(twoPointsPercentages, threePointsPercentages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateGraphs(List<Integer> twoPointsPercentages, List<Integer> threePointsPercentages) {
        addDataToTwoPointsGraph(twoPointsPercentages);
        addDataToThreePointsGraph(threePointsPercentages);

    }

    private void addDataToTwoPointsGraph(List<Integer> twoPointsPercentages) {
        GraphView graph = findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        for (int matchIndex = 0; matchIndex < twoPointsPercentages.size(); matchIndex++) {
            series.appendData(new DataPoint(matchIndex + 1, twoPointsPercentages.get(matchIndex)), true, twoPointsPercentages.size());
        }

        graph.addSeries(series);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getGridLabelRenderer().setNumHorizontalLabels(twoPointsPercentages.size());
        graph.getGridLabelRenderer().setNumVerticalLabels(11);

        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);
    }

    private void addDataToThreePointsGraph(List<Integer> threePointsPercentages) {
        GraphView graphThreePoints = findViewById(R.id.graphThreePoints);
        LineGraphSeries<DataPoint> seriesThreePoints = new LineGraphSeries<>();

        for (int matchIndex = 0; matchIndex < threePointsPercentages.size(); matchIndex++) {
            seriesThreePoints.appendData(new DataPoint(matchIndex + 1, threePointsPercentages.get(matchIndex)), true, threePointsPercentages.size());
        }

        graphThreePoints.addSeries(seriesThreePoints);
        graphThreePoints.getViewport().setMinY(0);
        graphThreePoints.getViewport().setMaxY(100);
        graphThreePoints.getGridLabelRenderer().setNumHorizontalLabels(threePointsPercentages.size());
        graphThreePoints.getGridLabelRenderer().setNumVerticalLabels(11);

        graphThreePoints.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graphThreePoints.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graphThreePoints.getGridLabelRenderer().setGridColor(Color.BLACK);
        graphThreePoints.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);
    }

}