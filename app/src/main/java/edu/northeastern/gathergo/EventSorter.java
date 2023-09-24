package edu.northeastern.gathergo;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EventSorter {
    public static void sortEventsByDistance(ArrayList<EventModel> events, double currentLatitude, double currentLongitude) {
        Collections.sort(events, new Comparator<EventModel>() {
            @Override
            public int compare(EventModel event1, EventModel event2) {
                float[] results1 = new float[1];
                Location.distanceBetween(currentLatitude, currentLongitude, event1.getEventLatitude(), event1.getEventLongitude(), results1);
                double distance1 = results1[0] / 1000; // Convert distance to kilometers

                float[] results2 = new float[1];
                Location.distanceBetween(currentLatitude, currentLongitude, event2.getEventLatitude(), event2.getEventLongitude(), results2);
                double distance2 = results2[0] / 1000; // Convert distance to kilometers

                return Double.compare(distance1, distance2);
            }
        });
        Log.d("EventSorter", "Event List: " + events);
    }
}
