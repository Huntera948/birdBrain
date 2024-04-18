package com.example.birdbrain.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.birdbrain.R;

import com.example.birdbrain.Database.Repository;
import com.example.birdbrain.Entities.Bird;

import java.util.List;

public class BirdList extends AppCompatActivity {
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_list);
        repository = new Repository(getApplication());
        repository.insertLog("System", "Activity Created", "BirdList activity was created.");
        List<Bird> allBirds = repository.getAllBirds();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BirdAdapter birdAdapter = new BirdAdapter(this);
        recyclerView.setAdapter(birdAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        birdAdapter.setBirds(allBirds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_birdlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            //Intent intent=new Intent(BirdList.this, BirdDetails.class);
            //startActivity(intent);
            repository.insertLog("User", "Navigation", "User navigated back from BirdList.");
            return true;
        }

        if (item.getItemId() == R.id.addSampleBirds) {
            Repository repo = new Repository(getApplication());
            Bird bird = new Bird(1, "Black-capped Chickadee", "very loud", "04/15/24", "LeFurge Woods Nature Preserve", null);
            repo.insert(bird);
            bird = new Bird(2, "Sandhill Crane", "quite stinky", "04/14/24", "Meyer Preserve", null);
            repo.insert(bird);
            List<Bird> allBirds = repository.getAllBirds();
            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            final BirdAdapter birdAdapter = new BirdAdapter(this);
            recyclerView.setAdapter(birdAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            birdAdapter.setBirds(allBirds);
            repository.insertLog("User", "Sample Birds Added", "User added sample birds to the list.");
            return true;
        }

        if (item.getItemId() == R.id.addBird) {
            repository.insertLog("User", "Add Bird", "User initiated adding a new bird.");
            Intent intent = new Intent(BirdList.this, BirdDetails.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        repository.insertLog("System", "Activity Resumed", "BirdList activity was resumed.");
        List<Bird> allBirds = repository.getAllBirds();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BirdAdapter birdAdapter = new BirdAdapter(this);
        recyclerView.setAdapter(birdAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        birdAdapter.setBirds(allBirds);

        //Toast.makeText(BirdDetails.this,"refresh list",Toast.LENGTH_LONG).show();
    }
}