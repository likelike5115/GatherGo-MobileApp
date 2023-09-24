package edu.northeastern.gathergo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikedEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikedEventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;

    private ArrayList<String> eventList;
    private EventAdapter adapter1;
    ArrayList<EventModel> events;

    public LikedEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LikedEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LikedEventFragment newInstance(String param1, String param2) {
        LikedEventFragment fragment = new LikedEventFragment();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference userRef = rootRef.child("Users").child("userId");
        recyclerView = view.findViewById(R.id.likedListView);
        Bundle args = getActivity().getIntent().getExtras();
        String theCurrentUser = args.getString("userId");
        DatabaseReference userRef = rootRef.child("Users").child(theCurrentUser).child("likedEvents");
        //String s = userRef.child(theCurrentUser).get().toString();
        eventList = new ArrayList<>();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                events = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String s = postSnapshot.getValue(String.class);
                    eventList.add(s);
                    Log.e("LikedString", "Value is: " + eventList.get(eventList.size() - 1));
                    DatabaseReference eventRef = rootRef.child("Event").child(s);
                    eventRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                EventModel event = task.getResult().getValue(EventModel.class);
                                //Log.e("name", "Value is: " + name);
                                if (event != null) {
                                    //EventModel event = new EventModel(name);
                                    events.add(event);
                                    Log.e("size", "Value is: " + events.size());
                                    adapter1.notifyDataSetChanged();
                                }

                            }
                        }
                    });

                }
                adapter1 = new EventAdapter(events, getContext(), null);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: ");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liked_event, container, false);
    }
}