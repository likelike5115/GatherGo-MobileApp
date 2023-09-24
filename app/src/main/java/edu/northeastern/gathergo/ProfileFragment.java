package edu.northeastern.gathergo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    //    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
    private ImageButton iBtnProfileImage;
    private Button confirmButton;
    private Button cancelButton;
    private String currentImageId;
    private String currentUserId;
    private String currentUserName;
    private String currentUserEmail;
    private EditText userNameET;
    private EditText userEmailET;
    private EditText userPhoneET;
    private EditText userAddressET;
    private EditText userDescriptionET;


    private String phoneNumber;
    private String address;
    private String description;

    private boolean isAccountEditable;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Bundle args = getActivity().getIntent().getExtras();
        currentUserId = args.getString("userId");
        iBtnProfileImage = getView().findViewById(R.id.ibtn_profile_image);
        isAccountEditable = args.getBoolean("IS_ACCOUNT_EDITABLE", true);

        userNameET = getView().findViewById(R.id.username);
        userEmailET = getView().findViewById(R.id.email);
        userPhoneET = getView().findViewById(R.id.phone);
        userAddressET = getView().findViewById(R.id.address);
        userDescriptionET = getView().findViewById(R.id.Description_input);


        confirmButton = getView().findViewById(R.id.confirm_button);
        cancelButton = getView().findViewById(R.id.cancel_button);
        preLoadingAccount(currentUserId);


//        iBtnProfileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(ProfileFragment.this, selectProfileImage.class);
////                intent.putExtra(INTENT_EXTRA_USER_ID, currentUserId);
////                startActivity(intent);
//            }
//        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = userPhoneET.getText().toString();
                address = userAddressET.getText().toString();
                description = userDescriptionET.getText().toString();
                currentUserName = userNameET.getText().toString();
                Log.d("profile", description);
                String newAvatarId;
                if (args.getString("avatarId") != null) {
                    newAvatarId = args.getString("avatarId");
                } else {
                    newAvatarId = currentImageId;
                }
                User user = new User(currentUserId, currentUserName, " ", newAvatarId, currentUserEmail, phoneNumber, address, description);


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("Users");

                userRef.child(currentUserId).setValue(user);
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("userId", currentUserId);
                startActivity(intent);
                Toast.makeText(getContext(), "Profile submitted successfully", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("userId", currentUserId);
                startActivity(intent);
            }
        });

        if (!isAccountEditable) {
            iBtnProfileImage.setEnabled(false);
            userNameET.setEnabled(false);
            userEmailET.setEnabled(false);
            userPhoneET.setEnabled(false);
            userAddressET.setEnabled(false);
            userDescriptionET.setEnabled(false);
            confirmButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void preLoadingAccount(String currentUserId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef;
        userRef = database.getReference("Users");

        userRef.child(currentUserId).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                GenericTypeIndicator<User> genericTypeIndicator = new GenericTypeIndicator<User>() {
                };
                User user = task.getResult().getValue(genericTypeIndicator);
                preBuildingUI(user);
            }
        });
    }

    private void preBuildingUI(User user) {
        currentImageId = user.getAvatarId();
        currentUserName = user.getUserName();
        currentUserEmail = user.getEmail();
        Bundle args = getActivity().getIntent().getExtras();
        if (args.getString("avatarId") != null) {
            String tempAvatarId = args.getString("avatarId");
            preBuildingAvatar("avatar_1");
        } else {
            preBuildingAvatar("avatar_1");
        }

        userNameET.setText(currentUserName);
        userEmailET.setText(currentUserEmail);
        phoneNumber = user.getPhoneNumber();
        address = user.getAddress();
        description = user.getDescription();


        if (phoneNumber.length() > 1) {
            userPhoneET.setText(phoneNumber);
        }

        if (address.length() > 1) {
            userAddressET.setText(address);
        }

        if (description.length() > 1) {
            userDescriptionET.setText(description);
        }
    }

    private void preBuildingAvatar(String currentImageId) {

            iBtnProfileImage.setImageResource(R.drawable.avatar_1);

    }
}