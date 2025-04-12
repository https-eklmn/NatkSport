package com.example.natksport;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natksport.Stat.StatArmresling;
import com.example.natksport.Stat.StatBascket;
import com.example.natksport.Stat.StatFutball;
import com.example.natksport.Stat.StatGTO;
import com.example.natksport.Stat.StatGirevoiSport;
import com.example.natksport.Stat.StatLegkaiaAtlet;
import com.example.natksport.Stat.StatLizhi;
import com.example.natksport.Stat.StatNastolTenis;
import com.example.natksport.Stat.StatPlavanie;
import com.example.natksport.Stat.StatPoliatlon;
import com.example.natksport.Stat.StatSportOrient;
import com.example.natksport.Stat.StatVoleball;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private List<Player> playerList;
    private Context context;
    private boolean matchStarted = false;
    private String matchId;

    public PlayerAdapter(List<Player> playerList, Context context) {
        this.playerList = playerList;
        this.context = context;
    }

    public void setMatchStarted(boolean matchStarted) {
        this.matchStarted = matchStarted;
        notifyDataSetChanged();
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void updatePlayerList(List<Player> players) {
        this.playerList = players;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.textViewPlayerName.setText(player.getName() + " " + player.getSurname());
        holder.textViewPlayerNumber.setText("Номер: " + player.getNumber());

        if (player.getPhotoUrl() != null && !player.getPhotoUrl().isEmpty()) {
            Bitmap bitmap = decodeBase64(player.getPhotoUrl());
            holder.imageViewPlayerPhoto.setImageBitmap(bitmap);
        } else {
            holder.imageViewPlayerPhoto.setImageResource(R.drawable.logo);
        }

        holder.buttonRecordAction.setVisibility(matchStarted ? View.VISIBLE : View.GONE);

        holder.buttonRecordAction.setOnClickListener(v -> showActionDialog(player));

        holder.buttonInfo.setOnClickListener(v -> {
            DatabaseReference databasePlayers = FirebaseDatabase.getInstance().getReference("players").child(player.getId());
            databasePlayers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Player fetchedPlayer = dataSnapshot.getValue(Player.class);
                        if (fetchedPlayer != null) {
                            int sportId = fetchedPlayer.getSportId();
                            Intent intent;
                            switch (sportId) {
                                case 1:
                                    intent = new Intent(context, StatFutball.class);
                                    break;
                                case 2:
                                    intent = new Intent(context, StatBascket.class);
                                    break;
                                case 3:
                                    intent = new Intent(context, StatVoleball.class);
                                    break;
                                case 4:
                                    intent = new Intent(context, StatLegkaiaAtlet.class);
                                    break;
                                case 5:
                                    intent = new Intent(context, StatNastolTenis.class);
                                    break;
                                case 6:
                                    intent = new Intent(context, StatLizhi.class);
                                    break;
                                case 7:
                                    intent = new Intent(context, StatPoliatlon.class);
                                    break;
                                case 8:
                                    intent = new Intent(context, StatGirevoiSport.class);
                                    break;
                                case 9:
                                    intent = new Intent(context, StatSportOrient.class);
                                    break;
                                case 10:
                                    intent = new Intent(context, StatArmresling.class);
                                    break;
                                case 11:
                                    intent = new Intent(context, StatPlavanie.class);
                                    break;
                                case 12:
                                    intent = new Intent(context, StatGTO.class);
                                    break;

                                default:
                                    Toast.makeText(context, "Неизвестный вид спорта", Toast.LENGTH_SHORT).show();
                                    return;
                            }

                            intent.putExtra("playerName", fetchedPlayer.getName() + " " + fetchedPlayer.getSurname());
                            intent.putExtra("playerNumber", fetchedPlayer.getNumber());
                            intent.putExtra("playerId", fetchedPlayer.getId());
                            context.startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, "Ошибка получения данных игрока", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    private void showActionDialog(Player player) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Выберите действие");

        String[] actions;


        int sportId = player.getSportId();


        switch (sportId) {
            case 1:
                actions = context.getResources().getStringArray(R.array.sob_futbol);
                break;
            case 2:
                actions = context.getResources().getStringArray(R.array.sob_bascet);
                break;
            case 3:
                actions = context.getResources().getStringArray(R.array.sob_voliebol);
                break;
            case 4:
                actions = context.getResources().getStringArray(R.array.sob_legkaia_atl);
                break;
            case 5:
                actions = context.getResources().getStringArray(R.array.sob_nastol_tenis);
                break;
            case 6:
                actions = context.getResources().getStringArray(R.array.sob_lizi);
                break;
            case 7:
                actions = context.getResources().getStringArray(R.array.sob_poliatlon);
                break;
            case 8:
                actions = context.getResources().getStringArray(R.array.sob_girevoi);
                break;
            case 9:
                actions = context.getResources().getStringArray(R.array.sob_sport_orient);
                break;
            case 10:
                actions = context.getResources().getStringArray(R.array.sob_armresling);
                break;
            case 11:
                actions = context.getResources().getStringArray(R.array.sob_plavanie);
                break;
            case 12:
                actions = context.getResources().getStringArray(R.array.sob_GTO);
                break;
            default:
                actions = new String[]{};
                break;
        }

        builder.setItems(actions, (dialog, which) -> {
            String action = actions[which];
            String opisanie = "Null";

            if (action.equals("Заплыв на 50м") || action.equals("Забег на 1км")
                    || action.equals("Эстафета на 100м")|| action.equals("Забег на 3км")
                    || action.equals("Забег на 5км")|| action.equals("Время прохождения")
                    || action.equals("Забег на 100м")|| action.equals("Забег на 400м")
                    || action.equals("Забег на 800м")|| action.equals("Эстафета на 400м")
                    || action.equals("Эстафета на 300м")|| action.equals("Эстафета на 200м")
                    || action.equals("Забег на 60м")|| action.equals("Забег на 2км")
                    || action.equals("Забег на лыжах 3км")|| action.equals("Забег на лыжах 5км")) {
                showTimeInputDialog(player, action);
            } else if (action.equals("Трехочковый бросок") || action.equals("Двухочковый бросок") ||
                    action.equals("Штрафной бросок") || action.equals("Подача") ||
                    action.equals("Нападающий удар") ||action.equals("Удар в створ ворот") ||
                    action.equals("Пенальти")||action.equals("Контратака")) {
                showHitMissDialog(player, action);
            } else if (action.equals("Поднимание туловища лежа на спине")|| action.equals("Отжимание")
                    || action.equals("Подтягивание")|| action.equals("Рывок 24кг")
                    || action.equals("Рывок 32кг")|| action.equals("Толчок 24кг")
                    || action.equals("Толчок 32кг")) {
                showKolvoPovtorDialog(player, action);
            } else if (action.equals("Наклон вперед из положения стоя")|| action.equals("Прыжок в длину с места")) {
                showRastoDialog(player, action);
            } else if (action.equals("Метание мяча в цель")|| action.equals("Стрельба из пневматической винтовки")) {
                showKolvoPointDialog(player, action);
            } else {
                recordAction(player, action,opisanie);
            }
        });

        builder.create().show();
    }
    private void showRastoDialog(Player player, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Введите количество очков");

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String opisanie = input.getText().toString();
            if (!opisanie.isEmpty()) {
                recordAction(player, action, opisanie);
            } else {
                Toast.makeText(context, "Введите корректное время", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
    private void showKolvoPointDialog(Player player, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Введите количество очков");

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String opisanie = input.getText().toString();
            if (!opisanie.isEmpty()) {
                recordAction(player, action, opisanie);
            } else {
                Toast.makeText(context, "Введите корректное время", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
    private void showKolvoPovtorDialog(Player player, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Введите количество повторений");

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String opisanie = input.getText().toString();
            if (!opisanie.isEmpty()) {
                recordAction(player, action, opisanie);
            } else {
                Toast.makeText(context, "Введите корректное время", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
    private void showTimeInputDialog(Player player, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Введите время в секундах");

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String opisanie = input.getText().toString();
            if (!opisanie.isEmpty()) {
                recordAction(player, action, opisanie);
            } else {
                Toast.makeText(context, "Введите корректное время", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
    private void showHitMissDialog(Player player, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Выберите результат");
        String opisanie = "Null";
        String[] results = {"Успешный", "Безрезультативный"};
        builder.setItems(results, (dialog, which) -> {
            String resultAction = results[which].equals("Успешный") ? action : action + " безрезультативный";
            recordAction(player, resultAction,opisanie);
        });

        builder.create().show();
    }

    private void recordAction(Player player, String action, String opisanie) {
        DatabaseReference databaseActions = FirebaseDatabase.getInstance().getReference("matchActions").push();
        Map<String, Object> actionData = new HashMap<>();
        actionData.put("playerId", player.getId());
        actionData.put("matchId", matchId);
        actionData.put("action", action);
        actionData.put("opisanie", opisanie);

        databaseActions.setValue(actionData)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Действие успешно записано", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Ошибка при записи действия", Toast.LENGTH_SHORT).show());
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPlayerName;
        TextView textViewPlayerNumber;
        ImageView imageViewPlayerPhoto;
        Button buttonRecordAction;
        ImageButton buttonInfo;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPlayerName = itemView.findViewById(R.id.textViewPlayerName);
            textViewPlayerNumber = itemView.findViewById(R.id.textViewPlayerNumber);
            imageViewPlayerPhoto = itemView.findViewById(R.id.imageViewPlayerPhoto);
            buttonRecordAction = itemView.findViewById(R.id.buttonRecordAction);
            buttonInfo = itemView.findViewById(R.id.buttonInfo);
        }
    }
}
