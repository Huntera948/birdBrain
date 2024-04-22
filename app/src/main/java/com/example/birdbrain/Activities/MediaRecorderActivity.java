package com.example.birdbrain.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.birdbrain.Database.Repository;
import com.example.birdbrain.R;
import com.example.birdbrain.Utilities.MediaRecorderUtility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaRecorderActivity extends AppCompatActivity implements MediaRecorderUtility.MediaRecorderListener {
    private static final int PERMISSION_CODE = 200;
    private MediaRecorderUtility recorderUtility;
    private TextView statusTextView;
    private Button startButton;
    private Button stopButton;
    Repository repository;
    private int birdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);

        statusTextView = findViewById(R.id.textViewStatus);
        startButton = findViewById(R.id.startRecordingButton);
        stopButton = findViewById(R.id.stopRecordingButton);

        repository = new Repository(getApplication());
        recorderUtility = new MediaRecorderUtility(this, this);
        birdID = getIntent().getIntExtra("id", -1);

        recorderUtility.setMediaRecorderListener(new MediaRecorderUtility.MediaRecorderListener() {
            @Override
            public void onRecordingStart(Uri fileUri) {
                runOnUiThread(() -> statusTextView.setText("Status: Recording... File Path: " + fileUri.toString()));
            }

            @Override
            public void onRecordingStop(Uri fileUri) {
                runOnUiThread(() -> {
                    statusTextView.setText("Status: Recording Stopped. File saved at: " + fileUri.toString());
                    repository.saveOrUpdateAudioPath(birdID, fileUri.toString());
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(MediaRecorderActivity.this, message, Toast.LENGTH_SHORT).show();
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });
            }
        });

        startButton.setOnClickListener(v -> {
            checkPermissions();
            recorderUtility.startRecording();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        });

        stopButton.setOnClickListener(v -> {
            recorderUtility.stopRecording();
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
        });
    }

    private void checkPermissionsAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
        } else {
            recorderUtility.startRecording();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecordingStart(Uri fileUri) {
        statusTextView.setText("Status: Recording...");
        Toast.makeText(MediaRecorderActivity.this, "File saved at: " + fileUri.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRecordingStop(Uri fileUri) {
        runOnUiThread(() -> {
            statusTextView.setText("Status: Recording Stopped.");
            Toast.makeText(MediaRecorderActivity.this, "File saved at: " + fileUri.toString(), Toast.LENGTH_LONG).show();
            repository.saveOrUpdateAudioPath(birdID, fileUri.toString());
        });
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void saveRecordingPath(String filePath) {
        // Assuming Repository has a method to save or update the path in your database
        repository.saveOrUpdateAudioPath(birdID, filePath);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
        } else {
            recorderUtility.startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recorderUtility.startRecording();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}


