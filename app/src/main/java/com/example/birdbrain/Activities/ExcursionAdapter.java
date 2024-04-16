package com.example.birdbrain.Activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdbrain.Entities.Excursion;
import com.example.birdbrain.R;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    private List<Excursion> mExcursions;
    private final Context context;
    private final LayoutInflater mInflater;
    class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionItemView;
        private final TextView excursionItemView2;

        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionItemView = itemView.findViewById(R.id.textView2);
            excursionItemView2 = itemView.findViewById(R.id.textView3);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Excursion current = mExcursions.get(position);
                    Intent intent = new Intent(context, ExcursionDetails.class);
                    intent.putExtra("id", current.getExcursionID());
                    intent.putExtra("name", current.getExcursionName());
                    intent.putExtra("birdID", current.getBirdID());
                    intent.putExtra("date", current.getExcursionDate());
                    intent.putExtra("birdStartDate", birdStartDate);
                    intent.putExtra("birdEndDate", birdEndDate);
                    Log.d("ExcursionDetails", "Excursion Adapter: Start Date: " + birdStartDate + ", End Date: " + birdEndDate);
                    context.startActivity(intent);
                }
            });
        }
    }

    private String birdStartDate;
    private String birdEndDate;

    public ExcursionAdapter(Context context, String startDate, String endDate) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.birdStartDate = startDate;
        this.birdEndDate = endDate;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            String name = current.getExcursionName();
            int birdID = current.getBirdID();
            holder.excursionItemView.setText(name);
            holder.excursionItemView2.setText(Integer.toString(birdID));
        } else {
            holder.excursionItemView.setText("No excursion name");
            holder.excursionItemView.setText("No bird id");
        }
    }

    public void setExcursions(List<Excursion> excursions) {
        mExcursions = excursions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mExcursions.size();
    }
}
