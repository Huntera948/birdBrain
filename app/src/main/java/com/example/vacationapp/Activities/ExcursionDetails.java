package com.example.vacationapp.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacationapp.Database.Repository;
import com.example.vacationapp.Entities.Excursion;
import com.example.vacationapp.Entities.Vacation;
import com.example.vacationapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {
    String name;
    String date;
    EditText editName;
    String vacationStartDate;
    String vacationEndDate;
    int excursionID;
    int prodID;
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
        prodID = getIntent().getIntExtra("prodID", -1);
        editNote = findViewById(R.id.note);
        date = getIntent().getStringExtra("date");
        editDate = findViewById(R.id.date);
        editDate.setText(date);
        vacationStartDate = getIntent().getStringExtra("vacationStartDate");
        vacationEndDate = getIntent().getStringExtra("vacationEndDate");

        repository = new Repository(getApplication());
        ArrayList<Vacation> vacationArrayList = new ArrayList<>(repository.getAllVacations());
        ArrayList<Integer> vacationIdList = new ArrayList<>();
        for (Vacation vacation : vacationArrayList) {
            vacationIdList.add(vacation.getVacationID());
        }
        ArrayAdapter<Integer> vacationIdAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vacationIdList);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(vacationIdAdapter);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            Intent intent = new Intent(ExcursionDetails.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        // return true;
//                Intent intent=new Intent(ExcursionDetails.this,MainActivity.class);
//                startActivity(intent);
//                return true;

        if (item.getItemId() == R.id.excursionsave) {
            // Your existing code to save or update the excursion

            // Add excursion date validation
            String excursionDateStr = editDate.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            try {
                Date excursionDate = sdf.parse(excursionDateStr);
                // Retrieve start and end dates of the associated vacation
                Date startDate = sdf.parse(vacationStartDate);
                Date endDate = sdf.parse(vacationEndDate);
                // Check if the excursion date is within the vacation period
                if (excursionDate.before(startDate) || excursionDate.after(endDate)) {
                    Toast.makeText(ExcursionDetails.this, "Excursion date must be during the associated vacation", Toast.LENGTH_LONG).show();
                    return true; // Exit the method without saving the excursion
                }
            } catch (ParseException e) {
                e.printStackTrace();
                // Handle parse exception
            }
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
}
