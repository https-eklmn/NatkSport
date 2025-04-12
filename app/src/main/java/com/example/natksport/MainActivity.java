package com.example.natksport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private DatabaseReference databaseReference;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.edit_username);
        passwordEditText = findViewById(R.id.edit_password);
        loginButton = findViewById(R.id.button_login);
        registerButton = findViewById(R.id.button_register);

        databaseReference = FirebaseDatabase.getInstance().getReference("User");





        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, null);
        if (savedUsername != null) {

            Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistration();
            }
        });
    }

    private void login() {
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        databaseReference.orderByChild("login").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("parol").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            Toast.makeText(MainActivity.this, "Вход выполнен", Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(KEY_USERNAME, username);
                            editor.apply();

                            Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                    Toast.makeText(MainActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Ошибка подключения к базе данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}
