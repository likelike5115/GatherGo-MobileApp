package edu.northeastern.gathergo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navBar;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navBar = findViewById(R.id.navBar);
        navBar.setOnNavigationItemSelectedListener(this);
        navBar.setSelectedItemId(R.id.home);
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("userId") != null) {
            userId = bundle.getString("userId");
        }
    }

    HomeFragment homeFragment = new HomeFragment();
    EventCreationFragment eventCreationFragment = new EventCreationFragment();
    MainProfileFragment profileFragment = new MainProfileFragment();

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;

            case R.id.create:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, eventCreationFragment).commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                return true;
        }
        return false;
    }
}