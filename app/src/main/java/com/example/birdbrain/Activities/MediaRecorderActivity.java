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
    private static final String TAG = MediaRecorderActivity.class.getSimpleName();
    private MediaRecorderUtility recorderUtility;
    private TextView statusTextView;
    private Button startButton;
    private Button stopButton;
    private MediaRecorder mediaRecorder;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);
        repository = new Repository(getApplication());

        statusTextView = findViewById(R.id.textViewStatus);
        startButton = findViewById(R.id.startRecordingButton);
        stopButton = findViewById(R.id.stopRecordingButton);

        recorderUtility = new MediaRecorderUtility(this, this);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            repository.insertLog("User", "Navigate Home", "User navigated back to BirdDetails from MediaRecorderActivity.");
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
        } else {
            // Permission has already been granted, safe to start recording
            recorderUtility.startRecording();
        }
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    PERMISSION_CODE);
        } else {
            // Permissions are already granted
            setupMediaRecorder();
        }
    }

    private void checkPermissionsAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_CODE);
        } else {
            setupMediaRecorder();
            mediaRecorder.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setupMediaRecorder();
                mediaRecorder.start();
            } else {
                Log.e("Permissions", "Permission denied by user");
            }
        }
    }

    private void setupMediaRecorder() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String audioFileName = "AUDIO_" + timeStamp + ".3gp";

        File storageDir = new File(getExternalFilesDir(null), "BirdCalls");
        if (!storageDir.mkdirs() && !storageDir.exists()) {
            Log.e(TAG, "Failed to create directory");
            return;
        }

        File audioFile = new File(storageDir, audioFileName);
        Uri audioFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", audioFile);

        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(audioFileUri, "w");
            if (pfd == null) {
                Log.e(TAG, "Failed to open file descriptor");
                return;
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(pfd.getFileDescriptor());
            mediaRecorder.prepare();
            pfd.close();
        } catch (IOException e) {
            Log.e(TAG, "Setup media recorder failed", e);
        }
    }

}
