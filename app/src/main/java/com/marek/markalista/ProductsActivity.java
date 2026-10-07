package com.marek.markalista;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private RecyclerView productList;
    private ProductsAdapter adapter;
    private List<String> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        productList = findViewById(R.id.productList);
        products = new ArrayList<>();

        // Load products from database or use defaults
        android.content.SharedPreferences prefs = getSharedPreferences("MarkaLista", MODE_PRIVATE);
        String dbJson = prefs.getString("products_db", null);

        if (dbJson == null) {
            // First time - use default products
            products.add("Chleb");
            products.add("Mleko");
            products.add("Jajka");
            products.add("Ser");
            products.add("Masło");
            products.add("Marchewka");
            products.add("Pomidor");
            products.add("Sałata");
            products.add("Cebula");
            products.add("Czosnek");
            products.add("Ziemniaki");
            products.add("Ryż");
            products.add("Makaron");
            products.add("Olej");
            products.add("Sól");
            products.add("Pieprz");
            products.add("Cukier");
            products.add("Mąka");
            products.add("Mleko skondensowane");
            products.add("Jogurt");
            products.add("Śmietana");
            products.add("Kielbasa");
            products.add("Szynka");
            products.add("Ryba");
            products.add("Kurczak");
            products.add("Gruszka");
            products.add("Jabłko");
            products.add("Banan");
            products.add("Pomarańcza");
            products.add("Cytryna");

            // Save defaults
            saveProducts(prefs, products);
        } else {
            // Load from database
            try {
                org.json.JSONArray array = new org.json.JSONArray(dbJson);
                for (int i = 0; i < array.length(); i++) {
                    products.add(array.getString(i));
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }

        // Sort alphabetically
        Collections.sort(products);

        adapter = new ProductsAdapter(products, (product, position) -> editProduct(product, position));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        productList.setLayoutManager(new LinearLayoutManager(this));
        productList.setAdapter(adapter);

        // Swipe to delete
        ItemTouchHelper swipeHelper = new ItemTouchHelper(
            new SwipeToDeleteStringCallback(products, () -> adapter.notifyDataSetChanged())
        );
        swipeHelper.attachToRecyclerView(productList);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Baza");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void editProduct(String product, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edytuj produkt");

        EditText input = new EditText(this);
        input.setText(product);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(product)) {
                products.set(position, newName);
                adapter.notifyItemChanged(position);
            }
        });
        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveProducts(android.content.SharedPreferences prefs, List<String> productList) {
        org.json.JSONArray array = new org.json.JSONArray();
        for (String product : productList) {
            array.put(product);
        }
        prefs.edit().putString("products_db", array.toString()).apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
