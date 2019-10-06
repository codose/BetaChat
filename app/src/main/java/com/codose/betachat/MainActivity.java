package com.codose.betachat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private SectionsAdapter mSectionsAdapter;
    private TabLayout mTabLayout;

    private ViewPager mViewpager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Beta Chat");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //tabs
        mViewpager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.main_tab);
        mSectionsAdapter = new SectionsAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mSectionsAdapter);
        mTabLayout.setupWithViewPager(mViewpager);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null || !currentUser.isEmailVerified()){
            mAuth.signOut();
            launchStart();
        }
    }

    private void launchStart() {
        Intent startIntent = new Intent(getApplicationContext(),StartActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(startIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.mLogOut:
                mAuth.signOut();
                launchStart();
                break;
            case R.id.mSettings:
                Intent i = new Intent(this,AccountSettings.class);
                startActivity(i);
                break;
            case R.id.mUsers:
                Intent intent = new Intent(this,AllUsers.class);
                startActivity(intent);
        }

        return true;
    }
}