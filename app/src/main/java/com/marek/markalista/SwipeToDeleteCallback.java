package com.marek.markalista;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SwipeToDeleteCallback extends ItemTouchHelper.Callback {
    private final List<Product> products;
    private final Runnable onDelete;

    public SwipeToDeleteCallback(List<Product> products, Runnable onDelete) {
        this.products = products;
        this.onDelete = onDelete;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (position >= 0 && position < products.size()) {
            products.remove(position);
            onDelete.run();
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {
            // Red background for delete (#c62828)
            Paint paint = new Paint();
            paint.setColor(0xffc62828);
            RectF background = new RectF(
                recyclerView.getWidth() + dX,
                viewHolder.itemView.getTop(),
                recyclerView.getWidth(),
                viewHolder.itemView.getBottom()
            );
            c.drawRect(background, paint);

            // White trash icon and text
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(48);
            textPaint.setTextAlign(Paint.Align.CENTER);
            float x = recyclerView.getWidth() - 44;
            float y = viewHolder.itemView.getTop() + (viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2f + 18;
            c.drawText("🗑", x, y, textPaint);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
