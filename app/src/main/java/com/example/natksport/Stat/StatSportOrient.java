package com.example.natksport.Stat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
public class StatSportOrient extends AppCompatActivity {

    private TextView textViewName, textViewNumber, textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs,
            textViewAverageOneHundrMetrs, textViewAverageFourHundrMetrs;
    private GraphView OneHundrMetrsGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_stat_sport_orient);

        String playerName = getIntent().getStringExtra("playerName");
        String playerNumber = getIntent().getStringExtra("playerNumber");
        String playerId = getIntent().getStringExtra("playerId");

        OneHundrMetrsGraph = findViewById(R.id.OneHundrMetrsGraph);

        textViewName = findViewById(R.id.textViewPlayerName);
        textViewNumber = findViewById(R.id.textViewPlayerNumber);
        textViewBestOneHundrMetrs = findViewById(R.id.textViewBestOneHundrMetrs);
        textViewWorstOneHundrMetrs = findViewById(R.id.textViewWorstOneHundrMetrs);
        textViewAverageOneHundrMetrs = findViewById(R.id.textViewAverageOneHundrMetrs);
        textViewAverageFourHundrMetrs = findViewById(R.id.textViewAverageFourHundrMetrs);

        textViewName.setText(playerName);
        textViewNumber.setText(playerNumber);


        loadRunStatistics(playerId);
    }
    private void loadRunStatistics(String playerId) {
        DatabaseReference databaseRuns = FirebaseDatabase.getInstance().getReference("matchActions");
        databaseRuns.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Double> hundredMeterTimes = new ArrayList<>();
                int fourHundredMeterTimes = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog action = snapshot.getValue(ActionLog.class);
                    if (action != null && action.getPlayerId().equals(playerId)) {
                        try {
                            switch (action.getAction()) {
                                case "Время прохождения":
                                    double time = Double.parseDouble(action.getopisanie());
                                    hundredMeterTimes.add(time);
                                    break;
                                case "Отметка пройдена не правильно":
                                    fourHundredMeterTimes++;
                                    break;
                            }
                        } catch (NumberFormatException e) {
                            Log.e("LoadRunStatistics", "Неверный формат времени: " + action.getopisanie());
                        }
                    }
                }

                updateStatisticsDisplay(hundredMeterTimes, fourHundredMeterTimes);
                updateGraphs(hundredMeterTimes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Ошибка загрузки данных: " + databaseError.getMessage());
            }
        });
    }
    private void updateStatisticsDisplay(List<Double> hundred, int fourHundred) {
        updateDisplayForDistance(textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs, textViewAverageOneHundrMetrs,  hundred);
        updateDisplay( textViewAverageFourHundrMetrs,  fourHundred);
    }
    private void updateDisplay(TextView averageText, int times) {
        if (times == 0) {

            averageText.setText("Все отметки пройдены правильно");
            return;
        }
        averageText.setText("Количество неверно пройденных отметок: " + times);
    }
    private void updateDisplayForDistance( TextView bestText, TextView worstText, TextView averageText, List<Double> times) {
        if (times.isEmpty()) {
            bestText.setText("Лучшее время: нет данных");
            worstText.setText("Худшее время: нет данных");
            averageText.setText("Среднее время: нет данных");
            return;
        }

        double bestTime = Collections.min(times);
        double worstTime = Collections.max(times);
        double averageTime = times.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        bestText.setText("Лучшее время: " + bestTime + " секунд");
        worstText.setText("Худшее время: " + worstTime + " секунд");
        averageText.setText("Среднее время: " + String.format("%.2f", averageTime) + " секунд");
    }
    private void updateGraphs(List<Double> hundred) {
        setupGraph(OneHundrMetrsGraph, hundred);
    }

    private void setupGraph(GraphView graph, List<Double> times) {
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
        graph.getGridLabelRenderer().setVerticalAxisTitle("Время в секундах");
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);

        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
    }

}