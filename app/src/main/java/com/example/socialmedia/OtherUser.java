package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public class OtherUser extends AppCompatActivity {

    EditText bioEditText;
    Button followButton;

    Toolbar toolbar;
    String currentUserEmail, otherUserEmail, otherUserId, currentUserId;
    boolean isFollowing;
    DatabaseReference followRequestsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_other_user);

        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        otherUserEmail = getIntent().getStringExtra("searched_username");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        followRequestsRef = FirebaseDatabase.getInstance().getReference().child("FollowRequests");

        updateFollowRequest();

        bioEditText = findViewById(R.id.bio_edit_text);
        followButton = findViewById(R.id.follow_button);
        toolbar = findViewById(R.id.toolbar_other_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(otherUserEmail);

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the current user is not following the visited user, send a follow request
                if (!isFollowing) {
                    // Add a new child node to the "follow requests" node with the following data:
                    // - The ID of the current user as the key
                    // - The ID of the visited user as the child key
                    // - The value "Pending" to indicate that the follow request is pending
                    followRequestsRef.child(currentUserId).child(otherUserId).setValue("Pending")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(OtherUser.this, "Follow request sent", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(OtherUser.this, "Failed to send follow request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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

    private void updateFollowRequest() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    Map<String, Object> userMap = (Map<String, Object>) snapshot.getValue();

                    // Create a JSONObject from the userMap
                    JSONObject userObject = new JSONObject(userMap);

                    // Check if the "email" field exists and has the desired value
                    try {
                        if (userObject.has("email") && Objects.equals(otherUserEmail, userObject.getString("email"))) {
                            otherUserId = key;
                            // Check if the current user is following the visited user
                            followRequestsRef.child(currentUserId).child(otherUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String followState = snapshot.getValue(String.class);
                                        if (followState.equals("Accepted")) {
                                            // The current user is following the visited user
                                            isFollowing = true;
                                            followButton.setText("Following");
                                        } else if (followState.equals("Pending")) {
                                            // The current user has sent a follow request to the visited user
                                            isFollowing = false;
                                            followButton.setText("Request Sent");
                                        } else {
                                            // The current user is not following the visited user
                                            isFollowing = false;
                                            followButton.setText("Follow");
                                        }
                                    } else {
                                        // The current user is not following the visited user
                                        isFollowing = false;
                                        followButton.setText("Follow");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("ProfileActivity", "Failed to read follow request", error.toException());
                                }
                            });
                            break;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    // print the key and value
                    Log.d("FirebaseData", "Key: " + key + " Value: " + userMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error reading data", databaseError.toException());
            }
        });
    }

}