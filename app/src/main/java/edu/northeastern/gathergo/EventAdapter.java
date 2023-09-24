package edu.northeastern.gathergo;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    ArrayList<EventModel> eventModelArrayList;
    private Context context;
    private String userId;

    public EventAdapter(ArrayList<EventModel> eventModelArrayList, Context context, String userId) {
        this.eventModelArrayList = eventModelArrayList;
        this.context = context;
        this.userId = userId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventName;
        private final TextView eventTime;
        private final ImageView eventPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.eventName);
            eventTime = (TextView) itemView.findViewById(R.id.eventTime);
            eventPicture = (ImageView) itemView.findViewById(R.id.eventPicture);
        }
    }

    public void filterList(ArrayList<EventModel> filteredList) {
        eventModelArrayList = filteredList;
        notifyDataSetChanged();
    }

    public ArrayList<EventModel> getEventModelArrayList() {
        return eventModelArrayList;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        EventModel model = eventModelArrayList.get(position);
        holder.eventName.setText(model.getEventName());
        holder.eventTime.setText(model.getEventTime());

//        Log.d(TAG, "onBindViewHolder position: " + position);
//        Log.d(TAG, "onBindViewHolder: user: " + model.getUser());
//        Log.d(TAG, "onBindViewHolder get host info: " + model.getHostinfo());

        String eventPictureUrl;
        if (model.getEventPictureUrls() != null) {
            eventPictureUrl = model.getEventPictureUrls().get(0);
        } else {
            eventPictureUrl = "https://firebasestorage.googleapis.com/v0/b/gathergo-8373a.appspot.com/o/images%2F1217.1194322377398?alt=media&token=01a9bf7d-a964-4c29-859e-743521d5c67a";
        }
        Glide.with(context)
                .load(eventPictureUrl)
                .placeholder(R.mipmap.ic_placeholder_picture_foreground)
                .fitCenter()
                .into(holder.eventPicture);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch EventDetailsActivity when an item is clicked
                Intent intent = new Intent(context, EventDetailPage.class);
                Log.d(TAG, "onClick: getEventName " + model.getEventName());
                Log.d(TAG, "onClick: getEventId " + model.getEventId());
                Log.d(TAG, "onClick: getHost " + model.getHost());
                Log.d(TAG, "onClick: getCurrentUserId " + userId);

                intent.putExtra("event_id", model.getEventId());
                intent.putExtra("event_name", model.getEventName());
                intent.putExtra("event_time", model.getEventTime());
                intent.putExtra("event_location", model.getEventLocation());
                intent.putExtra("event_description", model.getEventDescription());
                intent.putExtra("event_image", eventPictureUrl);
                intent.putExtra("more_info", model.getMoreEventInfo());
                intent.putExtra("event", (Serializable) model);

                intent.putExtra("host", model.getHost());
                intent.putExtra("userId", userId);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventModelArrayList.size();
    }

}
