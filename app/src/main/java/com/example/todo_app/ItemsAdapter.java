package com.example.todo_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter binds data from a specific model to views displayed on a RecyclerView
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public interface OnClickListener {
        void onItemClicked(int position);
    }

    public interface OnLongClickListener{
        void onItemLongClicked(int position);
    }

    // Fields
    List<String> items;
    OnLongClickListener longClickListener;
    OnClickListener clickListener;

    // Constructor
    public ItemsAdapter(List<String> items, OnLongClickListener longClickListener, OnClickListener clickListener){
        this.items = items;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }

    // Creates a new View Holder of the given time to represent an item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a view and inflate it with LayoutInflater
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_expandable_list_item_1, parent, false );
        // Wrap it inside a View Holder and return it
        return new ViewHolder(todoView);
    }

    // Updates the content of an item in a ViewHolder (changes to reflect the item at the given position)
    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {
        // Grab item at the position
        String item = items.get(position);
        // Bind item into specified view holder
        holder.bind(item);
    }

    // Returns the number of items available in the data
    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder describes (holds) an item and important information related to it (like its position)
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItem; // in this case, all views will be text

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1); // NOTE: text1 is Android's Framework identifier for TextView views
        }

        // Update view inside of the view holder with this data
        public void bind(String item) {
            tvItem.setText(item);

            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Notify the listener which position was clicked
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });

            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Notify the listener which position was long pressed.
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
