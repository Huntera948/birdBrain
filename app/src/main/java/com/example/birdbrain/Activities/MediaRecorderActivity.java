package com.example.birdbrain.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.birdbrain.R;
import com.example.birdbrain.Utilities.MediaRecorderUtility;

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

        recorderUtility = new MediaRecorderUtility(this);

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
    public void onRecordingStart(String filePath) {
        statusTextView.setText("Status: Recording... File Path: " + filePath);
    }

    @Override
    public void onRecordingStop(String filePath) {
        statusTextView.setText("Status: Recording Stopped. File saved at: " + filePath);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        // Reset buttons in case of error
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
}
