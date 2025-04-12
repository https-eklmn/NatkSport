package com.example.natksport.Stat;

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
public class StatGTO extends AppCompatActivity {
    private TextView textViewName, textViewNumber,
            textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs, textViewAverageOneHundrMetrs,
            textViewBestFourHundrMetrs, textViewWorstFourHundrMetrs, textViewAverageFourHundrMetrs,
            textViewBestEightHundrMetrs, textViewWorstEightHundrMetrs, textViewAverageEightHundrMetrs,
            textViewBestFourHundrMetrsEsta, textViewWorstFourHundrMetrsEsta, textViewAverageFourHundrMetrsEsta,
            textViewBestThreeHundrMetrsEsta, textViewWorstThreeHundrMetrsEsta, textViewAverageThreeHundrMetrsEsta,
            textViewBestTwoHundrMetrsEsta, textViewWorstTwoHundrMetrsEsta, textViewAverageTwoHundrMetrsEsta,
            textViewBestOneHundrMetrsEsta, textViewWorstOneHundrMetrsEsta, textViewAverageOneHundrMetrsEsta,
            textViewBestJump,textViewWorstJump,textViewAverageJump,
            textViewBestMetanBall,textViewWorstMetanBall,textViewAverageMetanBall;
    private GraphView OneHundrMetrsGraph,FourHundrMetrsGraph,EightHundrMetrsGraph,FourHundrMetrsEstaGraph,ThreeHundrMetrsEstaGraph,
            TwoHundrMetrsEstaGraph,OneHundrMetrsEstaGraph,JumpGraph,MetanBallGraph;



