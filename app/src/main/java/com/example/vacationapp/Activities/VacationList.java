package com.example.vacationapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.vacationapp.Database.Repository;
import com.example.vacationapp.Entities.Vacation;
import com.example.vacationapp.R;

import java.util.List;

public class VacationList extends AppCompatActivity {
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VacationList.this, VacationDetails.class);
                startActivity(intent);
            }
        });
        repository = new Repository(getApplication());
        List<Vacation> allVacations = repository.getAllVacations();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
//                Intent intent=new Intent(VacationDetails.this,MainActivity.class);
//                startActivity(intent);
            return true;
        }

        if (item.getItemId() == R.id.addSampleVacations) {
            Repository repo = new Repository(getApplication());
            Vacation vacation = new Vacation(1, "Italy Trip", "Hotel Italy", "01/31/1995", "01/31/2024");
            repo.insert(vacation);
            vacation = new Vacation(2, "Greece Trip", "Hotel Greece", "01/31/1995", "01/31/2024");
            repo.insert(vacation);
            List<Vacation> allVacations = repository.getAllVacations();
            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            final VacationAdapter vacationAdapter = new VacationAdapter(this);
            recyclerView.setAdapter(vacationAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            vacationAdapter.setVacations(allVacations);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
        List<Vacation> allVacations = repository.getAllVacations();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);

        //Toast.makeText(VacationDetails.this,"refresh list",Toast.LENGTH_LONG).show();
    }
}