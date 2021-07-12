package com.chatin.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.chatin.MainActivity;
import com.chatin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText mDisplayName,mEmail,mPassword;
    private Button mCreateBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;
    private String default_user_image_url = "https://firebasestorage.googleapis.com/v0/b/chatin-b374d.appspot.com/o/profile_images%2Fuser.png?alt=media&token=3e894fda-f7f6-4aa4-b457-feaac5fdacca";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDisplayName = findViewById(R.id.status_input);
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        mCreateBtn = findViewById(R.id.reg_Create_Btn);

        //PROGRESS BAR
        mRegProgress = new ProgressDialog(this);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();

        //TOOLBAR
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create a New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //CREATE ACCOUNT BUTTON
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(!TextUtils.isEmpty(display_name)|| !TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)){
                    mRegProgress.setTitle("Creating Account");
                    mRegProgress.setMessage("Please wait while we create your account.");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    //REGISTER USER ACCOUNT = METHOD
                    register_user(display_name,email,password);
                    //PROGRESS BAR ENABLE
                    mRegProgress.show();
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Please Fill Details to Continue.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register_user(String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);    //CREATES USERS NODE IN DATABASE


                    //CREATING AND STORING USER DATA IN NODE USING HASHMAP

                    HashMap<String,String> usermap = new HashMap<>();
                    usermap.put("name",display_name);
                    usermap.put("status","Hi there I'm using ChatIn.");
                    usermap.put("image",default_user_image_url);
                    usermap.put("thumb_image","default");

                    mDatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                Log.d("TAG","Create User With Email : SUCCESS");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                                Toast.makeText(RegisterActivity.this,"Created Account!",Toast.LENGTH_SHORT).show();
                                Toast.makeText(RegisterActivity.this,"Welcome "+display_name+"!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
                else{
                    mRegProgress.hide();
                    Log.w("TAG","Create User With Email : FAILED");
                    Toast.makeText(RegisterActivity.this,"Authentication Failed.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}