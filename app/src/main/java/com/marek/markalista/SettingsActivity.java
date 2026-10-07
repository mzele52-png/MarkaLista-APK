package com.marek.markalista;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private RadioGroup themeGroup, textSizeGroup;
    private Switch wallpaperSwitch, confirmClearSwitch, confirmDeleteSwitch, keepScreenOnSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("MarkaLista", MODE_PRIVATE);

        initTheme();
        initTextSize();
        initWallpaper();
        initToggles();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ustawienia");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initTheme() {
        themeGroup = findViewById(R.id.themeGroup);
        String currentTheme = prefs.getString("theme_mode", "system");

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
            prefs.edit().putString("theme_mode", theme).apply();
        });
    }

    private void initTextSize() {
        textSizeGroup = findViewById(R.id.textSizeGroup);
        String currentSize = prefs.getString("font_size", "normal");

        if ("small".equals(currentSize)) {
            textSizeGroup.check(R.id.textSmall);
        } else if ("large".equals(currentSize)) {
            textSizeGroup.check(R.id.textLarge);
        } else {
            textSizeGroup.check(R.id.textNormal);
        }

        textSizeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String size;
            if (checkedId == R.id.textSmall) {
                size = "small";
            } else if (checkedId == R.id.textLarge) {
                size = "large";
            } else {
                size = "normal";
            }
            prefs.edit().putString("font_size", size).apply();
        });
    }

    private void initWallpaper() {
        wallpaperSwitch = findViewById(R.id.wallpaperSwitch);
        if (wallpaperSwitch != null) {
            boolean showWallpaper = prefs.getBoolean("show_wallpaper", true);
            wallpaperSwitch.setChecked(showWallpaper);
            wallpaperSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("show_wallpaper", isChecked).apply()
            );
        }
    }

    private void initToggles() {
        confirmClearSwitch = findViewById(R.id.confirmClearSwitch);
        if (confirmClearSwitch != null) {
            boolean confirmClear = prefs.getBoolean("confirm_clear", true);
            confirmClearSwitch.setChecked(confirmClear);
            confirmClearSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("confirm_clear", isChecked).apply()
            );
        }

        confirmDeleteSwitch = findViewById(R.id.confirmDeleteSwitch);
        if (confirmDeleteSwitch != null) {
            boolean confirmDelete = prefs.getBoolean("confirm_delete", true);
            confirmDeleteSwitch.setChecked(confirmDelete);
            confirmDeleteSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("confirm_delete", isChecked).apply()
            );
        }

        keepScreenOnSwitch = findViewById(R.id.keepScreenOnSwitch);
        if (keepScreenOnSwitch != null) {
            boolean keepScreenOn = prefs.getBoolean("keep_screen_on", false);
            keepScreenOnSwitch.setChecked(keepScreenOn);
            keepScreenOnSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("keep_screen_on", isChecked).apply()
            );
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
