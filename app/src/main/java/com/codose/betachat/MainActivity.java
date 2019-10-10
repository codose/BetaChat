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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private SectionsAdapter mSectionsAdapter;
    private TabLayout mTabLayout;

    private DatabaseReference mUserRef;

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

        if(mAuth.getCurrentUser()!=null){
            mUserRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(mAuth.getCurrentUser().getUid());
            mSectionsAdapter = new SectionsAdapter(getSupportFragmentManager());
            mViewpager.setAdapter(mSectionsAdapter);
            mTabLayout.setupWithViewPager(mViewpager);
            mViewpager.setCurrentItem(1);
        }



    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null || !currentUser.isEmailVerified()){
            mAuth.signOut();
            launchStart();
        }else{
            mUserRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null || !currentUser.isEmailVerified()){
            mAuth.signOut();
            launchStart();
        }else{
            mUserRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
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
                mUserRef = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(mAuth.getCurrentUser().getUid());
                mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
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
