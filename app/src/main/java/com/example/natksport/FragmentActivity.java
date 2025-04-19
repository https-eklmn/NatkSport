package com.example.natksport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class FragmentActivity extends AppCompatActivity {

    private TextView textNews, textPlayers, textDescription, textuser;
    private ViewPager2 viewPager;
    private FragmentPagerAdapter pagerAdapter;
    private Button buttonLogout;

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USERNAME = "username";

    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        userRole = getIntent().getStringExtra("user_role");

        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new FragmentPagerAdapter(this, userRole);
        viewPager.setAdapter(pagerAdapter);

        textNews = findViewById(R.id.text_news);
        textPlayers = findViewById(R.id.text_players);
        textDescription = findViewById(R.id.trenera);
        textuser = findViewById(R.id.user);
        buttonLogout = findViewById(R.id.buttonLogout);

        if (!"Администратор".equals(userRole)) {
            textuser.setVisibility(View.GONE);
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateFooterColors(position);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        textNews.setOnClickListener(v -> viewPager.setCurrentItem(0));
        textPlayers.setOnClickListener(v -> viewPager.setCurrentItem(1));
        textDescription.setOnClickListener(v -> viewPager.setCurrentItem(2));
        if ("Администратор".equals(userRole)) {
            textuser.setOnClickListener(v -> viewPager.setCurrentItem(3));
        }

        updateFooterColors(0);
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.apply();

        Intent intent = new Intent(FragmentActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateFooterColors(int position) {
        if (position == 0) {
            textNews.setTextColor(Color.parseColor("#0073B7"));
            textPlayers.setTextColor(Color.BLACK);
            textDescription.setTextColor(Color.BLACK);
            textuser.setTextColor(Color.BLACK);
        } else if (position == 1) {
            textNews.setTextColor(Color.BLACK);
            textPlayers.setTextColor(Color.parseColor("#0073B7"));
            textDescription.setTextColor(Color.BLACK);
            textuser.setTextColor(Color.BLACK);
        } else if (position == 2) {
            textNews.setTextColor(Color.BLACK);
            textPlayers.setTextColor(Color.BLACK);
            textDescription.setTextColor(Color.parseColor("#0073B7"));
            textuser.setTextColor(Color.BLACK);
        } else if (position == 3) {
            textNews.setTextColor(Color.BLACK);
            textPlayers.setTextColor(Color.BLACK);
            textDescription.setTextColor(Color.BLACK);
            textuser.setTextColor(Color.parseColor("#0073B7"));
        }
    }
}
