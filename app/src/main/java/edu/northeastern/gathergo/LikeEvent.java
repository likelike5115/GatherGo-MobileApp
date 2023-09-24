package edu.northeastern.gathergo;

import static com.android.volley.VolleyLog.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class LikeEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_event);

        EventModel theEvent = (EventModel) getIntent().getSerializableExtra("event");
        Log.d(TAG, "LikedEvent class onCreate: theEvent " + theEvent.getEventName());
    }
}