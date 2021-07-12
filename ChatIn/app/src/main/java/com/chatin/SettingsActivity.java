package com.chatin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView mDisplayImage;
    private TextView mName,mstatus;
    private Button mChangeStatusBtn,mChangeImageBtn;
    private Toolbar mToolbar;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog mProgessDialog;

    //FIREBASE
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = findViewById(R.id.settings_display_name);
        mstatus = findViewById(R.id.settings_status);
        mDisplayImage = findViewById(R.id.settings_image);

        mChangeImageBtn = findViewById(R.id.settings_change_image_btn);
        mChangeStatusBtn = findViewById(R.id.settings_change_status_btn);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                //STORING DATA IN VARIABLES
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String thumb_image = snapshot.child("thumb_image").getValue().toString();
                mName.setText(name);
                mstatus.setText(status);

                //TODO CODE UPDATED LAST ON HERE - 1 JULY 2021 5:35AM
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(mDisplayImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).into(mDisplayImage);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });

        //STATUS BUTTON
        mChangeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mstatus.getText().toString();
                Intent statusIntent = new Intent(SettingsActivity.this,StatusActivity.class);
                statusIntent.putExtra("status_value",status_value);
                startActivity(statusIntent);
            }
        });

        //IMAGE BUTTON
        mChangeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
            }
        });
    }
    //CROP ACTIVITY
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            //STRING VALUE OF IMAGE
            Uri imageUri = data.getData();
            //CROP IMAGE ACTIVITY
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        //FOR CHECKING IF REQUESTCODE IS PASSED THROUGH THE CROP ACTIVITY AND IS FROM THE CROP ACTIVITY. TO GET URI DATA OF IMAGE.
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //PROGRESS DIALOG BOX
                mProgessDialog = new ProgressDialog(SettingsActivity.this);
                mProgessDialog.setTitle("Uploading Image");
                mProgessDialog.setMessage("Please Wait while we Upload the Image.");
                mProgessDialog.setCanceledOnTouchOutside(false);
                mProgessDialog.show();

                Uri resultUri = result.getUri();

                String Current_User_ID = mCurrentUser.getUid();

                StorageReference filepath = mImageStorage.child("profile_images").child(Current_User_ID + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri imageUrl =  uri;
                                String imageUrlData = imageUrl.toString();
                                mUserDatabase.child("image").setValue(imageUrlData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull  Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mProgessDialog.dismiss();
                                            Toast.makeText(SettingsActivity.this, "Image Uploaded Successfully.", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            mProgessDialog.hide();
                                            Toast.makeText(SettingsActivity.this, "Error in Uploading Image. Please try again in a while.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}