package com.example.birdbrain.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.birdbrain.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.birdbrain.Database.Repository;
import com.example.birdbrain.Entities.Bird;

import java.util.List;

public class BirdList extends AppCompatActivity {
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_list);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BirdList.this, BirdDetails.class);
                startActivity(intent);
            }
        });
        repository = new Repository(getApplication());
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
            return true;
        }

        if (item.getItemId() == R.id.addSampleBirds) {
            Repository repo = new Repository(getApplication());
            Bird bird = new Bird(1, "Black-capped Chickadee", "really fuckin cool bird", "01/31/95", "01/31/24");
            repo.insert(bird);
            bird = new Bird(2, "Sandhill Crane", "fuckin sick bird right here", "01/31/95", "01/31/24");
            repo.insert(bird);
            List<Bird> allBirds = repository.getAllBirds();
            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            final BirdAdapter birdAdapter = new BirdAdapter(this);
            recyclerView.setAdapter(birdAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            birdAdapter.setBirds(allBirds);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
        List<Bird> allBirds = repository.getAllBirds();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BirdAdapter birdAdapter = new BirdAdapter(this);
        recyclerView.setAdapter(birdAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        birdAdapter.setBirds(allBirds);

        //Toast.makeText(BirdDetails.this,"refresh list",Toast.LENGTH_LONG).show();
    }
}