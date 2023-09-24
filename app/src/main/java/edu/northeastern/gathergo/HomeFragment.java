package edu.northeastern.gathergo;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.List;


public class HomeFragment extends Fragment implements LocationListener {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private DatabaseReference mDatabase;
    private ArrayList<EventModel> eventList, searchedList, filteredList, overlapList;
    private SearchView searchView;
    private Button socialButton, studyButton, gymButton, networkingButton, workshopButton, outdoorButton, indoorButton, othersButton;
    private Button prevButton;
    private View locationEditor;
    private TextView locationContent;
    private LocationManager locationManager;
    private Location location;
    private double currentLatitude, currentLongitude;
    boolean isGPSEnabled;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private String userId;
    private int white, darkGray, green;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        HomeActivity activity = (HomeActivity) getActivity();
        userId = activity.getUserId();
        initComponents(view);
        initPlaceAPI();
        setUpData();
        initLocation();
        initRecyclerView();
        initSearch(view);
        initLocationEditor(view);
        initFilterTabs(view);

        return view;
    }

    private void initPlaceAPI() {
        String apiKey = "AIzaSyC4fqnMLYLzdtxBul3UFa9Gp8BbggxM52U";
        // Initialize the SDK
        Places.initialize(getContext(), apiKey);
    }

    private void setUpData() {
        eventList = new ArrayList<>();
        searchedList = new ArrayList<>();
        filteredList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ChildEventListener cl = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                EventModel e = snapshot.getValue(EventModel.class);
                eventList.add(e);
                searchedList.add(e);
                filteredList.add(e);
                EventSorter.sortEventsByDistance(eventList, currentLatitude, currentLongitude);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                EventModel e = snapshot.getValue(EventModel.class);
                eventList.add(e);
                searchedList.add(e);
                filteredList.add(e);
                EventSorter.sortEventsByDistance(eventList, currentLatitude, currentLongitude);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabase.child("Event").addChildEventListener(cl);
    }

    private void initComponents(View view) {
        recyclerView = view.findViewById(R.id.eventsListView);

        socialButton = view.findViewById(R.id.socialButton);
        studyButton = view.findViewById(R.id.studyButton);
        gymButton = view.findViewById(R.id.gymButton);
        networkingButton = view.findViewById(R.id.networkingButton);
        workshopButton = view.findViewById(R.id.workshopButton);
        outdoorButton = view.findViewById(R.id.outdoorButton);
        indoorButton = view.findViewById(R.id.indoorButton);
        othersButton = view.findViewById(R.id.othersButton);

        white = ContextCompat.getColor(getContext(), R.color.white);
        green = ContextCompat.getColor(getContext(), R.color.teal_700);
        darkGray = ContextCompat.getColor(getContext(), R.color.darkGray);

        locationEditor = view.findViewById(R.id.editLocation);
        locationContent = view.findViewById(R.id.locationContent);
    }

    private void initRecyclerView() {
        Toast.makeText(getContext(), "Locating events near you...", Toast.LENGTH_SHORT).show();
        adapter = new EventAdapter(eventList, getContext(), userId);
        EventSorter.sortEventsByDistance(eventList, currentLatitude, currentLongitude);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initSearch(View view) {
        searchView = (SearchView) view.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                updateListView();
                return false;
            }
        });
    }

    private void search(String text) {
        searchedList = new ArrayList<>();

        for (EventModel event : eventList) {
            if (event.getEventName().toLowerCase().contains(text.toLowerCase())
                    || event.getEventTime().toLowerCase().contains(text.toLowerCase())) {
                searchedList.add(event);
            }
        }
    }

    private void initLocation() {
        try {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            Toast.makeText(getContext(), "Location services are currently unavailable. To use this feature, please ensure that GPS or network location is enabled in your device settings.", Toast.LENGTH_LONG).show();
        } else if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, (LocationListener) this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                Log.d(TAG, "initLocation: currentLatitude: " + currentLatitude + "currentLongitude: " + currentLongitude);
            }
        }
        displayAddress(location);
    }

    private void displayAddress(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String address = ConvertLocation.locationToAddress(getContext(), latitude, longitude);
            locationContent.setText(address);

            Log.d(TAG, "initLocation: current address: " + address);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        displayAddress(location);
        updateListView();

        Log.d(TAG, "initLocation: LocationChange()");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }

        Log.d(TAG, "initLocation: onStop()");
    }

    private void initLocationEditor(View view) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        locationEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(requireActivity());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                currentLatitude = place.getLatLng().latitude;
                currentLongitude = place.getLatLng().longitude;
                locationContent.setText(place.getAddress());
                updateListView();

                Log.d(TAG, "initLocation: select address: " + place.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "User canceled autocomplete");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initFilterTabs(View view) {
        ArrayList<Button> buttonList = new ArrayList<>();
        buttonList.addAll(Arrays.asList(socialButton, studyButton, gymButton, networkingButton, workshopButton, outdoorButton, indoorButton, othersButton));

        for (Button currButton : buttonList) {
            currButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currButton == prevButton) {
                        lookUnselected(prevButton);
                        prevButton = null;
                        filteredList = eventList;
                    } else {
                        if (prevButton != null) {
                            lookUnselected(prevButton);
                        }
                        prevButton = currButton;
                        lookSelected(prevButton);

                        String tag = (String) currButton.getTag();
                        EventType eventType = EventType.valueOf(tag);
                        filter(eventType);
                    }
                    updateListView();
                }
            });
        }
    }

    private void filter(EventType selectedType) {
        filteredList = new ArrayList<>();

        for (EventModel event : eventList) {
            if (event.getEventTypes().contains(selectedType)) {
                filteredList.add(event);
            }
        }
    }

    private void lookSelected(Button parsedButton) {
        parsedButton.setTextColor(white);
        parsedButton.setBackgroundColor(green);
    }

    private void lookUnselected(Button parsedButton) {
        parsedButton.setTextColor(darkGray);
        parsedButton.setBackgroundColor(white);
    }

    private void updateListView() {
        overlapList = new ArrayList<>();

        for (EventModel element : searchedList) {
            if (filteredList.contains(element)) {
                overlapList.add(element);
            }
        }
        EventSorter.sortEventsByDistance(overlapList, currentLatitude, currentLongitude);
        adapter.filterList(overlapList);
    }
}
