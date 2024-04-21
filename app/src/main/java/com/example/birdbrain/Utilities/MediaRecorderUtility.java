package com.example.birdbrain.Utilities;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaRecorderUtility {

    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;

    // Listener interface to communicate recording events
    public interface MediaRecorderListener {
        void onRecordingStart(String filePath);

        void onRecordingStop(String filePath);

        void onError(String message);
    }

    private MediaRecorderListener listener;

    public MediaRecorderUtility(MediaRecorderListener listener) {
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

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BirdCalls/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File audioFile = new File(storageDir, audioFileName);
        audioFilePath = audioFile.getAbsolutePath();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);
    }
}