    private List<Match> matchList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_stat_gto);

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
        JumpGraph = findViewById(R.id.JumpGraph);
        MetanBallGraph = findViewById(R.id.MetanBallGraph);

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
        textViewBestJump = findViewById(R.id.textViewBestJump);
        textViewWorstJump = findViewById(R.id.textViewWorstJump);
        textViewAverageJump = findViewById(R.id.textViewAverageJump);
        textViewBestMetanBall = findViewById(R.id.textViewBestMetanBall);
        textViewWorstMetanBall = findViewById(R.id.textViewWorstMetanBall);
        textViewAverageMetanBall = findViewById(R.id.textViewAverageMetanBall);
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
                List<Integer> twoHundredRelayTimes = new ArrayList<>();
                List<Integer> oneHundredRelayTimes = new ArrayList<>();
                List<Integer> Jump = new ArrayList<>();
                List<Integer> MetanBall = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActionLog action = snapshot.getValue(ActionLog.class);
                    if (action != null && action.getPlayerId().equals(playerId)) {
                        int time = Integer.parseInt(action.getopisanie());
                        switch (action.getAction()) {
                            case "Забег на 60м":
                                hundredMeterTimes.add(time);
                                break;
                            case "Забег на 2км":
                                fourHundredMeterTimes.add(time);
                                break;
                            case "Забег на 3км":
                                eightHundredMeterTimes.add(time);
                                break;
                            case "Заплыв на 50м":
                                fourHundredRelayTimes.add(time);
                                break;
                            case "Подтягивание":
                                threeHundredRelayTimes.add(time);
                                break;
                            case "Поднимание туловища лежа на спине":
                                twoHundredRelayTimes.add(time);
                                break;
                            case "Наклон вперед из положения стоя":
                                oneHundredRelayTimes.add(time);
                                break;
                            case "Прыжок в длину с места":
                                Jump.add(time);
                                break;
                            case "Метание мяча в цель":
                                MetanBall.add(time);
                                break;
                        }
                    }
                }

                updateStatisticsDisplay(hundredMeterTimes, fourHundredMeterTimes, eightHundredMeterTimes,
                        fourHundredRelayTimes, threeHundredRelayTimes, twoHundredRelayTimes, oneHundredRelayTimes,Jump,MetanBall);

                updateGraphs(hundredMeterTimes, fourHundredMeterTimes, eightHundredMeterTimes,
                        fourHundredRelayTimes, threeHundredRelayTimes, twoHundredRelayTimes, oneHundredRelayTimes,Jump,MetanBall);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateStatisticsDisplay(List<Integer> hundred, List<Integer> fourHundred, List<Integer> eightHundred,
                                         List<Integer> fourHundredRelay, List<Integer> threeHundredRelay,
                                         List<Integer> twoHundredRelay, List<Integer> oneHundredRelay,
                                         List<Integer> JumpRelay, List<Integer> MetanBallRelay) {
        updateDisplayForDistanceTime(textViewBestOneHundrMetrs, textViewWorstOneHundrMetrs, textViewAverageOneHundrMetrs,  hundred);
        updateDisplayForDistanceTime(textViewBestFourHundrMetrs, textViewWorstFourHundrMetrs, textViewAverageFourHundrMetrs,  fourHundred);
        updateDisplayForDistanceTime(textViewBestEightHundrMetrs, textViewWorstEightHundrMetrs, textViewAverageEightHundrMetrs,  eightHundred);
        updateDisplayForDistanceTime(textViewBestFourHundrMetrsEsta, textViewWorstFourHundrMetrsEsta, textViewAverageFourHundrMetrsEsta,  fourHundredRelay);
        updateDisplayForRepsOrScores(textViewBestThreeHundrMetrsEsta, textViewWorstThreeHundrMetrsEsta, textViewAverageThreeHundrMetrsEsta,  threeHundredRelay);
        updateDisplayForRepsOrScores(textViewBestTwoHundrMetrsEsta, textViewWorstTwoHundrMetrsEsta, textViewAverageTwoHundrMetrsEsta,  twoHundredRelay);
        updateDisplayForDistance(textViewBestOneHundrMetrsEsta, textViewWorstOneHundrMetrsEsta, textViewAverageOneHundrMetrsEsta,  oneHundredRelay);
        updateDisplayForDistance(textViewBestJump, textViewWorstJump, textViewAverageJump,  JumpRelay);
        updateDisplayForShootingScore(textViewBestMetanBall, textViewWorstMetanBall, textViewAverageMetanBall,  MetanBallRelay);
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
    private void updateDisplayForDistanceTime( TextView bestText, TextView worstText, TextView averageText, List<Integer> times) {
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
    private void updateDisplayForDistance( TextView bestText, TextView worstText, TextView averageText, List<Integer> times) {
        if (times.isEmpty()) {
            bestText.setText("Лучшее длина: нет данных");
            worstText.setText("Худшее длина: нет данных");
            averageText.setText("Средняя длина: нет данных");
            return;
        }

        int bestTime = Collections.min(times);
        int worstTime = Collections.max(times);
        double averageTime = times.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        bestText.setText("Лучшее длина: " + bestTime + " сантиметров");
        worstText.setText("Худшее длина: " + worstTime + " сантиметров");
        averageText.setText("Средняя длина: " + String.format("%.1f", averageTime) + " сантиметров");
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
    private void updateGraphs(List<Integer> hundred, List<Integer> fourHundred, List<Integer> eightHundred,
                              List<Integer> fourHundredRelay, List<Integer> threeHundredRelay,
                              List<Integer> twoHundredRelay, List<Integer> oneHundredRelay,
                              List<Integer> JumpRelay, List<Integer> MetanBallRelay) {

        setupGraph(OneHundrMetrsGraph, hundred);
        setupGraph(FourHundrMetrsGraph, fourHundred);
        setupGraph(EightHundrMetrsGraph, eightHundred);
        setupGraph(FourHundrMetrsEstaGraph, fourHundredRelay);
        setupGraph(ThreeHundrMetrsEstaGraph, threeHundredRelay);
        setupGraph(TwoHundrMetrsEstaGraph, twoHundredRelay);
        setupGraph(OneHundrMetrsEstaGraph, oneHundredRelay);
        setupGraph(JumpGraph, JumpRelay);
        setupGraph(MetanBallGraph, MetanBallRelay);
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
    }
}