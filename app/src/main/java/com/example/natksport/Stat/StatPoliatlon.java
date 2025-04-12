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

public class StatPoliatlon extends AppCompatActivity {
    private TextView textViewName, textViewNumber,
            textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs, textViewAverageOneHundrMetrs,
            textViewBestFourHundrMetrs, textViewWorstFourHundrMetrs, textViewAverageFourHundrMetrs,
             textViewBestEightHundrMetrs, textViewWorstEightHundrMetrs, textViewAverageEightHundrMetrs,
            textViewBestFourHundrMetrsEsta, textViewWorstFourHundrMetrsEsta, textViewAverageFourHundrMetrsEsta,
            textViewBestThreeHundrMetrsEsta, textViewWorstThreeHundrMetrsEsta, textViewAverageThreeHundrMetrsEsta;
    private GraphView OneHundrMetrsGraph,FourHundrMetrsGraph,EightHundrMetrsGraph,FourHundrMetrsEstaGraph,ThreeHundrMetrsEstaGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_stat_poliatlon);
        String playerName = getIntent().getStringExtra("playerName");
        String playerNumber = getIntent().getStringExtra("playerNumber");
        String playerId = getIntent().getStringExtra("playerId");

        OneHundrMetrsGraph = findViewById(R.id.OneHundrMetrsGraph);
        FourHundrMetrsGraph = findViewById(R.id.FourHundrMetrsGraph);
        EightHundrMetrsGraph = findViewById(R.id.EightHundrMetrsGraph);
        FourHundrMetrsEstaGraph = findViewById(R.id.FourHundrMetrsEstaGraph);
        ThreeHundrMetrsEstaGraph = findViewById(R.id.ThreeHundrMetrsEstaGraph);

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
        textViewBestThreeHundrMetrsEsta = findViewById(R.id.textViewBestThreeHundrMetrsEsta);
        textViewWorstThreeHundrMetrsEsta = findViewById(R.id.textViewWorstThreeHundrMetrsEsta);
        textViewAverageThreeHundrMetrsEsta = findViewById(R.id.textViewAverageThreeHundrMetrsEsta);

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
                List<Integer> threeHundredRelayTimes = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog action = snapshot.getValue(ActionLog.class);
                    if (action != null && action.getPlayerId().equals(playerId)) {
                        int time = Integer.parseInt(action.getopisanie());
                        switch (action.getAction()) {
                            case "Забег на лыжах 3км":
                                hundredMeterTimes.add(time);
                                break;
                            case "Забег на лыжах 5км":
                                fourHundredMeterTimes.add(time);
                                break;
                            case "Подтягивание":
                                eightHundredMeterTimes.add(time);
                                break;
                            case "Отжимание":
                                fourHundredRelayTimes.add(time);
                                break;
                            case "Стрельба из пневматической винтовки":
                                threeHundredRelayTimes.add(time);
                                break;

                        }
                    }
                }

                updateStatisticsDisplay(hundredMeterTimes, fourHundredMeterTimes, eightHundredMeterTimes,
                        fourHundredRelayTimes, threeHundredRelayTimes);

                updateGraphs(hundredMeterTimes, fourHundredMeterTimes, eightHundredMeterTimes,
                        fourHundredRelayTimes, threeHundredRelayTimes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateStatisticsDisplay(List<Integer> hundred, List<Integer> fourHundred, List<Integer> eightHundred,
                                         List<Integer> fourHundredRelay, List<Integer> threeHundredRelay) {
        updateDisplayForDistance(textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs, textViewAverageOneHundrMetrs,  hundred);
        updateDisplayForDistance(textViewBestFourHundrMetrs, textViewWorstFourHundrMetrs, textViewAverageFourHundrMetrs,  fourHundred);

        updateDisplayForRepsOrScores(textViewBestEightHundrMetrs, textViewWorstEightHundrMetrs, textViewAverageEightHundrMetrs,  eightHundred);
        updateDisplayForRepsOrScores(textViewBestFourHundrMetrsEsta, textViewWorstFourHundrMetrsEsta, textViewAverageFourHundrMetrsEsta,  fourHundredRelay);

        updateDisplayForShootingScore(textViewBestThreeHundrMetrsEsta, textViewWorstThreeHundrMetrsEsta, textViewAverageThreeHundrMetrsEsta,  threeHundredRelay);

    }

    private void updateDisplayForDistance( TextView bestText, TextView worstText, TextView averageText, List<Integer> times) {
        if (times.isEmpty()) {
            bestText.setText("Лучшее время: нет данных");
            worstText.setText("Худшее время: нет данных");
            averageText.setText("Среднее время: нет данных");
            return;
        }

        int bestTime = Collections.min(times);
        int worstTime = Collections.max(times);
        double averageTime = times.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        bestText.setText("Лучшее время: " + bestTime + " секунд");
        worstText.setText("Худшее время: " + worstTime + " секунд");
        averageText.setText("Среднее время: " + String.format("%.2f", averageTime) + " секунд");
    }

    private void updateDisplayForRepsOrScores(TextView bestText, TextView worstText, TextView averageText, List<Integer> counts) {
        if (counts.isEmpty()) {
            bestText.setText("Лучшее значение: нет данных");
            worstText.setText("Худшее значение: нет данных");
            averageText.setText("Среднее значение: нет данных");
            return;
        }

        int bestCount = Collections.max(counts);
        int worstCount = Collections.min(counts);
        double averageCount = counts.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        bestText.setText("Лучшее значение: " + bestCount + " повторений");
        worstText.setText("Худшее значение: " + worstCount + " повторений");
        averageText.setText("Среднее значение: " + String.format("%.2f", averageCount) + " повторений");
    }

    private void updateDisplayForShootingScore(TextView bestText, TextView worstText, TextView averageText, List<Integer> scores) {
        if (scores.isEmpty()) {
            bestText.setText("Лучший результат: нет данных");
            worstText.setText("Худший результат: нет данных");
            averageText.setText("Средний результат: нет данных");
            return;
        }

        int bestScore = Collections.max(scores);
        int worstScore = Collections.min(scores);
        double averageScore = scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        bestText.setText("Лучший результат: " + bestScore + " очков");
        worstText.setText("Худший результат: " + worstScore + " очков");
        averageText.setText("Средний результат: " + String.format("%.2f", averageScore) + " очков");
    }

    private void updateGraphs(List<Integer> hundred, List<Integer> fourHundred, List<Integer> eightHundred,
                              List<Integer> fourHundredRelay, List<Integer> threeHundredRelay) {

        setupGraph(OneHundrMetrsGraph, hundred);
        setupGraph(FourHundrMetrsGraph, fourHundred);

        setupGraphForRepsOrScores(EightHundrMetrsGraph, eightHundred);
        setupGraphForRepsOrScores(FourHundrMetrsEstaGraph, fourHundredRelay);

        setupGraphForShootingScore(ThreeHundrMetrsEstaGraph, threeHundredRelay);

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
        graph.getGridLabelRenderer().setVerticalAxisTitle("Время в секундах");

        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
    }

    private void setupGraphForRepsOrScores(GraphView graph, List<Integer> counts) {
        if (counts.isEmpty()) {
            graph.removeAllSeries();
            return;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for (int i = 0; i < counts.size(); i++) {
            series.appendData(new DataPoint(i + 1, counts.get(i)), true, counts.size());
        }

        graph.addSeries(series);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(Collections.max(counts) + 10);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(counts.size());

        graph.getGridLabelRenderer().setNumHorizontalLabels(counts.size());
        graph.getGridLabelRenderer().setVerticalAxisTitle("Количество повторений");

        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
    }

    private void setupGraphForShootingScore(GraphView graph, List<Integer> scores) {
        if (scores.isEmpty()) {
            graph.removeAllSeries();
            return;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for (int i = 0; i < scores.size(); i++) {
            series.appendData(new DataPoint(i + 1, scores.get(i)), true, scores.size());
        }

        graph.addSeries(series);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(Collections.max(scores) + 10);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(scores.size());

        graph.getGridLabelRenderer().setNumHorizontalLabels(scores.size());
        graph.getGridLabelRenderer().setVerticalAxisTitle("Количество очков");

        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
    }

}
