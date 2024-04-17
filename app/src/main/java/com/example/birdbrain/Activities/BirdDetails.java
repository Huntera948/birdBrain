package com.example.birdbrain.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.birdbrain.Database.Repository;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdbrain.Entities.Excursion;
import com.example.birdbrain.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.birdbrain.Entities.Bird;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BirdDetails extends AppCompatActivity {
    int birdID;
    String name;
    EditText editName;
    String birdNotes;
    EditText editNotes;
    String birdSightingDate;
    Button editSightingDate;
    String birdLocationDescription;
    EditText editBirdLocationDescription;
    Repository repository;
    Bird currentBird;
    int numExcursions;
    private TextView dateTextView;
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_details);

        dateButton = findViewById(R.id.birdsightingdate);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        editName = findViewById(R.id.birdname);
        editName.setText(name);
        birdID = intent.getIntExtra("id", -1);
        editNotes = findViewById(R.id.birdnotes);
        birdNotes = intent.getStringExtra("notes");
        editNotes = findViewById(R.id.birdnotes);
        editNotes.setText(birdNotes);
        birdSightingDate = intent.getStringExtra("birdSightingDate");
        editSightingDate = findViewById(R.id.birdsightingdate);
        editSightingDate.setText(birdSightingDate);
        birdLocationDescription = intent.getStringExtra("birdLocationDescription");
        editBirdLocationDescription = findViewById(R.id.locationdescription);
        editBirdLocationDescription.setText(birdLocationDescription);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this, birdSightingDate, birdLocationDescription);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion p : repository.getAllExcursions()) {
            if (p.getBirdID() == birdID) filteredExcursions.add(p);
        }
        excursionAdapter.setExcursions(filteredExcursions);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BirdDetails.this, ExcursionDetails.class);
                intent.putExtra("birdID", birdID);
                intent.putExtra("birdSightingDate", birdSightingDate);
                intent.putExtra("birdLocationDescription", birdLocationDescription);
                startActivity(intent);
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(BirdDetails.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                                birdSightingDate = sdf.format(selectedDate.getTime());
                                dateButton.setText(birdSightingDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bird_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(BirdDetails.this, BirdList.class);
            startActivity(intent);
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

            Bird bird = new Bird(birdID, name, birdNotes, birdSightingDate, birdLocationDescription);

            if (birdID == -1) {
                if (repository.getAllBirds().size() == 0) {
                    birdID = 1;
                } else {
                    birdID = repository.getAllBirds().get(repository.getAllBirds().size() - 1).getBirdID() + 1;
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
            numExcursions = 0;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getBirdID() == birdID) ++numExcursions;
            }
            if (numExcursions == 0) {
                repository.delete(currentBird);
                Toast.makeText(BirdDetails.this, currentBird.getBirdName() + " was deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(BirdDetails.this, "Can't delete a bird with excursions", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if (item.getItemId() == R.id.addSampleExcursions) {
            if (birdID == -1)
                Toast.makeText(BirdDetails.this, "Please save bird before adding excursions", Toast.LENGTH_LONG).show();
            else {
                int excursionID;

                if (repository.getAllExcursions().size() == 0) excursionID = 1;
                else
                    excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;
                Excursion excursion = new Excursion(excursionID, "spa day", birdID, "02/01/95");
                repository.insert(excursion);
                excursion = new Excursion(++excursionID, "museum", birdID, "02/01/95");
                repository.insert(excursion);
                RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
                final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this, birdSightingDate, birdLocationDescription);
                recyclerView.setAdapter(excursionAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                List<Excursion> filteredExcursions = new ArrayList<>();
                for (Excursion p : repository.getAllExcursions()) {
                    if (p.getBirdID() == birdID) filteredExcursions.add(p);
                }
                excursionAdapter.setExcursions(filteredExcursions);
                return true;
            }
        }
        if (item.getItemId() == R.id.share) {
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
            return true;
        }
        if (item.getItemId() == R.id.notify) {
//            String myFormat = "MM/dd/yy";
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//            try {
//                Date sightingDate = sdf.parse(birdSightingDate);
//                Long startTrigger = sightingDate.getTime();
//                Intent startIntent = new Intent(BirdDetails.this, MyReceiver.class);
//                startIntent.putExtra("key", "Bird '" + name + "' is starting.");
//                PendingIntent startSender = PendingIntent.getBroadcast(BirdDetails.this, ++MainActivity.numAlert, startIntent, PendingIntent.FLAG_IMMUTABLE);
//                AlarmManager startAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                startAlarmManager.set(AlarmManager.RTC_WAKEUP, startTrigger, startSender);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            try {
//                Date endDate = sdf.parse(birdLocationDescription);
//                Long endTrigger = endDate.getTime();
//                Intent endIntent = new Intent(BirdDetails.this, MyReceiver.class);
//                endIntent.putExtra("key", "Bird '" + name + "' is ending.");
//                PendingIntent endSender = PendingIntent.getBroadcast(BirdDetails.this, ++MainActivity.numAlert, endIntent, PendingIntent.FLAG_IMMUTABLE);
//                AlarmManager endAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                endAlarmManager.set(AlarmManager.RTC_WAKEUP, endTrigger, endSender);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this, birdSightingDate, birdLocationDescription);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion p : repository.getAllExcursions()) {
            if (p.getBirdID() == birdID) filteredExcursions.add(p);
        }
        excursionAdapter.setExcursions(filteredExcursions);
        //Toast.makeText(BirdDetails.this,"refresh list",Toast.LENGTH_LONG).show();
    }

    private boolean isValidDateFormat(String date) {
        String regex = "\\d{2}/\\d{2}/\\d{2}"; // MM/dd/yy format
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }
}