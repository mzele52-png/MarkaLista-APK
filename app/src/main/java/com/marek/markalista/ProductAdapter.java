package com.marek.markalista;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private Runnable onListChanged;

    public ProductAdapter(List<Product> products, Runnable onListChanged) {
        this.products = products;
        this.onListChanged = onListChanged;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, position, products, onListChanged, this);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView productName;
        TextView voiceStar;
        TextView quantityField;
        ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.productCheckbox);
            productName = itemView.findViewById(R.id.productName);
            voiceStar = itemView.findViewById(R.id.voiceStar);
            quantityField = itemView.findViewById(R.id.quantityField);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        void bind(Product product, int position, List<Product> products, Runnable onListChanged, ProductAdapter adapter) {
            checkbox.setChecked(product.done);
            productName.setText(product.name);

            // Voice-added new products: show star and magenta color
            if (product.newByVoice) {
                voiceStar.setVisibility(View.VISIBLE);
                voiceStar.setTextColor(product.done ? 0xFF7a7a7a : 0xffc026d3);
                productName.setTextColor(product.done ? 0xFF7a7a7a : 0xffc026d3);
            } else {
                voiceStar.setVisibility(View.GONE);
                productName.setTextColor(product.done ? 0xFF7a7a7a : 0xFFFFFFFF);
            }

            updateStrikethrough(product);

            quantityField.setText(product.quantity);

            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                product.done = isChecked;
                updateStrikethrough(product);
                onListChanged.run();
            });

            quantityField.setText(product.quantity.isEmpty() ? "ilość" : product.quantity);
            quantityField.setTextColor(product.done ? 0xFF7a7a7a : 0xFF999999);

            deleteButton.setOnClickListener(v -> {
                products.remove(position);
                adapter.notifyItemRemoved(position);
                onListChanged.run();
            });
        }

        private void updateStrikethrough(Product product) {
            if (product.done) {
                productName.setPaintFlags(productName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                productName.setTextColor(0xFF7a7a7a);
                quantityField.setPaintFlags(quantityField.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                productName.setPaintFlags(productName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                productName.setTextColor(0xFFFFFFFF);
                quantityField.setPaintFlags(quantityField.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }
}
