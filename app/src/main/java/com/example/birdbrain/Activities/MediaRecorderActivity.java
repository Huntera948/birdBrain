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

    private MediaRecorderUtility recorderUtility;
    private TextView statusTextView;
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);

        statusTextView = findViewById(R.id.textViewStatus);
        startButton = findViewById(R.id.startRecordingButton);
        stopButton = findViewById(R.id.stopRecordingButton);

        recorderUtility = new MediaRecorderUtility(this, this);

        startButton.setOnClickListener(v -> {
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

    @Override
    public void onRecordingStart(Uri fileUri) {
        statusTextView.setText("Status: Recording... File Path: " + fileUri.toString());
    }

    @Override
    public void onRecordingStop(String message) {
        statusTextView.setText("Status: Recording Stopped. " + message);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        // Reset buttons in case of error
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
}


