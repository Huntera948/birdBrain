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
    private boolean isRecording = false;
    private final Context context;
    private MediaRecorderListener listener;
    private static final String TAG = MediaRecorderUtility.class.getSimpleName();

    public interface MediaRecorderListener {
        void onRecordingStart(Uri fileUri);
        void onRecordingStop(String message);
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

        Uri fileUri = setupMediaRecorder();

        if (fileUri != null) {
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
                listener.onRecordingStart(fileUri);
            } catch (IOException e) {
                Log.e(TAG, "Prepare failed", e);
                listener.onError("Failed to start recording: " + e.getMessage());
            }
        }
    }

    public void stopRecording() {
        if (mediaRecorder != null && isRecording) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            listener.onRecordingStop("Recording stopped and saved.");
        }
    }

    private Uri setupMediaRecorder() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String audioFileName = "AUDIO_" + timeStamp + ".3gp";
        File storageDir = new File(context.getExternalFilesDir(null), "BirdCalls");

        if (!storageDir.exists() && !storageDir.mkdirs()) {
            listener.onError("Failed to create directory for storing recordings.");
            return null;
        }

        File audioFile = new File(storageDir, audioFileName);
        Uri audioFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", audioFile);

        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(audioFileUri, "w");
            if (pfd == null) {
                listener.onError("Failed to open file descriptor.");
                return null;
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(pfd.getFileDescriptor());

            return audioFileUri;
        } catch (IOException e) {
            Log.e(TAG, "Setup media recorder failed", e);
            listener.onError("Setup media recorder failed: " + e.getMessage());
            return null;
        }
    }
}
