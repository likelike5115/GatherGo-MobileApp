package edu.northeastern.gathergo;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventCreationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventCreationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<EventType> arr;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String userId;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;
    private EditText name;
    private EditText description;
    private EditText time;
    private EditText address;
    private EditText moreInfo;
    private EditText requirement;
    private Spinner type;
    private Button create;
    private Button upload;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private double selectedLatitude;
    private double selectedLongitude;
    private ArrayList<String> eventPictureUrls;

    public EventCreationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventCreationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventCreationFragment newInstance(String param1, String param2) {
        EventCreationFragment fragment = new EventCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void upload() {

        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .build());
    }

    public void init(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        name = view.findViewById(R.id.eventname);
        description = view.findViewById(R.id.description);
        time = view.findViewById(R.id.time2);
        address = view.findViewById(R.id.address2);
        requirement = view.findViewById(R.id.requirement);
        moreInfo = view.findViewById(R.id.moreInfo);
        upload = view.findViewById(R.id.upload_pic);
        create = view.findViewById(R.id.create_event);
        type = view.findViewById(R.id.type_spinner);
        arr = new ArrayList<>();
        eventPictureUrls = new ArrayList<>();
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                arr.add(EventType.values()[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAddress();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();
                upload.setEnabled(false);
                upload();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });
    }

    public void post() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            Toast.makeText(getContext(), "Please provide an event name.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description.getText().toString())) {
            Toast.makeText(getContext(), "Please provide an event description.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address.getText().toString())) {
            Toast.makeText(getContext(), "Please select an event address.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(time.getText().toString())) {
            Toast.makeText(getContext(), "Please provide an event time.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(requirement.getText().toString())) {
            Toast.makeText(getContext(), "Please specify the event requirement.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(moreInfo.getText().toString())) {
            Toast.makeText(getContext(), "Please provide more information on the event.", Toast.LENGTH_SHORT).show();
        } else if (eventPictureUrls.isEmpty()) {
            Toast.makeText(getContext(), "Please upload at least one picture.", Toast.LENGTH_SHORT).show();
        } else {
            EventModel e = new EventModel(name.getText().toString(), description.getText().toString(), address.getText().toString(), selectedLatitude, selectedLongitude, arr, time.getText().toString(), userId, 0, moreInfo.getText().toString(), requirement.getText().toString(), eventPictureUrls);
            String key = mDatabase.child("Event").push().getKey();
            Log.d(TAG, "post: key " + key);
            Map<String, Object> postValue = e.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            String newKey = String.valueOf(e.getEventId());
            Log.d(TAG, "post: newKey " + newKey);
            childUpdates.put("/Event/" + newKey, postValue);
            mDatabase.updateChildren(childUpdates);
            Toast.makeText(this.getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
            name.setText("");
            arr.clear();
            description.setText("");
            address.setText("");
            time.setText("");
            requirement.setText("");
            moreInfo.setText("");
        }
    }

    public void selectAddress() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(requireActivity());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place);
                selectedLatitude = place.getLatLng().latitude;
                selectedLongitude = place.getLatLng().longitude;
                address.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
                upload.setEnabled(true);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "User canceled autocomplete");
                upload.setEnabled(true);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get UserId from home activity
        HomeActivity activity = (HomeActivity) getActivity();
        userId = activity.getUserId();

        //connect to database
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images");
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        double d = 10000 * Math.random();
                        UploadTask up = imagesRef.child(String.valueOf(d)).putFile(uri);
                        Task<Uri> urlTask = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                // eventPictureUrls.add(imagesRef.child(String.valueOf(d)).getDownloadUrl().toString());
                                // Continue with the task to get the download URL
                                return imagesRef.child(String.valueOf(d)).getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    eventPictureUrls.add(String.valueOf(downloadUri));
                                    Toast.makeText(activity, "Picture uploaded successfully!", Toast.LENGTH_SHORT).show();
                                    upload.setEnabled(true);
                                } else {
                                    Toast.makeText(activity, "Picture failed to be uploaded. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }

                });

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_creation, container, false);
        init(v);
        return v;
    }
}