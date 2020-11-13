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

public class RegisterActivity extends AppCompatActivity {
    private EditText registerEmailText;
    private EditText registerPassText;
    private EditText registerConfirmPassText;
    private Button registerBtn;
    private ProgressBar registerProgress;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerEmailText = findViewById(R.id.register_email_id);
        registerPassText = findViewById(R.id.register_password_id);
        registerConfirmPassText = findViewById(R.id.register_confirm_password_id);
        registerBtn = findViewById(R.id.register_btn_id);
        registerProgress = findViewById(R.id.register_progress_id);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = registerEmailText.getText().toString();
                String password = registerPassText.getText().toString();
                String passwordConfirm = registerConfirmPassText.getText().toString();

                if(!(email.length() == 0 || password.length() == 0) || passwordConfirm.length() == 0 || !password.equals(passwordConfirm)){

                    //make progressbar visible
                    registerProgress.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                startActivity(setupIntent);
                                finish();
                            }
                            else{
                                String error = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, error,Toast.LENGTH_SHORT).show();
                            }
                            registerProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }
}
