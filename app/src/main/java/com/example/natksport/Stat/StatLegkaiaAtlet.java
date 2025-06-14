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
import java.util.Locale;

public class StatLegkaiaAtlet extends AppCompatActivity {
    private TextView textViewName, textViewNumber,
            textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs, textViewAverageOneHundrMetrs,
            textViewBestFourHundrMetrs, textViewWorstFourHundrMetrs, textViewAverageFourHundrMetrs,
            textViewBestEightHundrMetrs, textViewWorstEightHundrMetrs, textViewAverageEightHundrMetrs,
            textViewBestFourHundrMetrsEsta, textViewWorstFourHundrMetrsEsta, textViewAverageFourHundrMetrsEsta,
            textViewBestThreeHundrMetrsEsta, textViewWorstThreeHundrMetrsEsta, textViewAverageThreeHundrMetrsEsta,
            textViewBestTwoHundrMetrsEsta, textViewWorstTwoHundrMetrsEsta, textViewAverageTwoHundrMetrsEsta,
            textViewBestOneHundrMetrsEsta, textViewWorstOneHundrMetrsEsta, textViewAverageOneHundrMetrsEsta;
    private GraphView OneHundrMetrsGraph,FourHundrMetrsGraph,EightHundrMetrsGraph,FourHundrMetrsEstaGraph,ThreeHundrMetrsEstaGraph,
            TwoHundrMetrsEstaGraph,OneHundrMetrsEstaGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_stat_legkaia_atlet);
        String playerName = getIntent().getStringExtra("playerName");
        String playerNumber = getIntent().getStringExtra("playerNumber");
        String playerId = getIntent().getStringExtra("playerId");

        OneHundrMetrsGraph = findViewById(R.id.OneHundrMetrsGraph);
        FourHundrMetrsGraph = findViewById(R.id.FourHundrMetrsGraph);
        EightHundrMetrsGraph = findViewById(R.id.EightHundrMetrsGraph);
        FourHundrMetrsEstaGraph = findViewById(R.id.FourHundrMetrsEstaGraph);
        ThreeHundrMetrsEstaGraph = findViewById(R.id.ThreeHundrMetrsEstaGraph);
        TwoHundrMetrsEstaGraph = findViewById(R.id.TwoHundrMetrsEstaGraph);
        OneHundrMetrsEstaGraph = findViewById(R.id.OneHundrMetrsEstaGraph);

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

        textViewBestTwoHundrMetrsEsta = findViewById(R.id.textViewBestTwoHundrMetrsEsta);
        textViewWorstTwoHundrMetrsEsta = findViewById(R.id.textViewWorstTwoHundrMetrsEsta);
        textViewAverageTwoHundrMetrsEsta = findViewById(R.id.textViewAverageTwoHundrMetrsEsta);

        textViewBestOneHundrMetrsEsta = findViewById(R.id.textViewBestOneHundrMetrsEsta);
        textViewWorstOneHundrMetrsEsta = findViewById(R.id.textViewWorstOneHundrMetrsEsta);
        textViewAverageOneHundrMetrsEsta = findViewById(R.id.textViewAverageOneHundrMetrsEsta);

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
                List<Double> fourHundredMeterTimes = new ArrayList<>();
                List<Double> eightHundredMeterTimes = new ArrayList<>();
                List<Double> fourHundredRelayTimes = new ArrayList<>();
                List<Double> threeHundredRelayTimes = new ArrayList<>();
                List<Double> twoHundredRelayTimes = new ArrayList<>();
                List<Double> oneHundredRelayTimes = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog action = snapshot.getValue(ActionLog.class);
                    if (action != null && action.getPlayerId().equals(playerId)) {
                        try {
                            // Replace comma with dot for proper decimal parsing
                            String timeStr = action.getopisanie().replace(",", ".");
                            double time = Double.parseDouble(timeStr);

                            switch (action.getAction()) {
                                case "Забег на 100м":
                                    hundredMeterTimes.add(time);
                                    break;
                                case "Забег на 400м":
                                    fourHundredMeterTimes.add(time);
                                    break;
                                case "Забег на 800м":
                                    eightHundredMeterTimes.add(time);
                                    break;
                                case "Эстафета на 400м":
                                    fourHundredRelayTimes.add(time);
                                    break;
                                case "Эстафета на 300м":
                                    threeHundredRelayTimes.add(time);
                                    break;
                                case "Эстафета на 200м":
                                    twoHundredRelayTimes.add(time);
                                    break;
                                case "Эстафета на 100м":
                                    oneHundredRelayTimes.add(time);
                                    break;
                            }
                        } catch (NumberFormatException e) {
                            Log.e("StatLegkaiaAtlet", "Error parsing time value: " + action.getopisanie());
                        }
                    }
                }

                updateStatisticsDisplay(hundredMeterTimes, fourHundredMeterTimes, eightHundredMeterTimes,
                        fourHundredRelayTimes, threeHundredRelayTimes, twoHundredRelayTimes, oneHundredRelayTimes);

                updateGraphs(hundredMeterTimes, fourHundredMeterTimes, eightHundredMeterTimes,
                        fourHundredRelayTimes, threeHundredRelayTimes, twoHundredRelayTimes, oneHundredRelayTimes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateStatisticsDisplay(List<Double> hundred, List<Double> fourHundred, List<Double> eightHundred,
                                         List<Double> fourHundredRelay, List<Double> threeHundredRelay,
                                         List<Double> twoHundredRelay, List<Double> oneHundredRelay) {
        updateDisplayForDistance(textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs, textViewAverageOneHundrMetrs, hundred);
        updateDisplayForDistance(textViewBestFourHundrMetrs, textViewWorstFourHundrMetrs, textViewAverageFourHundrMetrs, fourHundred);
        updateDisplayForDistance(textViewBestEightHundrMetrs, textViewWorstEightHundrMetrs, textViewAverageEightHundrMetrs, eightHundred);
        updateDisplayForDistance(textViewBestFourHundrMetrsEsta, textViewWorstFourHundrMetrsEsta, textViewAverageFourHundrMetrsEsta, fourHundredRelay);
        updateDisplayForDistance(textViewBestThreeHundrMetrsEsta, textViewWorstThreeHundrMetrsEsta, textViewAverageThreeHundrMetrsEsta, threeHundredRelay);
        updateDisplayForDistance(textViewBestTwoHundrMetrsEsta, textViewWorstTwoHundrMetrsEsta, textViewAverageTwoHundrMetrsEsta, twoHundredRelay);
        updateDisplayForDistance(textViewBestOneHundrMetrsEsta, textViewWorstOneHundrMetrsEsta, textViewAverageOneHundrMetrsEsta, oneHundredRelay);
    }

    private void updateDisplayForDistance(TextView bestText, TextView worstText, TextView averageText, List<Double> times) {
        if (times.isEmpty()) {
            bestText.setText("Лучшее время: нет данных");
            worstText.setText("Худшее время: нет данных");
            averageText.setText("Среднее время: нет данных");
            return;
        }

        double bestTime = Collections.min(times);
        double worstTime = Collections.max(times);
        double averageTime = times.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        bestText.setText("Лучшее время: " + formatTime(bestTime));
        worstText.setText("Худшее время: " + formatTime(worstTime));
        averageText.setText("Среднее время: " + formatTime(averageTime));
    }
    private String formatTime(double time) {

        return String.format(Locale.getDefault(), "%.2f", time).replace(".", ",") + " секунд";
    }

    private void updateGraphs(List<Double> hundred, List<Double> fourHundred, List<Double> eightHundred,
                              List<Double> fourHundredRelay, List<Double> threeHundredRelay,
                              List<Double> twoHundredRelay, List<Double> oneHundredRelay) {

        setupGraph(OneHundrMetrsGraph, hundred);
        setupGraph(FourHundrMetrsGraph, fourHundred);
        setupGraph(EightHundrMetrsGraph, eightHundred);
        setupGraph(FourHundrMetrsEstaGraph, fourHundredRelay);
        setupGraph(ThreeHundrMetrsEstaGraph, threeHundredRelay);
        setupGraph(TwoHundrMetrsEstaGraph, twoHundredRelay);
        setupGraph(OneHundrMetrsEstaGraph, oneHundredRelay);
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