package com.example.birdbrain.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.birdbrain.Entities.LogEntry;
import com.example.birdbrain.R;
import com.example.birdbrain.Database.Repository;
import com.example.birdbrain.Utilities.PDFGenerator;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //public static int numAlert;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new Repository(getApplication());

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repository.insertLog("User123","Home Screen Enter","User clicked the enter button on the home screen");
                Intent intent = new Intent(MainActivity.this, BirdList.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.generateReport) {
            generatePDFReport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void generatePDFReport() {
        Repository repository = new Repository(getApplication());
        List<LogEntry> logs = repository.getAllLogs(); // Make sure to fetch logs in a way that does not block the UI thread
        PDFGenerator.generateLogReport(MainActivity.this, logs);
    }
}