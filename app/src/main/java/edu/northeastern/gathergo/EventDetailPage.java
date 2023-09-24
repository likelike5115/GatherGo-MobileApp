package edu.northeastern.gathergo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class EventDetailPage extends AppCompatActivity {
    Button likeButton;
    Button registerButton;
    Button shareButton;
    boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_event_detail);

        // Get the event ID from the Intent
        int eventId = getIntent().getIntExtra("event_id", -1);
        String eventName = getIntent().getStringExtra("event_name");
        String eventTime = getIntent().getStringExtra("event_time");
        String eventLocation = getIntent().getStringExtra("event_location");
        String eventDescription = getIntent().getStringExtra("event_description");
        String eventImageUrl = getIntent().getStringExtra("event_image");
        String theCurrentUser = getIntent().getStringExtra("userId");
        String eventMoreInfo = getIntent().getStringExtra("more_info");

        ImageView eventImageView = findViewById(R.id.event_image);
        Picasso.get().load(eventImageUrl).into(eventImageView);

        Picasso.get().load(eventImageUrl)
                .placeholder(R.drawable.border)
                .error(R.drawable.border)
                .into(eventImageView);

        Log.d(TAG, "onCreate: eventID" + eventId);
        Log.d(TAG, "onCreate: eventName" + eventName);
        // Retrieve the selected event from your data source
        //EventModel event = getEventModelById(eventId);

        // Update the UI to display the details of the selected event
        TextView eventNameTextView = findViewById(R.id.event_name);
        TextView eventDateTextView = findViewById(R.id.date_time);
        TextView eventLocationTextView = findViewById(R.id.location);
        TextView eventDescriptionTextView = findViewById(R.id.event_description);
        TextView eventMoreInfoTextView = findViewById(R.id.more_info);

        EventModel theEvent = (EventModel) getIntent().getSerializableExtra("event");
        Log.d(TAG, "onCreate: theEvent " + theEvent.getEventName());

        // Set the values for the UI elements
        eventNameTextView.setText(eventName);
        eventDateTextView.setText(eventTime);
        eventLocationTextView.setText(eventLocation);
        eventDescriptionTextView.setText(eventDescription);
        eventMoreInfoTextView.setText(eventMoreInfo);
        //eventParticipantsTextView.setText("Number of Participants: " + theEvent.getNumOfParticipants());

        String strEventId = String.valueOf(theEvent.getEventId());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference childRef = ref.child("Event");
        boolean regTester = childRef.child(strEventId).child("registered user: " + theCurrentUser).equals("Registered");
        Log.d(TAG, "onCreate: regTester " + regTester);
        if (childRef.child(strEventId).child("registered user: " + theCurrentUser).equals("Registered")) {
            isRegistered = true;
            registerButton.setText("Unregister");
        }

        shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> {
            // Create a share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this event!");

            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, I thought you might be interested in this event: " + eventName + " at " + eventLocation + " on " + eventTime);

            // Start the share activity
            startActivity(Intent.createChooser(shareIntent, "Share event"));
        });

        likeButton = findViewById(R.id.like_button);
        likeButton.setOnClickListener(v -> {

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userRef = rootRef.child("Users");
            // userRef.child(theCurrentUser).child("likedEvents").setValue(eventId);
            userRef.child(theCurrentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        User u = task.getResult().getValue(User.class);
                        Log.d(TAG, "read from firebase " + u.getUserId());
                        List<String> l = u.getLikedEvents();
                        if (l == null) {
                            l = new ArrayList<>();
                        }
                        l.add(String.valueOf(eventId));
                        u.setLikedEvents(l);
                        userRef.child(theCurrentUser).setValue(u);
                        Log.d(TAG, "read from firebase " + u.getLikedEvents().get(0).toString());
                    }
                }
            });
            Toast likeToast = Toast.makeText(getApplicationContext(), "You liked this event!", Toast.LENGTH_SHORT);
            likeToast.setDuration(Toast.LENGTH_SHORT);
            likeToast.show();
        });


        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            if (!isRegistered) {//current status: unregistered

                theEvent.addNumOfParticipants(theEvent.getNumOfParticipants());
                // Get a reference to the root of the database
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                // Get a reference to the "users" node
                DatabaseReference eventRef = rootRef.child("Event");
                eventRef.child(strEventId).child("numOfParticipants").setValue(theEvent.getNumOfParticipants());
                eventRef.child(strEventId).child("registered user: " + theCurrentUser).setValue("Registered");
                registerButton.setText("Unregister");
                isRegistered = true;

                DatabaseReference rootRegRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userRegRef = rootRegRef.child("Users");
                userRegRef.child(theCurrentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            User u = task.getResult().getValue(User.class);
                            Log.d(TAG, "read from firebase " + u.getUserId());
                            List<String> l = u.getaddedEvents();
                            if (l == null) {
                                l = new ArrayList<>();
                            }
                            l.add(String.valueOf(eventId));
                            u.setAddedEvents(l);
                            userRegRef.child(theCurrentUser).setValue(u);
                            Log.d(TAG, "read from firebase " + u.getaddedEvents().get(0).toString());

                        }
                    }
                });
                Toast registerToast = Toast.makeText(getApplicationContext(), "You registered this event!", Toast.LENGTH_SHORT);
                registerToast.setDuration(Toast.LENGTH_SHORT);
                registerToast.show();


                long startTimeMillis = System.currentTimeMillis();
                // Set the duration of the event in milliseconds (1 hour in this example)
                long durationMillis = 60 * 60 * 1000;
                // Create an intent to create a new calendar event
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.Events.TITLE, eventName)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventTime);

                // Launch the calendar app with the event details pre-populated
                startActivity(intent);
            } else {
                //current status: registered
                theEvent.minusNumOfParticipants(theEvent.getNumOfParticipants());
                // Get a reference to the root of the database
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                // Get a reference to the "users" node
                DatabaseReference eventRef = rootRef.child("Event");
                eventRef.child(strEventId).child("numOfParticipants").setValue(theEvent.getNumOfParticipants());
                eventRef.child(strEventId).child("registered user: " + theCurrentUser).setValue("Canceled");
                registerButton.setText("Register");
                isRegistered = false;

                DatabaseReference rootRegRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userUnregRef = rootRegRef.child("Users");
                userUnregRef.child(theCurrentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            User u = task.getResult().getValue(User.class);
                            Log.d(TAG, "read from firebase " + u.getUserId());
                            List<String> l = u.getaddedEvents();
                            if (l == null) {
                                l = new ArrayList<>();
                            }
                            l.remove(String.valueOf(eventId));
                            u.RemoveRegEvent(String.valueOf(eventId));
                            //userUnregRef.child(theCurrentUser).setValue(u);
                            //Log.d(TAG, "read from firebase " + u.getaddedEvents().get(0).toString());
                        }
                    }
                });
                Toast unregisterToast = Toast.makeText(getApplicationContext(), "You unregistered this event!", Toast.LENGTH_SHORT);
                unregisterToast.setDuration(Toast.LENGTH_SHORT);
                unregisterToast.show();
            }

            Log.d(TAG, "onCreate: registerButton numOfParticipants " + theEvent.getNumOfParticipants());
//            Intent intent = new Intent(this, RegisterEvent.class);
//            intent.putExtra("event", (Serializable) theEvent);
//            startActivity(intent);
        });
    }
}
