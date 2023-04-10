package com.example.socialmedia;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    EditText nameEditText, bioEditText, locationEditText, numberEditText;
    ImageView profilePictureImageView;
    Button saveButton;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_profile);

        saveButton = findViewById(R.id.save_button);
        profilePictureImageView = findViewById(R.id.profile_picture_image_view);
        nameEditText = findViewById(R.id.name_edit_text);
        bioEditText = findViewById(R.id.bio_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        numberEditText = findViewById(R.id.number_edit_text);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser fbUser = firebaseAuth.getCurrentUser();
                if (fbUser != null) {
                    String userId = fbUser.getUid();
                    User user = new User(
                            nameEditText.getText().toString().trim(),
                            bioEditText.getText().toString().trim(),
                            locationEditText.getText().toString().trim(),
                            numberEditText.getText().toString().trim());

                    databaseReference.child("users").child(userId).setValue(user);
                    Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
