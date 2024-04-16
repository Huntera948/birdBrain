package com.example.birdbrain.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.birdbrain.Database.Repository;
import com.example.birdbrain.Entities.Excursion;
import com.example.birdbrain.Entities.Bird;
import com.example.birdbrain.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcursionDetails extends AppCompatActivity {
    String name;
    String date;
    EditText editName;
    String birdSightingDate;
    String birdLocationDescription;
    int excursionID;
    int birdID;
    EditText editNote;
    EditText editDate;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = getIntent().getStringExtra("name");
        editName = findViewById(R.id.excursionName);
        editName.setText(name);
        excursionID = getIntent().getIntExtra("id", -1);
        birdID = getIntent().getIntExtra("birdID", -1);
        editNote = findViewById(R.id.note);
        date = getIntent().getStringExtra("date");
        editDate = findViewById(R.id.date);
        editDate.setText(date);
        birdSightingDate = getIntent().getStringExtra("birdSightingDate");
        birdLocationDescription = getIntent().getStringExtra("birdLocationDescription");
        Log.d("ExcursionDetails", "Excursion Details: Start Date: " + birdSightingDate + ", End Date: " + birdLocationDescription);
        repository = new Repository(getApplication());
        ArrayList<Bird> birdArrayList = new ArrayList<>(repository.getAllBirds());
        ArrayList<Integer> birdIdList = new ArrayList<>();
        for (Bird bird : birdArrayList) {
            birdIdList.add(bird.getBirdID());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        // return true;
//                Intent intent=new Intent(ExcursionDetails.this,MainActivity.class);
//                startActivity(intent);
//                return true;

        if (item.getItemId() == R.id.excursionsave) {
            String dateFromScreen = editDate.getText().toString();
            if (!isValidDateFormat(dateFromScreen)) {
                Toast.makeText(ExcursionDetails.this, "Invalid date format. Please use MM/dd/yy", Toast.LENGTH_LONG).show();
                return true;
            }

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                try {
                    Date excursionDate = sdf.parse(dateFromScreen);
                    Date sightingDate = sdf.parse(birdSightingDate);
                    Date endDate = sdf.parse(birdLocationDescription);

                    // Check if the excursion date is within the bird date range
                    if (excursionDate.before(sightingDate) || excursionDate.after(endDate)) {
                        Toast.makeText(ExcursionDetails.this, "Excursion date must be between bird start and end dates", Toast.LENGTH_LONG).show();
                        return true;
                    }

                    // Save or update the excursion as necessary
                    Excursion excursion;
                    if (excursionID == -1) {  // Handling for new excursion
                        excursionID = repository.getAllExcursions().size() == 0 ? 1 :
                                repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;
                        excursion = new Excursion(excursionID, editName.getText().toString(), birdID, dateFromScreen);
                        repository.insert(excursion);
                    } else {  // Handling for updating an existing excursion
                        excursion = new Excursion(excursionID, editName.getText().toString(), birdID, dateFromScreen);
                        repository.update(excursion);
                    }
                } catch (ParseException e) {
                    Toast.makeText(ExcursionDetails.this, "Error parsing dates", Toast.LENGTH_LONG).show();
                    return true;
                }
            return true;
        }

        if (item.getItemId() == R.id.excursiondelete) {
            Excursion currentExcursion = null;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getExcursionID() == excursionID) {
                    currentExcursion = excursion;
                    break;
                }
            }
            if (currentExcursion != null) {
                repository.delete(currentExcursion);
                Toast.makeText(ExcursionDetails.this, currentExcursion.getExcursionName() + " was deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ExcursionDetails.this, "Excursion not found", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if (item.getItemId() == R.id.share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString());
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Message Title");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            return true;
        }
        if (item.getItemId() == R.id.notify) {
            String dateFromScreen = editDate.getText().toString();
            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                Long trigger = myDate.getTime();
                Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
                intent.putExtra("key", "Excursion '" + name + "' is starting.");
                PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private boolean isValidDateFormat(String date) {
        String regex = "\\d{2}/\\d{2}/\\d{2}"; // MM/dd/yy format
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }
}
