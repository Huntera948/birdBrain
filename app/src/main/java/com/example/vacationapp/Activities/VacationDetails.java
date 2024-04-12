package com.example.vacationapp.Activities;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vacationapp.Database.Repository;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationapp.Entities.Excursion;
import com.example.vacationapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import com.example.vacationapp.Entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VacationDetails extends AppCompatActivity {
    String name;
    String vacationHotel;
    String vacationStartDate;
    String vacationEndDate;
    int vacationID;
    EditText editName;
    EditText editHotel;
    EditText editStartDate;
    EditText editEndDate;
    Repository repository;
    Vacation currentVacation;
    int numExcursions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);
        name = getIntent().getStringExtra("name");
        editName = findViewById(R.id.vacationname);
        editName.setText(name);
        vacationID = getIntent().getIntExtra("id", -1);
        editHotel = findViewById(R.id.vacationhotel);
        vacationHotel = getIntent().getStringExtra("hotel");
        editHotel = findViewById(R.id.vacationhotel);
        editHotel.setText(vacationHotel);
        vacationStartDate = getIntent().getStringExtra("vacationStartDate");
        editStartDate = findViewById(R.id.vacationstartdate);
        editStartDate.setText(vacationStartDate);
        vacationEndDate = getIntent().getStringExtra("vacationEndDate");
        editEndDate = findViewById(R.id.vacationenddate);
        editEndDate.setText(vacationEndDate);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion p : repository.getAllExcursions()) {
            if (p.getVacationID() == vacationID) filteredExcursions.add(p);
        }
        excursionAdapter.setExcursions(filteredExcursions);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("prodID", vacationID);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.vacationsave) {
            Vacation vacation;
            if (vacationID == -1) {
                if (repository.getAllVacations().size() == 0) vacationID = 1;
                else
                    vacationID = repository.getAllVacations().get(repository.getAllVacations().size() - 1).getVacationID() + 1;
                vacationHotel = editHotel.getText().toString();
                vacationStartDate = editStartDate.getText().toString();
                vacationEndDate = editEndDate.getText().toString();
                if (!isValidDateFormat(vacationStartDate) || !isValidDateFormat(vacationEndDate)) {
                    Toast.makeText(VacationDetails.this, "Invalid date format. Please use MM/dd/yy", Toast.LENGTH_LONG).show();
                    return true;
                }
                if (!isEndDateAfterStartDate(vacationStartDate, vacationEndDate)) {
                    Toast.makeText(VacationDetails.this, "End date must be after start date", Toast.LENGTH_LONG).show();
                    return true;
                }
                vacation = new Vacation(vacationID, editName.getText().toString(), editHotel.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString());
                repository.insert(vacation);
            } else {
                try {
                    vacationHotel = editHotel.getText().toString();
                    vacationStartDate = editStartDate.getText().toString();
                    vacationEndDate = editEndDate.getText().toString();
                    if (!isValidDateFormat(vacationStartDate) || !isValidDateFormat(vacationEndDate)) {
                        Toast.makeText(VacationDetails.this, "Invalid date format. Both dates must be filled in and entered as MM/dd/yy", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if (!isEndDateAfterStartDate(vacationStartDate, vacationEndDate)) {
                        Toast.makeText(VacationDetails.this, "End date must be after start date", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    vacation = new Vacation(vacationID, editName.getText().toString(), editHotel.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString());
                    repository.update(vacation);
                } catch (Exception e) {

                }
            }
            return true;
        }
        if (item.getItemId() == R.id.vacationdelete) {
            for (Vacation prod : repository.getAllVacations()) {
                if (prod.getVacationID() == vacationID) currentVacation = prod;
            }

            numExcursions = 0;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getVacationID() == vacationID) ++numExcursions;
            }

            if (numExcursions == 0) {
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getVacationName() + " was deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if (item.getItemId() == R.id.addSampleExcursions) {
            if (vacationID == -1)
                Toast.makeText(VacationDetails.this, "Please save vacation before adding excursions", Toast.LENGTH_LONG).show();

            else {
                int excursionID;

                if (repository.getAllExcursions().size() == 0) excursionID = 1;
                else
                    excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;
                Excursion excursion = new Excursion(excursionID, "spa day", vacationID);
                repository.insert(excursion);
                excursion = new Excursion(++excursionID, "museum", vacationID);
                repository.insert(excursion);
                RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
                final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
                recyclerView.setAdapter(excursionAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                List<Excursion> filteredExcursions = new ArrayList<>();
                for (Excursion p : repository.getAllExcursions()) {
                    if (p.getVacationID() == vacationID) filteredExcursions.add(p);
                }
                excursionAdapter.setExcursions(filteredExcursions);
                return true;
            }

        }
        if (item.getItemId() == R.id.share) {
            String vacationDetails = "Vacation Details:\n" +
                    "Name: " + name + "\n" +
                    "Hotel: " + vacationHotel + "\n" +
                    "Start Date: " + vacationStartDate + "\n" +
                    "End Date: " + vacationEndDate + "\n";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails);
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Vacation Details");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "Share via");
            startActivity(shareIntent);
            return true;
        }

        if (item.getItemId() == R.id.notify) {
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            try {
                Date startDate = sdf.parse(vacationStartDate);
                Long startTrigger = startDate.getTime();
                Intent startIntent = new Intent(VacationDetails.this, MyReceiver.class);
                startIntent.putExtra("key", "Vacation '" + name + "' is starting.");
                PendingIntent startSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, startIntent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager startAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                startAlarmManager.set(AlarmManager.RTC_WAKEUP, startTrigger, startSender);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                Date endDate = sdf.parse(vacationEndDate);
                Long endTrigger = endDate.getTime();
                Intent endIntent = new Intent(VacationDetails.this, MyReceiver.class);
                endIntent.putExtra("key", "Vacation '" + name + "' is ending.");
                PendingIntent endSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, endIntent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager endAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                endAlarmManager.set(AlarmManager.RTC_WAKEUP, endTrigger, endSender);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {

        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion p : repository.getAllExcursions()) {
            if (p.getVacationID() == vacationID) filteredExcursions.add(p);
        }
        excursionAdapter.setExcursions(filteredExcursions);

        //Toast.makeText(VacationDetails.this,"refresh list",Toast.LENGTH_LONG).show();
    }

    private boolean isValidDateFormat(String date) {
        String regex = "\\d{2}/\\d{2}/\\d{2}"; // MM/dd/yy format
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    private boolean isEndDateAfterStartDate(String startDate, String endDate) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        try {
            Date startDateObj = format.parse(startDate);
            Date endDateObj = format.parse(endDate);
            return endDateObj.after(startDateObj);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}