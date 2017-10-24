package com.gujjubites;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 9/11/2017.
 */
public class CustomSwiperAdapterEvents extends RecyclerView.Adapter<CustomSwiperAdapterEvents.RecyclerViewHolder> {

    Context ctx;
    private List<newEvents> events = new ArrayList<>();

    public CustomSwiperAdapterEvents(Context ctx, List<newEvents> events)
    {
        this.ctx = ctx;
        this.events = events;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_layout, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        //holder.eventImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(ctx).load(events.get(position).getImg_url()).centerCrop().into(holder.eventImage);
        holder.eventTitle.setText(events.get(position).getTitle());
        holder.eventDescription.setText(events.get(position).getDescription());
        
    }

    public int getItemCount() {
        return events.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventTitle;
        TextView eventDescription;
        

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            eventImage = (ImageView) itemView.findViewById(R.id.event_image);
            eventDescription = (TextView) itemView.findViewById(R.id.event_description);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
        
            }
    }
}