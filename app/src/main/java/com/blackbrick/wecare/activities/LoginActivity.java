package com.blackbrick.wecare.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blackbrick.wecare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;
    private ProgressBar loginProgress;

    private FirebaseAuth mAuth;

    //onCreate method is the first method which is called after an activity is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //savedInstanceState is the previous state.
        //if the activity has not existed before, then it is null

        setContentView(R.layout.activity_login);

        // initialise firebase authentication
        mAuth = FirebaseAuth.getInstance();

        loginEmailText = findViewById(R.id.email_id);
        loginPassText = findViewById(R.id.password_id);
        loginBtn = findViewById(R.id.login_btn_id);
        loginRegBtn = findViewById(R.id.signup_btn_id);
        loginProgress = findViewById(R.id.login_progress_id);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String loginEmail = loginEmailText.getText().toString();
                String loginPass = loginPassText.getText().toString();

                // if both login and password are not empty
                if(!(loginEmail.length() == 0 || loginPass.length() == 0)){
                    // make progressbar visible
                    loginProgress.setVisibility(View.VISIBLE);

                    // listeners are used for capturing events

                    // note that onCompleteListener is used, not onSuccessListener
                    // in onCompleteListener, onComplete method is fired when the task is completed, irrespective of whether
                    // the task is successful or not. In onSuccessListener, the onSuccess method is called when the task is successful
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // finish the current activity
                            }
                            else{
                                // unsuccessful
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    //onStart() method is called after the onCreate() method. This is where the activity becomes visible to the user
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); //finish loginActivity. onDestroy() method is called as this activity is destroyed
        }
    }
}

