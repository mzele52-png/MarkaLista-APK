package com.marek.markalista;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private RadioGroup themeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("MarkaLista", MODE_PRIVATE);
        themeGroup = findViewById(R.id.themeGroup);

        String currentTheme = prefs.getString("theme", "dark");
        if ("light".equals(currentTheme)) {
            themeGroup.check(R.id.themeLight);
        } else if ("dark".equals(currentTheme)) {
            themeGroup.check(R.id.themeDark);
        } else {
            themeGroup.check(R.id.themeSystem);
        }

        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String theme;
            if (checkedId == R.id.themeLight) {
                theme = "light";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.themeDark) {
                theme = "dark";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                theme = "system";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            prefs.edit().putString("theme", theme).apply();
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ustawienia");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
