package com.codose.betachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private EditText mUsername,mEmail,mPassword,mConfirm,mFullname,mPhone;
    private ProgressBar progressBar;
    private View reg_btn;
    private ProgressDialog regProgress, verify;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.pass1);
        mConfirm = findViewById(R.id.pass2);
        mFullname = findViewById(R.id.fullname);
        mPhone = findViewById(R.id.phone);
        reg_btn = findViewById(R.id.reg_user);
        progressBar = findViewById(R.id.progressBar);

        regProgress = new ProgressDialog(Register.this);
        verify = new ProgressDialog(Register.this);

        progressBar.setVisibility(View.GONE);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String confirm = mConfirm.getText().toString();
                String fullname = mFullname.getText().toString();
                String phone = mPhone.getText().toString();



                if (TextUtils.isEmpty(username)){
                    mUsername.setError("Please Enter your username");
                    Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(email)){
                    mEmail.setError("Enter your Email");
                    Toast.makeText(getApplicationContext(), "Enter your Email", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)){
                    mPassword.setError("Enter your Password");
                    Toast.makeText(getApplicationContext(), "Enter your Password", Toast.LENGTH_SHORT).show();
                }
                else if (!(password.equals(confirm))){
                    mConfirm.setError("Passwords do not match");
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(fullname)){
                    mFullname.setError("Enter your Full Name");
                    Toast.makeText(getApplicationContext(), "Enter your Full Name", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(phone)){
                    mPhone.setError("Enter your Phone number");
                    Toast.makeText(getApplicationContext(), "Enter your Phone number", Toast.LENGTH_SHORT).show();
                }
                else{
                    newuser(username,email,password,fullname,phone);
                    regProgress.setTitle("Creating new user");
                    regProgress.setMessage("Please wait while we create your account");
                    regProgress.setCanceledOnTouchOutside(false);
                    regProgress.show();
                }
            }
        });
    }

    private void newuser(final String username, String email, String password, final String fullname, final String phone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            String uid = currentUser.getUid();
                            UserProfileChangeRequest profileChange = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();
                            currentUser.updateProfile(profileChange);
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("username",username);
                            userMap.put("fullname",fullname);
                            userMap.put("phone",phone);
                            userMap.put("status","Hello, I'm  using BetaChat app :)");
                            userMap.put("image","default");
                            userMap.put("t_img","default");

                            mDatabase.setValue(userMap);
                            regProgress.dismiss();
                            sendVerificationEmail();
                        }else{
                            regProgress.hide();
                            Toast.makeText(Register.this, "Please try again later",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void sendVerificationEmail() {
        verify.setTitle("Sending Verifiction mail");
        verify.setMessage("You will receive a mail shortly");
        verify.setCanceledOnTouchOutside(false);
        verify.show();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        currentUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "Email Verification Successfully sent. Please check your mail.",
                            Toast.LENGTH_SHORT).show();
                    Intent main = new Intent(getApplicationContext(),SignIn.class);
                    main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    verify.dismiss();
                    mAuth.signOut();
                    startActivity(main);
                    finish();
                }
                else{
                    currentUser.delete();
                    Toast.makeText(Register.this, "Please try again.",
                            Toast.LENGTH_SHORT).show();
                    sendVerificationEmail();
                }
            }
        });
    }
}