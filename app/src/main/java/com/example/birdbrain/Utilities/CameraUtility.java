package com.example.birdbrain.Utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

public class CameraUtility {
    private final Context context;
    private final CameraActionListener listener;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri currentPhotoUri;
    private static final String TAG = CameraUtility.class.getSimpleName();

    // Interface for callback
    public interface CameraActionListener {
        void onImageCaptured(Uri photoUri);

        void onError(String error);
    }
    public CameraUtility(Context context, CameraActionListener listener) {
        this.context = context;
        this.listener = listener;
        setupTakePictureLauncher();
    }

    private void setupTakePictureLauncher() {
        takePictureLauncher = ((AppCompatActivity) context).registerForActivityResult(
                new ActivityResultContracts.TakePicture(), result -> {
                    if (result) {
                        Log.d(TAG, "Image capture successful: " + currentPhotoUri.toString());
                        listener.onImageCaptured(currentPhotoUri);
                    } else {
                        Log.e(TAG, "Image capture failed.");
                        listener.onError("Image capture failed.");
                    }
                }
        );
    }

    public void takePicture(Uri photoUri) {
        if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            this.currentPhotoUri = photoUri;
            takePictureLauncher.launch(photoUri);
        } else {
            Log.e(TAG, "Camera permission not granted.");
            listener.onError("Camera permission not granted.");
        }
    }

}

