package edu.northeastern.gathergo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
@IgnoreExtraProperties
public class EventModel implements Serializable {

    private int eventId;
    private String eventName;
    private String eventDescription;
    private String eventLocation;
    private double eventLatitude;
    private double eventLongitude;
    private ArrayList<EventType> eventTypes;
    private String eventTime;
    private String host;
    private int numOfParticipants;
    private String moreEventInfo;
    private String attendeeRequirement;
    private ArrayList<String> eventPictureUrls;
    private double distance;
    //private String hostinfo;

    public EventModel(int eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
    }

    public EventModel() {
        //default
    }

    public EventModel(int eventId) {
        this.eventId = eventId;
    }

    public EventModel(String eventName) {
        this.eventName = eventName;
    }

    public EventModel(String eventName, String eventDescription, String eventLocation,
                      double eventLatitude, double eventLongitude,
                      ArrayList<EventType> eventTypes, String eventTime, String host, int numOfParticipants, String moreEventInfo,
                      String attendeeRequirement, ArrayList<String> eventPictureUrls) {
        this.eventId = generateEventId();
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.eventLatitude = eventLatitude;
        this.eventLongitude = eventLongitude;
        this.eventTypes = eventTypes;
        this.eventTime = eventTime;
        this.host = host;
        this.numOfParticipants =numOfParticipants;
        this.moreEventInfo = moreEventInfo;
        this.attendeeRequirement = attendeeRequirement;
        this.eventPictureUrls = eventPictureUrls;
    }

    private static int generateEventId() {
        Random random = new Random();
        int eventId = 10000000 + random.nextInt(90000000);
        return eventId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public ArrayList<EventType> getEventTypes() {
        return eventTypes;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getHost() {

        return host;
    }

    public int getNumOfParticipants() {
        return numOfParticipants;
    }

    public void addNumOfParticipants(int numOfParticipants) {
        this.numOfParticipants = numOfParticipants +1;
    }

    public void minusNumOfParticipants(int numOfParticipants){
        if(numOfParticipants>=1) {
            this.numOfParticipants = numOfParticipants - 1;
        }
    }

    public String getMoreEventInfo() {
        return moreEventInfo;
    }

    public String getAttendeeRequirement() {
        return attendeeRequirement;
    }

    public ArrayList<String> getEventPictureUrls() {
        return eventPictureUrls;
    }

    public double getEventLongitude() {
        return eventLongitude;
    }

    public double getEventLatitude() {
        return eventLatitude;
    }

    // for the sake of testing event sorter:
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("eventId", eventId);
        result.put("eventName", eventName);
        result.put("eventDescription", eventDescription);
        result.put("eventLocation", eventLocation);
        result.put("eventLatitude", eventLatitude);
        result.put("eventLongitude", eventLongitude);
        result.put("eventTypes", eventTypes);
        result.put("eventTime", eventTime);
        result.put("host", host);
        result.put("numOfParticipants", numOfParticipants);
        result.put("moreEventInfo", moreEventInfo);
        result.put("attendeeRequirement", attendeeRequirement);
        result.put("eventPictureUrls", eventPictureUrls);
        return result;
    }
}