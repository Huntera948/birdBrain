package com.example.birdbrain.Activities;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.example.birdbrain.R;
import com.example.birdbrain.Entities.Bird;
import com.example.birdbrain.Database.Repository;
import com.example.birdbrain.Utilities.CameraUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BirdDetails extends AppCompatActivity implements CameraUtility.CameraActionListener {

    @Override
    public void onImageCaptured(Uri photoUri) {
        // Assuming you have a method in your Repository to handle the saving
        int birdId = -1;  // Use -1 or fetch the next ID from the database if adding a new bird
        String birdName = "New Bird";  // Default name, replace with actual data if available
        String birdNotes = "";
        String birdSightingDate = new SimpleDateFormat("MM/dd/yy", Locale.US).format(new Date());
        String birdLocationDescription = "";
        String birdImagePath = "";
        String birdAudioPath = "";
        Bird bird = new Bird(birdId, birdName, birdNotes, birdSightingDate, birdLocationDescription, birdImagePath, birdAudioPath);
        bird.setImagePath(photoUri.toString());
        repository.update(bird);

        // Load and display the captured image
        loadImageFromUri(photoUri, imageViewBird);

        Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        Log.i("CameraUtility", "Image saved at: " + photoUri);

        // If updating an existing bird, fetch the bird details from the database
        if (birdID != -1) {
            bird = repository.getBirdById(birdID);
            bird.setImagePath(photoUri.toString());  // Assuming photoUri is the URI of the newly captured image
        }
        repository.update(bird);
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    int birdID;
    String name;
    EditText editName;
    String birdNotes;
    EditText editNotes;
    String birdSightingDate;
    Button editSightingDate;
    String birdLocationDescription;
    EditText editBirdLocationDescription;
    String imagePath;
    String audioPath;
    Repository repository;
    Bird currentBird;
    private TextView dateTextView;
    private Button dateButton;
    private ImageView imageViewBird;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private CameraUtility cameraUtility;
    private MediaPlayer mediaPlayer;
    private Button playButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_details);

        cameraUtility = new CameraUtility(this, this);

        // Initialize UI components
        dateButton = findViewById(R.id.birdsightingdate);
        editName = findViewById(R.id.birdname);
        editNotes = findViewById(R.id.birdnotes);
        editSightingDate = findViewById(R.id.birdsightingdate);
        editBirdLocationDescription = findViewById(R.id.locationdescription);
        imageViewBird = findViewById(R.id.imageViewBird);

        playButton = findViewById(R.id.button_play);
        stopButton = findViewById(R.id.button_stop);

        playButton.setOnClickListener(v -> playAudio());
        stopButton.setOnClickListener(v -> stopAudio());

        Intent intent = getIntent();
        birdID = intent.getIntExtra("id", -1);

        repository = new Repository(getApplication());
        repository.insertLog("System", "Activity Start", "BirdDetails activity started.");

        // Fetch bird details from the database
        Bird bird = repository.getBirdById(birdID);

        if (bird != null) {
            // Bird exists, populate the UI with bird details
            editName.setText(bird.getBirdName());
            editNotes.setText(bird.getBirdNotes());
            editSightingDate.setText(bird.getBirdSightingDate());
            editBirdLocationDescription.setText(bird.getBirdLocationDescription());
            if (bird.getImagePath() != null && !bird.getImagePath().isEmpty()) {
                Uri imageUri = Uri.parse(bird.getImagePath());
                loadImageFromUri(imageUri, imageViewBird);
                playButton.setVisibility(View.VISIBLE);
            }
        } else {
            // No bird found, clear or set default values
            editName.setText("");
            editNotes.setText("");
            editSightingDate.setText("");
            editBirdLocationDescription.setText("");
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(BirdDetails.this,
                        android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                                birdSightingDate = sdf.format(selectedDate.getTime());
                                editSightingDate.setText(birdSightingDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        // Checking or requesting camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bird_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(BirdDetails.this, BirdList.class);
            startActivity(intent);
            repository.insertLog("User", "Navigate Home", "User navigated back to BirdList from BirdDetails.");
            return true;
        }
        if (item.getItemId() == R.id.birdsave) {
            birdNotes = editNotes.getText().toString();
            birdLocationDescription = editBirdLocationDescription.getText().toString();
            name = editName.getText().toString();

            if (!isValidDateFormat(birdSightingDate)) {
                Toast.makeText(BirdDetails.this, "Invalid date format. Please use MM/dd/yy", Toast.LENGTH_LONG).show();
                return true;
            }

            Bird bird = new Bird(birdID, name, birdNotes, birdSightingDate, birdLocationDescription, imagePath, audioPath);
            String birdDetails = "Name: " + name + ", Notes: " + birdNotes + ", Date: " + birdSightingDate;
            if (birdID == -1) {
                if (repository.getAllBirds().size() == 0) {
                    birdID = 1;
                    repository.insertLog("User", "Save New Bird", "New bird details saved: " + birdDetails);
                } else {
                    birdID = repository.getAllBirds().get(repository.getAllBirds().size() - 1).getBirdID() + 1;
                    repository.insertLog("User", "Update Bird", "Bird details updated: " + birdDetails);
                }
                bird.setBirdID(birdID);
                repository.insert(bird);
                Toast.makeText(BirdDetails.this, "Bird saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                repository.update(bird);
                Toast.makeText(BirdDetails.this, "Bird updated successfully!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (item.getItemId() == R.id.birddelete) {
            for (Bird prod : repository.getAllBirds()) {
                if (prod.getBirdID() == birdID) currentBird = prod;
            }
            repository.delete(currentBird);
            Toast.makeText(BirdDetails.this, currentBird.getBirdName() + " was deleted", Toast.LENGTH_LONG).show();
            repository.insertLog("User", "Delete Bird", "Bird deleted: ID " + birdID);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.share) {
            name = editName.getText().toString();
            birdNotes = editNotes.getText().toString();
            birdSightingDate = editSightingDate.getText().toString();
            birdLocationDescription = editBirdLocationDescription.getText().toString();
            String birdDetails = "Bird Details:\n" +
                    "Name: " + name + "\n" +
                    "Notes: " + birdNotes + "\n" +
                    "Sighting Date: " + birdSightingDate + "\n" +
                    "Location Description: " + birdLocationDescription + "\n";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, birdDetails);
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Bird Details");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "Share via");
            startActivity(shareIntent);
            repository.insertLog("User", "Share Bird", "Bird details shared.");
            return true;
        }

        if (item.getItemId() == R.id.recordirdcall) {
            Intent intent = new Intent(this, com.example.birdbrain.Activities.MediaRecorderActivity.class);
            // Pass the birdID as an extra in the intent
            intent.putExtra("id", birdID);
            startActivity(intent);
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.takebirdpicture) {
            checkPermissionsAndTakePicture();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        repository.insertLog("System", "Activity Resume", "BirdDetails activity resumed.");
        //Toast.makeText(BirdDetails.this,"refresh list",Toast.LENGTH_LONG).show();
        Bird updatedBird = repository.getBirdById(birdID);
        updatePlayButtonVisibility(updatedBird.getAudioPath());
    }

    private boolean isValidDateFormat(String date) {
        if (date == null) {
            return false;  // Immediately return false if the date is null
        }
        String regex = "\\d{2}/\\d{2}/\\d{2}";  // MM/dd/yy format
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    private void loadImageIntoView(String imagePath) {
        Uri imageUri = Uri.parse(imagePath);
        loadImageFromUri(imageUri, imageViewBird);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePicture();
        } else {
            Toast.makeText(this, "Camera permission is necessary", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionsAndTakePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePicture();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private Uri getPhotoFileUri() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, "JPEG_" + timeStamp + ".jpg");

        return FileProvider.getUriForFile(this, "com.example.birdbrain.fileprovider", image);
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Uri photoUri = getPhotoFileUri();
            if (photoUri != null) {
                cameraUtility.takePicture(photoUri);
            } else {
                Toast.makeText(this, "Error preparing file for photo", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void loadImageFromUri(Uri imageUri, ImageView imageView) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e("LoadImage", "File not found", e);
            Toast.makeText(this, "Unable to load image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                Bird bird = repository.getBirdById(birdID);
                String audioUriString = bird.getAudioPath();
                if (audioUriString != null && !audioUriString.isEmpty()) {
                    Uri audioUri = Uri.parse(audioUriString);
                    mediaPlayer.setDataSource(getApplicationContext(), audioUri);
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    mediaPlayer.start();
                    stopButton.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Playing audio...", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to play audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            stopButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void updatePlayButtonVisibility(String audioPath) {
        if (audioPath != null && !audioPath.trim().isEmpty()) {
            playButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.GONE);
        }
    }
}