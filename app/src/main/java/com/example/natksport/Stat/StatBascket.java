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

public class StatBascket extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewNumber;
    private TextView textViewMatchesPlayed;
    private TextView textViewTwoPointsScored;
    private TextView textViewTwoPointsMissed;
    private TextView textViewBlocked;
    private TextView textViewThreePointsScored;
    private TextView textViewThreePointsMissed;
    private TextView textViewRebounds;
    private TextView textViewAssists;
    private Spinner matchSelector;

    private List<Match> matchList = new ArrayList<>();
    private Match selectedMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_stat_bascket);

        String playerName = getIntent().getStringExtra("playerName");
        String playerNumber = getIntent().getStringExtra("playerNumber");
        String playerId = getIntent().getStringExtra("playerId");

        textViewName = findViewById(R.id.textViewPlayerName);
        textViewNumber = findViewById(R.id.textViewPlayerNumber);
        textViewMatchesPlayed = findViewById(R.id.MatchesPlayed);
        textViewTwoPointsScored = findViewById(R.id.textViewTwoPointsScored);
        textViewTwoPointsMissed = findViewById(R.id.textViewTwoPointsMissed);
        textViewBlocked = findViewById(R.id.textViewBlocked);
        textViewThreePointsScored = findViewById(R.id.textViewThreePointsScored);
        textViewThreePointsMissed = findViewById(R.id.textViewThreePointsMissed);
        textViewRebounds = findViewById(R.id.textViewRebounds);
        textViewAssists = findViewById(R.id.textViewAssists);
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
        findViewById(R.id.graphFreeThrows).setVisibility(View.VISIBLE);
    }

    private void hideGraphs() {
        findViewById(R.id.graph).setVisibility(View.GONE);
        findViewById(R.id.graphThreePoints).setVisibility(View.GONE);
        findViewById(R.id.graphFreeThrows).setVisibility(View.GONE);
    }
    private void fetchMatches() {
        DatabaseReference databaseMatches = FirebaseDatabase.getInstance().getReference("matches");
        databaseMatches.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchList.clear();
                matchList.add(new Match("all", "Все Матчи",2));

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Match match = snapshot.getValue(Match.class);
                    if (match != null && match.getSportId() == 2) {
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
                int blocked = 0;
                int threePointsScored = 0;
                int threePointsMissed = 0;
                int defensiveRebounds = 0;
                int offensiveRebounds = 0;
                int turnovers = 0;
                int fouls = 0;
                int technicalFouls = 0;
                int assists = 0;
                int freeThrowsScored = 0;
                int freeThrowsMissed = 0;

                Set<String> uniqueMatches = new HashSet<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog actionLog = snapshot.getValue(ActionLog.class);
                    if (actionLog != null) {
                        String action = actionLog.getAction();
                        String matchId = actionLog.getMatchId();

                        uniqueMatches.add(matchId);

                        switch (action) {
                            case "Двухочковый бросок":
                                twoPointsScored++;
                                break;
                            case "Двухочковый бросок безрезультативный":
                                twoPointsMissed++;
                                break;
                            case "Заблокировал":
                                blocked++;
                                break;
                            case "Трехочковый бросок":
                                threePointsScored++;
                                break;
                            case "Трехочковый бросок безрезультативный":
                                threePointsMissed++;
                                break;
                            case "Подбор в защите":
                                defensiveRebounds++;
                                break;
                            case "Подбор в атаке":
                                offensiveRebounds++;
                                break;
                            case "Потеря мяча":
                                turnovers++;
                                break;
                            case "Фол":
                                fouls++;
                                break;
                            case "Технический фол":
                                technicalFouls++;
                                break;
                            case "Голевая передача":
                                assists++;
                                break;
                            case "Штрафной бросок":
                                freeThrowsScored++;
                                break;
                            case "Штрафной бросок безрезультативный":
                                freeThrowsMissed++;
                                break;
                        }
                    }
                }
                updateStats(twoPointsScored, twoPointsMissed, blocked, threePointsScored, threePointsMissed,
                        defensiveRebounds, offensiveRebounds, turnovers, fouls, technicalFouls, assists,
                        freeThrowsScored, freeThrowsMissed, uniqueMatches.size());
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
                int blocked = 0;
                int threePointsScored = 0;
                int threePointsMissed = 0;
                int defensiveRebounds = 0;
                int offensiveRebounds = 0;
                int turnovers = 0;
                int fouls = 0;
                int technicalFouls = 0;
                int assists = 0;
                int freeThrowsScored = 0;
                int freeThrowsMissed = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog actionLog = snapshot.getValue(ActionLog.class);
                    if (actionLog != null && actionLog.getMatchId().equals(matchId)) {
                        String action = actionLog.getAction();

                        switch (action) {
                            case "Двухочковый бросок":
                                twoPointsScored++;
                                break;
                            case "Двухочковый бросок безрезультативный":
                                twoPointsMissed++;
                                break;
                            case "Заблокировал":
                                blocked++;
                                break;
                            case "Трехочковый бросок":
                                threePointsScored++;
                                break;
                            case "Трехочковый бросок безрезультативный":
                                threePointsMissed++;
                                break;
                            case "Подбор в защите":
                                defensiveRebounds++;
                                break;
                            case "Подбор в атаке":
                                offensiveRebounds++;
                                break;
                            case "Потеря мяча":
                                turnovers++;
                                break;
                            case "Фол":
                                fouls++;
                                break;
                            case "Технический фол":
                                technicalFouls++;
                                break;
                            case "Голевая передача":
                                assists++;
                                break;
                            case "Штрафной бросок":
                                freeThrowsScored++;
                                break;
                            case "Штрафной бросок безрезультативный":
                                freeThrowsMissed++;
                                break;
                        }
                    }
                }

                updateStats(twoPointsScored, twoPointsMissed, blocked, threePointsScored, threePointsMissed,
                        defensiveRebounds, offensiveRebounds, turnovers, fouls, technicalFouls, assists,
                        freeThrowsScored, freeThrowsMissed,1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void updateStats(int twoPointsScored, int twoPointsMissed, int blocked, int threePointsScored,
                             int threePointsMissed, int defensiveRebounds, int offensiveRebounds,
                             int turnovers, int fouls, int technicalFouls,
                             int assists, int freeThrowsScored, int freeThrowsMissed, int matchesPlayed) {
        textViewMatchesPlayed.setText("Матчей сыграно: " + matchesPlayed);
        textViewTwoPointsScored.setText("Количество попаданий: " + twoPointsScored);
        textViewTwoPointsMissed.setText("Количество промахов: " + twoPointsMissed);
        textViewBlocked.setText("Заблокировал: " + blocked);
        textViewThreePointsScored.setText("Количество попаданий: " + threePointsScored);
        textViewThreePointsMissed.setText("Количество промахов: " + threePointsMissed);
        textViewRebounds.setText("Подборы в защите: " + defensiveRebounds + ", Подборы в атаке: " + offensiveRebounds);
        TextView textViewFreeThrowsScored = findViewById(R.id.textViewFreeThrowsScored);
        textViewFreeThrowsScored.setText("Количество попаданий: " + freeThrowsScored);
        TextView textViewFreeThrowsMissed = findViewById(R.id.textViewFreeThrowsMissed);
        textViewFreeThrowsMissed.setText("Количество промахов: " + freeThrowsMissed);
        TextView textViewTurnovers = findViewById(R.id.textViewTurnovers);
        textViewTurnovers.setText("Потеря мяча: " + turnovers);
        TextView textViewFouls = findViewById(R.id.textViewFouls);
        textViewFouls.setText("Фолы: " + fouls);
        TextView textViewTechnicalFouls = findViewById(R.id.textViewTechnicalFouls);
        textViewTechnicalFouls.setText("Технические фолы: " + technicalFouls);
        textViewAssists.setText("Асисты: " + assists);
        updatePercentages(twoPointsScored, twoPointsMissed, threePointsScored, threePointsMissed, freeThrowsScored, freeThrowsMissed);
    }

    private void updatePercentages(int twoPointsScored, int twoPointsMissed, int threePointsScored, int threePointsMissed, int freeThrowsScored, int freeThrowsMissed) {
        updateTwoPointsPercentage(twoPointsScored, twoPointsMissed);
        updateThreePointsPercentage(threePointsScored, threePointsMissed);

        int totalFreeThrows = freeThrowsScored + freeThrowsMissed;
        int freeThrowsPercentage = totalFreeThrows > 0 ? (freeThrowsScored * 100 / totalFreeThrows) : 0;
        TextView textViewFreeThrowsPercentageValue = findViewById(R.id.textViewFreeThrowsPercentageValue);
        textViewFreeThrowsPercentageValue.setText(freeThrowsPercentage + "%");

        View viewFreeThrowsPercentage = findViewById(R.id.viewFreeThrowsPercentage);
        int totalWidthFreeThrows = ((LinearLayout) findViewById(R.id.linearLayoutFreeThrowsPercentage)).getWidth();
        int filledWidthFreeThrows = (int) (totalWidthFreeThrows * (freeThrowsPercentage / 100.0));
        LinearLayout.LayoutParams paramsFreeThrows = new LinearLayout.LayoutParams(filledWidthFreeThrows, 10);
        viewFreeThrowsPercentage.setLayoutParams(paramsFreeThrows);
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
                List<Integer> freeThrowsPercentages = new ArrayList<>();

                LinkedHashMap<String, int[]> matchStats = new LinkedHashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog actionLog = snapshot.getValue(ActionLog.class);
                    if (actionLog != null) {
                        String matchId = actionLog.getMatchId();
                        matchStats.putIfAbsent(matchId, new int[6]);

                        int[] stats = matchStats.get(matchId);

                        switch (actionLog.getAction()) {
                            case "Двухочковый бросок":
                                stats[0]++;
                                break;
                            case "Двухочковый бросок безрезультативный":
                                stats[1]++;
                                break;
                            case "Трехочковый бросок":
                                stats[2]++;
                                break;
                            case "Трехочковый бросок безрезультативный":
                                stats[3]++;
                                break;
                            case "Штрафной бросок":
                                stats[4]++;
                                break;
                            case "Штрафной бросок безрезультативный":
                                stats[5]++;
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

                    int totalFreeThrows = stats[4] + stats[5];
                    int freeThrowsPercentage = totalFreeThrows > 0 ? (stats[4] * 100 / totalFreeThrows) : 0;
                    freeThrowsPercentages.add(freeThrowsPercentage);
                }

                updateGraphs(twoPointsPercentages, threePointsPercentages, freeThrowsPercentages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void updateGraphs(List<Integer> twoPointsPercentages, List<Integer> threePointsPercentages, List<Integer> freeThrowsPercentages) {
        addDataToTwoPointsGraph(twoPointsPercentages);
        addDataToThreePointsGraph(threePointsPercentages);
        addDataToFreeThrowsGraph(freeThrowsPercentages);
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
    }

    private void addDataToFreeThrowsGraph(List<Integer> freeThrowsPercentages) {
        GraphView graphFreeThrows = findViewById(R.id.graphFreeThrows);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        for (int matchIndex = 0; matchIndex < freeThrowsPercentages.size(); matchIndex++) {
            series.appendData(new DataPoint(matchIndex + 1, freeThrowsPercentages.get(matchIndex)), true, freeThrowsPercentages.size());
        }

        graphFreeThrows.addSeries(series);
        graphFreeThrows.getViewport().setMinY(0);
        graphFreeThrows.getViewport().setMaxY(100);
        graphFreeThrows.getGridLabelRenderer().setNumHorizontalLabels(freeThrowsPercentages.size());
        graphFreeThrows.getGridLabelRenderer().setNumVerticalLabels(11);

        graphFreeThrows.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graphFreeThrows.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graphFreeThrows.getGridLabelRenderer().setGridColor(Color.BLACK);
    }
}