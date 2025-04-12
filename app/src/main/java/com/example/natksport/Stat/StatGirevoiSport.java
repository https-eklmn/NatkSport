package com.example.natksport.Stat;

import android.graphics.Color;
import android.os.Bundle;
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
import java.util.Collections;
import java.util.List;

public class StatGirevoiSport extends AppCompatActivity {

    private TextView textViewName, textViewNumber,  textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs, textViewAverageOneHundrMetrs,
            textViewBestFourHundrMetrs, textViewWorstFourHundrMetrs, textViewAverageFourHundrMetrs,
             textViewBestEightHundrMetrs, textViewWorstEightHundrMetrs, textViewAverageEightHundrMetrs,
            textViewBestFourHundrMetrsEsta, textViewWorstFourHundrMetrsEsta, textViewAverageFourHundrMetrsEsta;
    private GraphView OneHundrMetrsGraph,FourHundrMetrsGraph,EightHundrMetrsGraph,FourHundrMetrsEstaGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_stat_girevoi_sport);

        String playerName = getIntent().getStringExtra("playerName");
        String playerNumber = getIntent().getStringExtra("playerNumber");
        String playerId = getIntent().getStringExtra("playerId");

        OneHundrMetrsGraph = findViewById(R.id.OneHundrMetrsGraph);
        FourHundrMetrsGraph = findViewById(R.id.FourHundrMetrsGraph);
        EightHundrMetrsGraph = findViewById(R.id.EightHundrMetrsGraph);
        FourHundrMetrsEstaGraph = findViewById(R.id.FourHundrMetrsEstaGraph);


        textViewName = findViewById(R.id.textViewPlayerName);
        textViewNumber = findViewById(R.id.textViewPlayerNumber);

        textViewBestOneHundrMetrs = findViewById(R.id.textViewBestOneHundrMetrs);
        textViewWorstOneHundrMetrs = findViewById(R.id.textViewWorstOneHundrMetrs);
        textViewAverageOneHundrMetrs = findViewById(R.id.textViewAverageOneHundrMetrs);

        textViewBestFourHundrMetrs = findViewById(R.id.textViewBestFourHundrMetrs);
        textViewWorstFourHundrMetrs = findViewById(R.id.textViewWorstFourHundrMetrs);
        textViewAverageFourHundrMetrs = findViewById(R.id.textViewAverageFourHundrMetrs);

        textViewBestEightHundrMetrs = findViewById(R.id.textViewBestEightHundrMetrs);
        textViewWorstEightHundrMetrs = findViewById(R.id.textViewWorstEightHundrMetrs);
        textViewAverageEightHundrMetrs = findViewById(R.id.textViewAverageEightHundrMetrs);

        textViewBestFourHundrMetrsEsta = findViewById(R.id.textViewBestFourHundrMetrsEsta);
        textViewWorstFourHundrMetrsEsta = findViewById(R.id.textViewWorstFourHundrMetrsEsta);
        textViewAverageFourHundrMetrsEsta = findViewById(R.id.textViewAverageFourHundrMetrsEsta);

        textViewName.setText(playerName);
        textViewNumber.setText(playerNumber);

        loadRunStatistics(playerId);
    }

    private void loadRunStatistics(String playerId) {
        DatabaseReference databaseRuns = FirebaseDatabase.getInstance().getReference("matchActions");
        databaseRuns.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> hundredMeterTimes = new ArrayList<>();
                List<Integer> fourHundredMeterTimes = new ArrayList<>();
                List<Integer> eightHundredMeterTimes = new ArrayList<>();
                List<Integer> fourHundredRelayTimes = new ArrayList<>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog action = snapshot.getValue(ActionLog.class);
                    if (action != null && action.getPlayerId().equals(playerId)) {
                        int time = Integer.parseInt(action.getopisanie());
                        switch (action.getAction()) {
                            case "Рывок 24кг":
                                hundredMeterTimes.add(time);
                                break;
                            case "Рывок 32кг":
                                fourHundredMeterTimes.add(time);
                                break;
                            case "Толчок 24кг":
                                eightHundredMeterTimes.add(time);
                                break;
                            case "Толчок 32кг":
                                fourHundredRelayTimes.add(time);
                                break;

                        }
                    }
                }

                updateStatisticsDisplay(hundredMeterTimes, fourHundredMeterTimes, eightHundredMeterTimes,
                        fourHundredRelayTimes);

                updateGraphs(hundredMeterTimes, fourHundredMeterTimes, eightHundredMeterTimes,
                        fourHundredRelayTimes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateStatisticsDisplay(List<Integer> hundred, List<Integer> fourHundred, List<Integer> eightHundred,
                                         List<Integer> fourHundredRelay) {
        updateDisplayForDistance(textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs, textViewAverageOneHundrMetrs,  hundred);
        updateDisplayForDistance(textViewBestFourHundrMetrs, textViewWorstFourHundrMetrs, textViewAverageFourHundrMetrs,  fourHundred);
        updateDisplayForDistance(textViewBestEightHundrMetrs, textViewWorstEightHundrMetrs, textViewAverageEightHundrMetrs,  eightHundred);
        updateDisplayForDistance(textViewBestFourHundrMetrsEsta, textViewWorstFourHundrMetrsEsta, textViewAverageFourHundrMetrsEsta,  fourHundredRelay);

    }

    private void updateDisplayForDistance( TextView bestText, TextView worstText, TextView averageText, List<Integer> times) {
        if (times.isEmpty()) {
            bestText.setText("Наибольшее количество повторений: нет данных");
            worstText.setText("Наименьшее количество повторений: нет данных");
            averageText.setText("Средний показатель количества повторений: нет данных");
            return;
        }

        int bestTime = Collections.max(times);
        int worstTime = Collections.min(times);
        double averageTime = times.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        bestText.setText("Наибольшее количество повторений: " + bestTime + " раз");
        worstText.setText("Наименьшее количество повторений: " + worstTime + " раз");
        averageText.setText("Средний показатель количества повторений: " + String.format("%.2f", averageTime) + " раз");
    }
    private void updateGraphs(List<Integer> hundred, List<Integer> fourHundred, List<Integer> eightHundred,
                              List<Integer> fourHundredRelay) {

        setupGraph(OneHundrMetrsGraph, hundred);
        setupGraph(FourHundrMetrsGraph, fourHundred);
        setupGraph(EightHundrMetrsGraph, eightHundred);
        setupGraph(FourHundrMetrsEstaGraph, fourHundredRelay);
    }

    private void setupGraph(GraphView graph, List<Integer> times) {
        if (times.isEmpty()) {
            graph.removeAllSeries();
            return;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for (int i = 0; i < times.size(); i++) {
            series.appendData(new DataPoint(i + 1, times.get(i)), true, times.size());
        }

        graph.addSeries(series);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(Collections.max(times) + 10);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(times.size());

        graph.getGridLabelRenderer().setNumHorizontalLabels(times.size());
        graph.getGridLabelRenderer().setVerticalAxisTitle("Количество повторений");

        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
    }

}