package com.example.birdbrain.Activities;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdbrain.Database.Repository;
import com.example.birdbrain.Entities.Bird;
import com.example.birdbrain.R;

import java.util.List;

public class BirdAdapter extends RecyclerView.Adapter<BirdAdapter.BirdViewHolder> {

    class BirdViewHolder extends RecyclerView.ViewHolder {
        private final TextView birdItemView;
        private final TextView notesItemView;
        private final TextView sightingDateItemView;
        private final TextView locationDescriptionItemView;
        Repository repository;

        private BirdViewHolder(View itemView) {
            super(itemView);
            birdItemView = itemView.findViewById(R.id.textView);
            notesItemView = itemView.findViewById(R.id.birdnotes);
            sightingDateItemView = itemView.findViewById(R.id.birdsightingdate);
            locationDescriptionItemView = itemView.findViewById(R.id.locationdescription);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Bird current = mBirds.get(position);
                    Intent intent = new Intent(context, BirdDetails.class);
                    intent.putExtra("id", current.getBirdID());
                    intent.putExtra("name", current.getBirdName());
                    intent.putExtra("notes", current.getBirdNotes());
                    intent.putExtra("birdSightingDate", current.getBirdSightingDate());
                    intent.putExtra("birdLocationDescription", current.getBirdLocationDescription());
                    context.startActivity(intent);
                    Repository repository = new Repository((Application) context.getApplicationContext());
                    repository.insertLog("User", "View Bird Details", "Viewed details for bird: " + current.getBirdName());
                }
            });
        }
    }

    private List<Bird> mBirds;
    private final Context context;
    private final LayoutInflater mInflater;

    public BirdAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public BirdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.bird_list_item, parent, false);
        return new BirdViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BirdViewHolder holder, int position) {
        if (mBirds != null) {
            Bird current = mBirds.get(position);
            String name = current.getBirdName();
            String sightingDate = current.getBirdSightingDate();
            String locationDescription = current.getBirdLocationDescription();
            //holder.birdItemView.setText(name);
            String displayText = name + "\nSighting Date: " + sightingDate + "\nLocation: " + locationDescription;
            holder.birdItemView.setText(displayText);
            Repository repository = new Repository((Application) context.getApplicationContext());
            repository.insertLog("System", "Bind Data", "Bound data for bird: " + name + " at position: " + position);
        } else {
            holder.birdItemView.setText("No bird name");
        }
    }

    public void setBirds(List<Bird> birds) {
        mBirds = birds;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mBirds.size();
    }
}
