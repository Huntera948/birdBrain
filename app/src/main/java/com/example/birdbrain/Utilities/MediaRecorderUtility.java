package com.example.birdbrain.Utilities;

import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaRecorderUtility {

    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private final Context context;

    private MediaRecorderListener listener;

    // Listener interface to communicate recording events
    public interface MediaRecorderListener {
        void onRecordingStart(String filePath);

        void onRecordingStop(String filePath);

        void onError(String message);
    }

    public MediaRecorderUtility(Context context, MediaRecorderListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void startRecording() {
        if (isRecording) {
            listener.onError("Recording is already in progress");
            return;
        }

        setupMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.i("MediaRecorderUtility", "audioFilePath Created at: " + audioFilePath);
            isRecording = true;
            if (listener != null) {
                listener.onRecordingStart(audioFilePath);
            }
        } catch (IOException e) {
            Log.e("MediaRecorderUtility", "Prepare failed for " + audioFilePath, e);
            if (listener != null) {
                listener.onError("Failed to start recording: " + e.getMessage());
            }
        }
    }

    public void stopRecording() {
        if (!isRecording) {
            listener.onError("No recording is in progress");
            return;
        }

        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            if (listener != null) {
                listener.onRecordingStop(audioFilePath);
            }
        }
    }

    private void setupMediaRecorder() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String audioFileName = "AUDIO_" + timeStamp + ".3gp";

        File storageDir = new File(context.getExternalFilesDir(null), "BirdCalls");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File audioFile = new File(storageDir, audioFileName);
        Uri audioFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", audioFile);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // Use ParcelFileDescriptor for API level 26 and above
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(audioFileUri, "w");
            if (pfd != null) {
                mediaRecorder.setOutputFile(pfd.getFileDescriptor());
            }

        } catch (Exception e) {
            Log.e("MediaRecorderUtility", "Exception setting up media recorder", e);
            if (listener != null) {
                listener.onError("Failed to set up media recorder: " + e.getMessage());
            }
        }
    }

}
