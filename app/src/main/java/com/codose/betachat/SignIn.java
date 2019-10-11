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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class SignIn extends AppCompatActivity {
    private EditText mLogMail, mPassword;
    private View log_user;
    private TextView new_user, sign_txt, forgot;
    private ProgressDialog logProgress;

    private DatabaseReference mDatabaseRef;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mLogMail = findViewById(R.id.log_email);
        mPassword = findViewById(R.id.log_pass);
        log_user = findViewById(R.id.log_user);
        new_user = findViewById(R.id.new_user);
        sign_txt = findViewById(R.id.register_text);
        Typeface face = ResourcesCompat.getFont(this,R.font.pacifico);
        sign_txt.setTypeface(face);
        forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgot = new Intent(getApplicationContext(),ForgotPassword.class);
                startActivity(forgot);
                finish();
            }
        });

        logProgress = new ProgressDialog(SignIn.this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_new = new Intent(getApplicationContext(),Register.class);
                startActivity(reg_new);
                finish();
            }
        });



        log_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLogMail.getText().toString();
                String password = mPassword.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ){
                    Toast.makeText(SignIn.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else{
                    logProgress.setTitle("Signing in...");
                    logProgress.setMessage("Please wait while we check your credentials");
                    logProgress.setCanceledOnTouchOutside(false);
                    logProgress.show();
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            logProgress.dismiss();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            final String Cuid = currentUser.getUid();
                            if (currentUser.isEmailVerified()){



                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if (!task.isSuccessful()) {

                                                    return;
                                                }

                                                // Get new Instance ID token
                                                String token = task.getResult().getToken();
                                                mDatabaseRef.child(Cuid).child("device_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });

                                            }
                                        });

                                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity(main);
                                finish();
                            }else{
                                mAuth.signOut();
                                Toast.makeText(getApplicationContext()
                                        , "Email not Verified, Please check your mail for Verification steps",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            logProgress.hide();;
                            Toast.makeText(getApplicationContext()
                                    , "Username or Password does not match",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}
