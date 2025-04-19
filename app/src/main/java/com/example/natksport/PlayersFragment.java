package com.example.natksport;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PlayersFragment extends Fragment {

    private Button buttonAddPlayer;
    private Button buttonStartMatch;
    private DatabaseReference databasePlayers;
    private DatabaseReference databaseMatches;
    private RecyclerView recyclerViewPlayers;
    private PlayerAdapter playerAdapter;
    private List<Player> playerList;
    private boolean matchStarted = false;
    private static final int IMAGE_PICK_CODE = 1000;
    private String selectedImage;
    private List<VidSport> sportList = new ArrayList<>();
    private ArrayAdapter<String> sportsAdapter;
    private Spinner spinnerSportFilter;
    private ArrayAdapter<String> sportsFilterAdapter;
    private String userRole;

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_players, container, false);

        buttonAddPlayer = view.findViewById(R.id.buttonAddPlayer);
        buttonStartMatch = view.findViewById(R.id.buttonStartMatch);
        recyclerViewPlayers = view.findViewById(R.id.recyclerViewPlayers);
        playerList = new ArrayList<>();
        playerAdapter = new PlayerAdapter(playerList, getActivity());
        spinnerSportFilter = view.findViewById(R.id.spinnerSportFilter);
        loadSportsForFilter();

        databasePlayers = FirebaseDatabase.getInstance().getReference("players");
        databaseMatches = FirebaseDatabase.getInstance().getReference("matches");

        recyclerViewPlayers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPlayers.setAdapter(playerAdapter);

        loadPlayers();
        updateButtonVisibility();

        buttonAddPlayer.setOnClickListener(v -> addPlayer());

        buttonStartMatch.setOnClickListener(v -> {
            if (!matchStarted) {
                startMatch();
            } else {
                endMatch();
            }
        });

        spinnerSportFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterPlayersBySport(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    private void updateButtonVisibility() {
        if ("Пользователь".equals(userRole)) {
            buttonAddPlayer.setVisibility(View.GONE);
            buttonStartMatch.setVisibility(View.GONE);
        } else {
            buttonAddPlayer.setVisibility(View.VISIBLE);
            buttonStartMatch.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                selectedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void loadSportsForFilter() {
        DatabaseReference databaseSports = FirebaseDatabase.getInstance().getReference("VidSporta");
        databaseSports.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> sportNames = new ArrayList<>();
                sportNames.add("Все виды спорта"); // Добавим опцию для всех игроков
                sportList.clear(); // Очистим список перед загрузкой

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    VidSport sport = snapshot.getValue(VidSport.class);
                    if (sport != null) {
                        sportList.add(sport); // Сохраняем вид спорта в sportList
                        sportNames.add(sport.getNaimenovanie());
                    }
                }

                sportsFilterAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, sportNames);
                sportsFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSportFilter.setAdapter(sportsFilterAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Ошибка при загрузке видов спорта", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterPlayersBySport(int selectedPosition) {
        String selectedSport = spinnerSportFilter.getItemAtPosition(selectedPosition).toString();
        List<Player> filteredList = new ArrayList<>();


        if (selectedSport.equals("Все виды спорта")) {
            playerAdapter.updatePlayerList(playerList);
            return;
        }


        int selectedSportId = -1;
        for (VidSport sport : sportList) {
            if (sport.getNaimenovanie().equals(selectedSport)) {
                selectedSportId = sport.getIDVidaSporta();
                break;
            }
        }


        for (Player player : playerList) {
            if (player.getSportId() == selectedSportId) {
                filteredList.add(player);
            }
        }


        playerAdapter.updatePlayerList(filteredList);
    }


    private void loadPlayers() {
        databasePlayers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Player player = snapshot.getValue(Player.class);
                    if (player != null) {
                        playerList.add(player);
                    }
                }
                playerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Ошибка при загрузке игроков", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPlayer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_player, null);
        builder.setView(dialogView);

        EditText editTextSurname = dialogView.findViewById(R.id.editTextSurname);
        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextPatronymic = dialogView.findViewById(R.id.editTextPatronymic);
        EditText editTextHeight = dialogView.findViewById(R.id.editTextHeight);
        EditText editTextNumber = dialogView.findViewById(R.id.editTextNumber);
        Spinner spinnerPosition = dialogView.findViewById(R.id.spinnerPosition);
        Spinner spinnerSports = dialogView.findViewById(R.id.spinnerSports);
        Button buttonSelectImage = dialogView.findViewById(R.id.buttonSelectImage);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);


        loadSportsAndPositions(spinnerSports);

        spinnerPosition.setEnabled(false);

        spinnerSports.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    loadPositionsForSelectedSport(sportList.get(position).getIDVidaSporta(), spinnerPosition);
                    spinnerPosition.setVisibility(View.VISIBLE);
                } else {
                    spinnerPosition.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerPosition.setVisibility(View.GONE);
            }
        });

        buttonSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), IMAGE_PICK_CODE);
        });

        AlertDialog dialog = builder.create();

        buttonSave.setOnClickListener(v -> {
            String surname = editTextSurname.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String patronymic = editTextPatronymic.getText().toString().trim();
            String height = editTextHeight.getText().toString().trim();
            String number = editTextNumber.getText().toString().trim();
            int selectedSportPosition = spinnerSports.getSelectedItemPosition();
            int sportId = sportList.get(selectedSportPosition).getIDVidaSporta();


            String position;
            if (selectedSportPosition == 0) {
                position = "0";
            } else if (spinnerPosition.getVisibility() == View.GONE) {
                position = "0";
            } else {
                position = spinnerPosition.getSelectedItem().toString();
            }

            if (surname.isEmpty() || name.isEmpty() || patronymic.isEmpty() || height.isEmpty() || number.isEmpty() || selectedSportPosition == 0) {
                Toast.makeText(getActivity(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            generateUniquePlayerId(surname, name, patronymic, height, number, position, sportId);
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void loadPositionsForSelectedSport(int sportId, Spinner spinnerPosition) {
        DatabaseReference databasePositions = FirebaseDatabase.getInstance().getReference("Position");
        databasePositions.orderByChild("IDSporta").equalTo(sportId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> positionNames = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Position position = snapshot.getValue(Position.class);
                            if (position != null) {
                                positionNames.add(position.getNaimenovanie());
                                Log.d("Position", "Loaded position: " + position.getNaimenovanie());
                            } else {
                                Log.d("Position", "Position is null");
                            }
                        }

                        if (!positionNames.isEmpty()) {
                            ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, positionNames);
                            positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerPosition.setAdapter(positionAdapter);
                            spinnerPosition.setEnabled(true);
                            spinnerPosition.setVisibility(View.VISIBLE);
                        } else {
                            spinnerPosition.setVisibility(View.GONE);
                            Log.d("Position", "No positions found for sportId: " + sportId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Ошибка при загрузке позиций", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void generateUniquePlayerId(String surname, String name, String patronymic, String height, String number, String position, int sportId) {
        String playerId = generateTenDigitId();


        databasePlayers.child(playerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    generateUniquePlayerId(surname, name, patronymic, height, number, position, sportId);
                } else {

                    Player player = new Player(playerId, surname, name, patronymic, height, number, selectedImage, position, sportId);
                    databasePlayers.child(playerId).setValue(player)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Игрок успешно добавлен", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Ошибка при добавлении игрока", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ошибка при проверке ID игрока", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String generateTenDigitId() {
        Random random = new Random();
        int tenDigitId = 1000000000 + random.nextInt(900000000);
        return String.valueOf(tenDigitId);
    }
    private void loadSportsAndPositions(Spinner spinnerSports) {
        DatabaseReference databaseSports = FirebaseDatabase.getInstance().getReference("VidSporta");
        databaseSports.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sportList.clear();
                List<String> sportNames = new ArrayList<>();
                VidSport defaultSport = new VidSport(0, "Выберите вид спорта");
                sportList.add(0, defaultSport);
                sportNames.add(0, defaultSport.getNaimenovanie());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    VidSport sport = snapshot.getValue(VidSport.class);
                    if (sport != null) {
                        sportList.add(sport);
                        sportNames.add(sport.getNaimenovanie());
                    }
                }

                sportsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, sportNames);
                sportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSports.setAdapter(sportsAdapter);

               
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Ошибка при загрузке видов спорта", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMatch() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Описание матча");

        final EditText input = new EditText(getActivity());
        builder.setView(input);

        Spinner sportSpinner = new Spinner(getActivity());

        loadSportsAndPositions(sportSpinner);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(sportSpinner);
        builder.setView(layout);

        builder.setPositiveButton("Начать", (dialog, which) -> {
            String matchDescription = input.getText().toString().trim();
            int selectedSportPosition = sportSpinner.getSelectedItemPosition();

            if (matchDescription.isEmpty()) {
                Toast.makeText(getActivity(), "Пожалуйста, введите описание матча", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSportPosition == 0) {
                Toast.makeText(getActivity(), "Пожалуйста, выберите вид спорта", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedSportId = sportList.get(selectedSportPosition).getIDVidaSporta();

            String matchId = generateTenDigitId();
            Match match = new Match(matchId, matchDescription, selectedSportId);

            databaseMatches.child(matchId).setValue(match)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Матч успешно начат", Toast.LENGTH_SHORT).show();
                        matchStarted = true;
                        playerAdapter.setMatchStarted(true);
                        playerAdapter.setMatchId(matchId);
                        spinnerSportFilter.setVisibility(View.GONE);
                        filterPlayersBySport(selectedSportId);

                        buttonStartMatch.setText("Матч завершился");
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Ошибка при начале матча", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
    private void endMatch() {
        matchStarted = false;
        playerAdapter.setMatchStarted(false);
        buttonStartMatch.setText("Начать игровой матч");
        Toast.makeText(getActivity(), "Матч завершен", Toast.LENGTH_SHORT).show();
        filterPlayersBySport(0);
        spinnerSportFilter.setVisibility(View.VISIBLE);
    }

}