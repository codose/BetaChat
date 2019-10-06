package com.codose.betachat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class AccountSettings extends AppCompatActivity {
    private CircularImageView mProfilePic;
    private TextView mPhone, mEmail, mFullname;
    private EditText mUsername, mStatus;
    private ImageView img_edit;
    private DatabaseReference mUserInfo;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private static final int Gallery_pic =1;
    private ProgressBar progressBar;

    private StorageReference mStorage;

    private ProgressDialog mProgress, mProgressUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Toolbar toolbar = findViewById(R.id.set_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        mUsername =  findViewById(R.id.editName);
        mStatus = findViewById(R.id.editStatus);
        mProfilePic = findViewById(R.id.profile_pic);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        mFullname = findViewById(R.id.full_name);
        img_edit = findViewById(R.id.edit_img);
        progressBar = findViewById(R.id.progressBar2);

        mStorage = FirebaseStorage.getInstance().getReference();

        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),Gallery_pic); */

                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountSettings.this);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String mUid = currentUser.getUid();

        mUserInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(mUid);

        mUserInfo.keepSynced(true);

        mUserInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String thumb = dataSnapshot.child("t_img").getValue().toString();
                String name = dataSnapshot.child("fullname").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                mUsername.setText(username);
                mStatus.setText(status);
                mPhone.setText(phone);
                mFullname.setText(name);
                mEmail.setText(currentUser.getEmail());
                if(!image.equals("default")){
                    //Picasso.get().load(thumb).placeholder(R.drawable.headshot).into(mProfilePic);
                    Picasso.get().load(thumb).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.headshot).into(mProfilePic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(thumb).placeholder(R.drawable.headshot).into(mProfilePic);

                        }
                    });
                }
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AccountSettings.this,"Unable to load user info please try again",Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.save_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mSave:
                saveUserInfo();
        }
        return true;
    }

    private void saveUserInfo() {
        mProgress = new ProgressDialog(AccountSettings.this);
        mProgress.setTitle("Updating Profile");
        mProgress.setMessage("User profile is currently updating, Please wait");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        String username = mUsername.getText().toString();
        String status = mStatus.getText().toString();
        UserProfileChangeRequest profileChange = new UserProfileChangeRequest.Builder()
                .setDisplayName(username).build();
        currentUser.updateProfile(profileChange);
        mUserInfo.child("status").setValue(status);
        mUserInfo.child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mProgress.dismiss();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(i);
                    finish();
                }else{
                    mProgress.hide();
                    Toast.makeText(AccountSettings.this, "Please try again",
                            Toast.LENGTH_SHORT).show(); }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressUpload = new ProgressDialog(AccountSettings.this);
                mProgressUpload.setTitle("Uploading Image...");
                mProgressUpload.setMessage("Please wait while image uploads");
                mProgressUpload.setCanceledOnTouchOutside(false);
                mProgressUpload.show();

                final Uri resultUri = result.getUri();
                final File thumb_path = new File(resultUri.getPath());

                final Bitmap thumb_bit = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                String cUid = currentUser.getUid();

                final StorageReference filepath = mStorage.child("profile_img").child(cUid + ".jpg");
                final StorageReference thumb_file = mStorage.child("profile_img").child("thumbs").child(cUid + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String download_uri = uri.toString();
                                    UploadTask uploadTask = thumb_file.putBytes(thumb_byte);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle unsuccessful uploads
                                            mProgressUpload.dismiss();
                                            Toast.makeText(AccountSettings.this,"Upload Error", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            thumb_file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String thumb_uri = uri.toString();
                                                    if(task.isSuccessful()){
                                                        Map updateHash = new HashMap();
                                                        updateHash.put("image",download_uri);
                                                        updateHash.put("t_img",thumb_uri);
                                                        mUserInfo.updateChildren(updateHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mProgressUpload.dismiss();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });

                                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                            // ...
                                        }
                                    });

                                }
                            });

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
