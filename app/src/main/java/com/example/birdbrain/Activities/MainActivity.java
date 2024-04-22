package com.example.birdbrain.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.birdbrain.Entities.LogEntry;
import com.example.birdbrain.R;
import com.example.birdbrain.Database.Repository;
import com.example.birdbrain.Utilities.PDFGenerator;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new Repository(getApplication());

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repository.insertLog("User123","Home Screen Login","User logged in.");
                Intent intent = new Intent(MainActivity.this, BirdList.class);
                startActivity(intent);
                EditText usernameEt = findViewById(R.id.username);
                EditText passwordEt = findViewById(R.id.password);
                String username = usernameEt.getText().toString();
                String password = passwordEt.getText().toString();
                login(username, password);
            }
        });
        Button createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repository.insertLog("User123","RegisterActivity Navigation","User navigated to Register page.");
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
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
        if (id == R.id.viewPDFLog) {
            Intent intent = new Intent(this, PDFViewerActivity.class);
            intent.putExtra("filePath", "MyAppLogs/logReport.pdf");  // Only the file name if it's directly in the internal storage root
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void generatePDFReport() {
        Repository repository = new Repository(getApplication());
        List<LogEntry> logs = repository.getAllLogs(); // Make sure to fetch logs in a way that does not block the UI thread
        PDFGenerator.generateLogReport(MainActivity.this, logs);
    }
    public void login(String username, String password) {
        // Assuming you have a Repository instance named repository
        boolean success = repository.login(username, password);
        Toast Toast = null;
        if (success) {
            // Handle successful login
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            // Navigate to another activity or update UI
        } else {
            // Handle failed login
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }
}