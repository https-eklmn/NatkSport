package com.example.natksport;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameEditText, surnameEditText, patronymicEditText, emailEditText, usernameEditText, passwordEditText;
    private Button registerButton;
    private DatabaseReference  userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        nameEditText = findViewById(R.id.edit_name);
        surnameEditText = findViewById(R.id.edit_surname);
        patronymicEditText = findViewById(R.id.edit_patronymic);
        emailEditText = findViewById(R.id.edit_email);
        usernameEditText = findViewById(R.id.edit_username);
        passwordEditText = findViewById(R.id.edit_password);
        registerButton = findViewById(R.id.button_register);


        userRef = FirebaseDatabase.getInstance().getReference("User");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserToTable();
            }
        });
    }

    private void addUserToTable() {
        String name = nameEditText.getText().toString().trim();
        String surname = surnameEditText.getText().toString().trim();
        String patronymic = patronymicEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        checkIfUsernameExists(username, name, surname, patronymic,  email, password);
    }

    private void checkIfUsernameExists(String username, String name, String surname, String patronymic,  String email, String password) {
        userRef.orderByChild("login").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Toast.makeText(RegistrationActivity.this, "Логин уже занят. Пожалуйста, выберите другой.", Toast.LENGTH_SHORT).show();
                } else {

                    generateUniqueUserId(name, surname, patronymic,  email, username, password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegistrationActivity.this, "Ошибка доступа к базе данных.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateUniqueUserId(String name, String surname, String patronymic, String email, String username, String password) {
        int idUser = (int) (System.currentTimeMillis() / 1000);

        userRef.child(String.valueOf(idUser)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    generateUniqueUserId(name, surname, patronymic, email, username, password);
                } else {

                    addUserToDatabase(idUser, name, surname, patronymic,  email, username, password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegistrationActivity.this, "Ошибка доступа к базе данных.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToDatabase(int idUser, String name, String surname, String patronymic,  String email, String username, String password) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("iduser", idUser);
        userData.put("imia", name);
        userData.put("familia", surname);
        userData.put("otchestvo", patronymic);
        userData.put("pochta", email);
        userData.put("naimenovanieRoli", "Пользователь");
        userData.put("login", username);
        userData.put("parol", password);

        userRef.child(String.valueOf(idUser)).setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Ошибка регистрации, попробуйте еще раз!!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}