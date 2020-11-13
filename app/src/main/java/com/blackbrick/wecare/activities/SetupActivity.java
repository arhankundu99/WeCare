package com.blackbrick.wecare.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blackbrick.wecare.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

//for loading image into circular imageview
import com.bumptech.glide.Glide;

//for cropping image
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

//for circular imageciew
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageURI; //URI(Uniform resource identifier) as its name suggests is used to identify resource(whether it be a page of text, a video or sound clip, a still or animated image, or a program).
    private EditText setupName;
    private Button setupBtn;

    private String userId;
    private ProgressBar progressBar;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        firebaseAuth = FirebaseAuth.getInstance();

        // in this activity, we store the images in firebase storage
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.accountSetupProgressBarID);
        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupBtn = findViewById(R.id.setup_btn);

        userId = firebaseAuth.getCurrentUser().getUid();

        //if user-details already exists in firebase, load image of user in the circular image
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        Toast.makeText(SetupActivity.this, "Account Exists", Toast.LENGTH_LONG).show();

                        String name = task.getResult().getString("user");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);

                        setupName.setText(name);
                        // why this is not working

                        //glide is used for loading image into circular imageview
                        Glide.with(SetupActivity.this).load(image).into(setupImage);
                    }
                }
            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(SetupActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else bringImagePicker();

                }
                else bringImagePicker();
            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final String userName = setupName.getText().toString();

                if(userName.length() == 0){
                    Toast.makeText(SetupActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                }
                else if(mainImageURI == null){
                    Toast.makeText(SetupActivity.this, "Enter your image", Toast.LENGTH_SHORT).show();
                }
                else{
                    userId = firebaseAuth.getCurrentUser().getUid();

                    //get reference to the path where you want to store the image
                    StorageReference image_path = storageReference.child("profile_images").child(userId+".jpg");

                    //add the profile image in storage
                    image_path.putFile(mainImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //task is successful
                            //now add the image download uri to firestore
                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();
                                    //Toast.makeText(SetupActivity.this, downloadUrl, Toast.LENGTH_SHORT).show();

                                    Map<String, String> userMap = new HashMap<>();
                                    userMap.put("name", userName);
                                    userMap.put("image", downloadUri);

                                    //create a collection users and add user details
                                    firebaseFirestore.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SetupActivity.this, "User Settings are updated", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                String error = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, error, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void bringImagePicker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }
    //Override onActivityResult method to get crop result, this method will fire after the bringImagePicker() method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SetupActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
