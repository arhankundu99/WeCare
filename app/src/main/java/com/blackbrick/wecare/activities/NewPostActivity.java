package com.blackbrick.wecare.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackbrick.wecare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NewPostActivity extends AppCompatActivity{
    private int MAX_LENGTH = 100;

    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;

    private Uri postImageUri = null;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private String category;

    private Spinner spinner;
    private static final String[] paths = {"Select Category", "Food", "Blood", "Clothes", "Books"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        spinner = findViewById(R.id.spinner);

        newPostImage = findViewById(R.id.postImageID);
        newPostDesc = findViewById(R.id.postdesc_textID);
        newPostBtn = findViewById(R.id.post_btnID);

        spinner.setPrompt("Select Category");
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(NewPostActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                              @Override
                                              public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                                  switch (position) {
                                                      case 1:
                                                          //blood fragment
                                                          category = "food";
                                                          break;
                                                      case 2:
                                                          //clothes fragment
                                                          category = "blood";
                                                          break;
                                                      case 3:
                                                          // food fragment
                                                          category = "clothes";
                                                          break;
                                                      case 4:
                                                          // books fragment
                                                          category = "books";
                                                          break;

                                                  }
                                              }

                                              @Override
                                              public void onNothingSelected(AdapterView<?> adapterView) {

                                              }
                                          });

                newPostImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setMinCropResultSize(512, 512)
                                .setAspectRatio(1, 1)
                                .start(NewPostActivity.this);
                    }
                });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String desc = newPostDesc.getText().toString();

                if(!(desc.length() == 0 || postImageUri == null)){
                    String randomName = random();

                    StorageReference filePath = storageReference.child("post_images").child(randomName);
                    filePath.putFile(postImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();
                                    String user_id = mAuth.getCurrentUser().getUid();

                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("imageUrl", downloadUri);
                                    postMap.put("desc", desc);
                                    postMap.put("userId", user_id);
                                    postMap.put("timeStamp", FieldValue.serverTimestamp());

                                    firebaseFirestore.collection("Posts").document(category + " Posts").collection(category).add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                String error = task.getException().toString();
                                                Toast.makeText(NewPostActivity.this, error, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
