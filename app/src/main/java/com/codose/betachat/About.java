package com.codose.betachat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class About extends AppCompatActivity {

    private FrameLayout twitter, whatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.about_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");

        twitter = findViewById(R.id.Frame2);
        whatsapp = findViewById(R.id.Frame3);

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent("android.intent.action.VIEW", Uri.parse("https://twitter.com/codose_"));
                startActivity(view);
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inview = new Intent("android.intent.action.VIEW", Uri.parse("https://wa.me/+2348165757132"));
                startActivity(inview);
            }
        });
    }
}
