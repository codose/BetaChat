package com.codose.betachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private EditText email;
    private TextView forgot_text;
    private View forgot_button;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email_address);
        forgot_text = findViewById(R.id.forgot_text);
        forgot_button = findViewById(R.id.forgot_btn);

        Typeface face = ResourcesCompat.getFont(this,R.font.pacifico);
        forgot_text.setTypeface(face);

        progressDialog = new ProgressDialog(ForgotPassword.this);

        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("You will receive a mail shortly.");
        progressDialog.setCanceledOnTouchOutside(false);

        forgot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_address = email.getText().toString();

                if(TextUtils.isEmpty(email_address)){
                    email.setError("Please Enter your email");
                    Toast.makeText(getApplicationContext(), "Please Enter a valid email address", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.show();
                    sendForgotMail(email_address);
                }

            }
        });
    }

    private void sendForgotMail(String email_address) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email_address).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Please check your mail for next steps", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(),SignIn.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Please try again later", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }
            }
        });
    }
}
