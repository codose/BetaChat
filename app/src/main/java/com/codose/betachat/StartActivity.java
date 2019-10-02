package com.codose.betachat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private Button reg_btn;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        reg_btn = findViewById(R.id.reg_button);
        login = findViewById(R.id.login_button);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_open = new Intent(getApplicationContext(),Register.class);
                startActivity(reg_open);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log_open = new Intent(getApplicationContext(),SignIn.class);
                startActivity(log_open);
            }
        });
    }
}
