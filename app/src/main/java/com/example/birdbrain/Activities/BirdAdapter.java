package com.example.birdbrain.Activities;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdbrain.Entities.Bird;
import com.example.birdbrain.R;

import java.util.List;

public class BirdAdapter extends RecyclerView.Adapter<BirdAdapter.BirdViewHolder> {

    class BirdViewHolder extends RecyclerView.ViewHolder {
        private final TextView birdItemView;
        private final TextView hotelItemView;
        private final TextView startDateItemView;
        private final TextView endDateItemView;

        private BirdViewHolder(View itemView) {
            super(itemView);
            birdItemView = itemView.findViewById(R.id.textView);
            hotelItemView = itemView.findViewById(R.id.birdhotel);
            startDateItemView = itemView.findViewById(R.id.birdstartdate);
            endDateItemView = itemView.findViewById(R.id.birdenddate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Bird current = mBirds.get(position);
                    Intent intent = new Intent(context, BirdDetails.class);
                    intent.putExtra("id", current.getBirdID());
                    intent.putExtra("name", current.getBirdName());
                    intent.putExtra("hotel", current.getBirdHotel());
                    intent.putExtra("birdStartDate", current.getBirdStartDate());
                    intent.putExtra("birdEndDate", current.getBirdEndDate());

                    context.startActivity(intent);
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
            String hotel = current.getBirdHotel();
            String startDate = current.getBirdStartDate();
            String endDate = current.getBirdEndDate();
            //holder.birdItemView.setText(name);
            String displayText = name + "\nHotel: " + hotel + "\nStart Date: " + startDate + "\nEnd Date: " + endDate;
            holder.birdItemView.setText(displayText);
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
