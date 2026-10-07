package com.marek.markalista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SPEECH_REQUEST_CODE = 100;
    private EditText productInput;
    private Button addButton;
    private RecyclerView productList;
    private View emptyStateContainer;
    private ProductAdapter adapter;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply theme and language BEFORE setContentView
        applySettings();
        applyLanguage();

        setContentView(R.layout.activity_main);
        applyFontSize();

        productInput = findViewById(R.id.productInput);
        addButton = findViewById(R.id.addButton);
        productList = findViewById(R.id.productList);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);

        products = new ArrayList<>();
        adapter = new ProductAdapter(products, this::updateEmptyState);

        productList.setLayoutManager(new LinearLayoutManager(this));
        productList.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
            new SwipeToDeleteCallback(products, () -> {
                adapter.notifyDataSetChanged();
                updateEmptyState();
            })
        );
        itemTouchHelper.attachToRecyclerView(productList);

        addButton.setOnClickListener(v -> addProduct());

        Button micButton = findViewById(R.id.micButton);
        if (micButton != null) {
            micButton.setOnClickListener(v -> startSpeechRecognition());
        }

        Button deleteAllButton = findViewById(R.id.deleteAllButton);
        if (deleteAllButton != null) {
            deleteAllButton.setOnClickListener(v -> {
                products.clear();
                adapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }

        updateEmptyState();

        // Header buttons
        findViewById(R.id.helpButton).setOnClickListener(v -> showHelp());
        findViewById(R.id.settingsButton).setOnClickListener(v -> showSettings());
        findViewById(R.id.bazaButton).setOnClickListener(v -> openBaza());
    }

    private void showHelp() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Pomoc");
        builder.setMessage("MarkaLista - Lista Zakupów\n\n" +
            "1. Dodaj produkt - wpisz nazwę lub użyj mikrofonu\n" +
            "2. Mikrofon - naciśnij aby dyktować produkty\n" +
            "3. Zaznacz - kliknij koło aby oznaczyć zakupione\n" +
            "4. Przesunięcie - przesuń w lewo aby usunąć\n" +
            "5. Usuń wszystko - wyczyść całą listę\n" +
            "6. Baza - podgląd wszystkich produktów");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void openBaza() {
        startActivity(new Intent(this, ProductsActivity.class));
    }

    private void addProduct() {
        String productName = productInput.getText().toString().trim();
        if (!productName.isEmpty()) {
            // iOS logic: check if product already exists
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).name.equalsIgnoreCase(productName)) {
                    // Product exists - merge quantity, mark as unchecked
                    Product existing = products.get(i);
                    existing.done = false;
                    adapter.notifyItemChanged(i);
                    productInput.setText("");
                    updateEmptyState();
                    return;
                }
            }

            // Product doesn't exist - add to END of list
            products.add(new Product(productName));
            adapter.notifyItemInserted(products.size() - 1);
            productInput.setText("");
            productList.scrollToPosition(products.size() - 1);
            updateEmptyState();

            // Add to product database (Baza)
            addToProductDatabase(productName);
        }
    }

    private void addToProductDatabase(String productName) {
        android.content.SharedPreferences prefs = getSharedPreferences("MarkaLista", MODE_PRIVATE);
        String dbJson = prefs.getString("products_db", "[]");
        try {
            org.json.JSONArray array = new org.json.JSONArray(dbJson);
            // Check if already exists
            for (int i = 0; i < array.length(); i++) {
                if (array.getString(i).equalsIgnoreCase(productName)) {
                    return; // Already in database
                }
            }
            array.put(productName);
            prefs.edit().putString("products_db", array.toString()).apply();
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateEmptyState() {
        if (products.isEmpty()) {
            productList.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);
        } else {
            productList.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);
        }
    }

    private void startSpeechRecognition() {
        Button micButton = findViewById(R.id.micButton);
        if (micButton != null) {
            micButton.setBackgroundResource(R.drawable.mic_button_listening);
        }

        SharedPreferences prefs = getSharedPreferences("MarkaLista", MODE_PRIVATE);
        String language = prefs.getString("language", "pl");
        String languageTag = getLanguageTag(language);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Powiedz nazwę produktu");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            if (micButton != null) {
                micButton.setBackgroundResource(R.drawable.mic_button_bg);
            }
            productInput.requestFocus();
        }
    }

    private String getLanguageTag(String language) {
        switch (language) {
            case "en": return "en-US";
            case "de": return "de-DE";
            case "fr": return "fr-FR";
            case "es": return "es-ES";
            case "it": return "it-IT";
            case "pt": return "pt-BR";
            case "ru": return "ru-RU";
            case "uk": return "uk-UA";
            case "cs": return "cs-CZ";
            case "sk": return "sk-SK";
            case "hu": return "hu-HU";
            case "ro": return "ro-RO";
            case "tr": return "tr-TR";
            case "el": return "el-GR";
            case "ja": return "ja-JP";
            default: return "pl-PL";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Button micButton = findViewById(R.id.micButton);
        if (micButton != null) {
            micButton.setBackgroundResource(R.drawable.mic_button_bg);
        }

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                productInput.setText(results.get(0));
                addProduct();
            }
        }
    }

    private void applySettings() {
        SharedPreferences prefs = getSharedPreferences("MarkaLista", MODE_PRIVATE);

        // Apply theme
        String themeMode = prefs.getString("theme_mode", "system");
        switch (themeMode) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        // Keep screen on flag
        if (prefs.getBoolean("keep_screen_on", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void applyLanguage() {
        SharedPreferences prefs = getSharedPreferences("MarkaLista", MODE_PRIVATE);
        String language = prefs.getString("language", "pl");

        java.util.Locale locale = new java.util.Locale(language);
        java.util.Locale.setDefault(locale);

        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getDisplayMetrics());
    }

    private void applyFontSize() {
        SharedPreferences prefs = getSharedPreferences("MarkaLista", MODE_PRIVATE);
        String fontSize = prefs.getString("font_size", "normal");
        float scale = 1.0f;

        switch (fontSize) {
            case "small":
                scale = 0.8f;
                break;
            case "large":
                scale = 1.2f;
                break;
            default:
                scale = 1.0f;
        }

        applyFontSizeToView(findViewById(android.R.id.content), scale);
    }

    private void applyFontSizeToView(View view, float scale) {
        if (view instanceof TextView && !(view instanceof Button)) {
            TextView tv = (TextView) view;
            float currentSize = tv.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
            tv.setTextSize(currentSize * scale);
        }

        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyFontSizeToView(vg.getChildAt(i), scale);
            }
        }
    }
}
