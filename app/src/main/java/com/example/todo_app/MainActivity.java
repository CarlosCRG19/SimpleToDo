package com.example.todo_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils; // Use apache File Utils instead of Android's

public class MainActivity extends AppCompatActivity {

    // Data for intents
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 42;


    List<String> items; // Array list that will store all tasks

    // Views
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;

    // Adapter Object (extends RecyclerView.Adapter)
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign each view through id
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        // Load items from plain file
        loadItems();

        // Use interface to define an action when long click on an item (delete)
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                items.remove(position);
                // Notify that item was removed
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item removed!", Toast.LENGTH_SHORT).show(); // displays small popup with a success message
                // Save changes on plain file
                saveItems();
            }
        };

        // Use interface to define an action when item is clicked (edit)
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                // Create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // Pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // Display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };

        // Assign adapter to recyclerview
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this)); // provides similar functionality to ListView

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                //Add item to the model
                items.add(todoItem);
                // Notify the adapter that item is inserted
                itemsAdapter.notifyItemInserted(items.size()-1);
                // Delete text entry on the EditText
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    // Handle result of edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            // Retrieve updated value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // Extract original position
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            // Update model at specified position
            items.set(position, itemText);
            // Notify the adapter
            itemsAdapter.notifyItemChanged(position);
            Toast.makeText(getApplicationContext(), "Item updated succesfully!", Toast.LENGTH_SHORT).show();
            // Persist changes
            saveItems();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }


    }

    // Returns plain file as a File object
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // Reads data.txt and loads every item
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch(IOException e){
            Log.e("MainActivity", "Error reading items", e);
            // In case of error, assign items to an empty ArrayList
            items = new ArrayList<>();
        }
    }

    // Writes items into the data file for persistence
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch(IOException e){
            Log.e("MainActivity", "Error reading items", e);
        }
    }

}