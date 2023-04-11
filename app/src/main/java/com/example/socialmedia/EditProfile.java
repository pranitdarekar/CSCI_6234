package com.example.socialmedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfile extends AppCompatActivity {

    EditText nameEditText, bioEditText, locationEditText, numberEditText;
    ImageView profilePictureImageView;
    Button saveButton, updatePictureButton;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    StorageReference storageRef;

    ActivityResultLauncher<String> launcher;

    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_profile);

        saveButton = findViewById(R.id.save_button);
        profilePictureImageView = findViewById(R.id.profile_picture_image_view);
        updatePictureButton = findViewById(R.id.update_picture_button);
        nameEditText = findViewById(R.id.name_edit_text);
        bioEditText = findViewById(R.id.bio_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        numberEditText = findViewById(R.id.number_edit_text);
        toolbar = findViewById(R.id.toolbar_edit_profile);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        String userId = firebaseAuth.getCurrentUser().getUid();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + userId);


        // Set up the activity result launcher to pick an image
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        // Get the selected image URI
                        Uri imageUri = result;

                        // Upload the image to Firebase Storage
                        UploadTask uploadTask = profilePicRef.putFile(imageUri);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get the download URL of the uploaded image
                                profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Save the download URL to the user's profile in Firebase Realtime Database
                                        databaseReference.child("users").child(userId).child("profilePicture").setValue(uri.toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Profile picture updated successfully
                                                        Toast.makeText(EditProfile.this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Failed to update the profile picture
                                                        Toast.makeText(EditProfile.this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to upload the image
                                Toast.makeText(EditProfile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        // Set OnClickListener on Update Picture button
        updatePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the launcher to pick an image
                launcher.launch("image/*");
            }
        });



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
                            numberEditText.getText().toString().trim(),
                            fbUser.getUid(),
                            fbUser.getEmail());

                    databaseReference.child("users").child(userId).setValue(user);
                    Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        SubMenu subMenu = menu.findItem(R.id.main_menu).getSubMenu();
        for (int i = 0; i < subMenu.size(); i++) {
            MenuItem subMenuItem = subMenu.getItem(i);
            subMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // Handle submenu item tap
                    switch (item.getItemId()) {
                        case R.id.add_posts:
                            Intent addPostIntent = new Intent(getApplicationContext(), AddPost.class);
                            startActivity(addPostIntent);
                            finish();
                            break;
                        case R.id.logout:
                            FirebaseAuth.getInstance().signOut();
                            Intent logoutIntent = new Intent(getApplicationContext(), Login.class);
                            startActivity(logoutIntent);
                            finish();
                            break;
                        case R.id.edit_profile:
                            Intent editProfileIntent = new Intent(getApplicationContext(), EditProfile.class);
                            startActivity(editProfileIntent);
                            finish();
                            break;
                        case R.id.search_user:
                            Intent searchUserIntent = new Intent(getApplicationContext(), SearchUser.class);
                            startActivity(searchUserIntent);
                            finish();
                            break;
                    }
                    return true;
                }
            });
        }
        return true;
    }
}
