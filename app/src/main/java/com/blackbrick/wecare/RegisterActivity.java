package com.blackbrick.wecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerEmailText;
    private EditText registerPassText;
    private Button registerBtn;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        registerEmailText = findViewById(R.id.register_email_id);
        registerPassText = findViewById(R.id.register_password_id);
        registerBtn = findViewById(R.id.register_btn_id);
        loginProgress = findViewById(R.id.login_progress_id);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = registerEmailText.getText().toString();
                String password = registerPassText.getText().toString();

                if(!(email.length() == 0 || password.length() == 0)){
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                startActivity(setupIntent);
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Error!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser current_user = mAuth.getCurrentUser();

        if(current_user != null){
            sendToMain();
        }

    }
    private void sendToMain(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
