package com.blackbrick.wecare.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbrick.wecare.R;
import com.blackbrick.wecare.fragments.FoodFragment;
import com.blackbrick.wecare.fragments.BloodFragment;
import com.blackbrick.wecare.fragments.BooksFragment;
import com.blackbrick.wecare.fragments.ClothesFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private BottomNavigationView mainBottomNav;

    private String current_userID;

    private BloodFragment bloodFragment;
    private BooksFragment booksFragment;
    private FoodFragment foodFragment;
    private ClothesFragment clothesFragment;

    private ImageView mainActivityImage;
    private ImageView coronaImage;
    private LinearLayout requestLayout;
    private LinearLayout donationDriveLayout;
    private ConstraintLayout mainLayout;


    // in this activity we use 4 fragments. fragment corresponds to blood, books, clothes and food
    // fragments are a part of activity. We can say fragments as sub-activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainBottomNav = findViewById(R.id.mainBottomNav);

        bloodFragment = new BloodFragment();
        booksFragment = new BooksFragment();
        foodFragment = new FoodFragment();
        clothesFragment = new ClothesFragment();

        requestLayout = findViewById(R.id.requestDonationLayout);
        donationDriveLayout = findViewById(R.id.donationDriveLayout);
        mainLayout = findViewById(R.id.main_container);

        mainActivityImage = findViewById(R.id.main_activity_image);
        coronaImage = findViewById(R.id.corona_image);

        Glide.with(MainActivity.this).load(this.getResources().getDrawable(R.drawable.main_activity_image)).into(mainActivityImage);
        Glide.with(MainActivity.this).load(this.getResources().getDrawable(R.drawable.main_activity_corona)).into(coronaImage);

        donationDriveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });

        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int color = Color.parseColor("#000000");
                makeBackgroundInvisible();
                switch (item.getItemId()){
                    case R.id.bottom_action_blood:
                        replaceFragment(bloodFragment);
                        return true;
                    case R.id.bottom_action_clothes:
                        replaceFragment(clothesFragment);
                        return true;
                    case R.id.bottom_action_food:
                        replaceFragment(foodFragment);
                        return true;
                    case R.id.bottom_action_books:
                        replaceFragment(booksFragment);
                        return true;
                }
                return false;
            }
        });

    }

    //if any user has not logged out, then automatically login with the user details
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            //if the user has not completed setup activity, then move to setup activity
            current_userID = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else{
                        String error = task.getException().toString();
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout_btn:
                logout();
                return true;
            case R.id.action_settings_btn:
                startActivity(new Intent(MainActivity.this, SetupActivity.class));
                return true;
            case R.id.menu_post_id:
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                return true;
            default:
                return false;
        }
    }

    private void logout(){
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    private void makeBackgroundInvisible(){
        coronaImage.setVisibility(View.INVISIBLE);
        mainActivityImage.setVisibility(View.INVISIBLE);
        requestLayout.setVisibility(View.INVISIBLE);
        donationDriveLayout.setVisibility(View.INVISIBLE);

        TextView text1 = findViewById(R.id.main_activity_text1);
        TextView text2 = findViewById(R.id.main_activity_text2);
        TextView text3 = findViewById(R.id.app_use_heading);
        TextView text4 = findViewById(R.id.app_use1);
        TextView text5 = findViewById(R.id.app_use2);

        text1.setVisibility(View.INVISIBLE);
        text2.setVisibility(View.INVISIBLE);
        text3.setVisibility(View.INVISIBLE);
        text4.setVisibility(View.INVISIBLE);
        text5.setVisibility(View.INVISIBLE);


    }
}
