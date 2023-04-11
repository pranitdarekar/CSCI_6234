package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MyPage extends AppCompatActivity {

    Toolbar toolbar;
    TextView bioTextView;
    String userId;
    ImageView profilePictureImage;
    RecyclerView recyclerView;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_page);

        toolbar = findViewById(R.id.toolbar_my_page);
        bioTextView = findViewById(R.id.bio_text_view);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profilePictureImage = findViewById(R.id.profile_image_view);

        recyclerView = findViewById(R.id.images_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ImageAdapter();
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        updateProfileComponents();

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
                        case R.id.my_page:
                            Intent myPageIntent = new Intent(getApplicationContext(), MyPage.class);
                            startActivity(myPageIntent);
                            finish();
                            break;
                    }
                    return true;
                }
            });
        }
        return true;
    }

    private void updateProfileComponents() {
        updateBioText();
        updateProfilePicture();
        displayPosts();
    }

    private void updateBioText() {
        DatabaseReference bioRef = FirebaseDatabase.getInstance().getReference("users/" + userId + "/bio");

        bioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String bio = dataSnapshot.getValue(String.class);
                    bioTextView.setText(bio);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error reading bio from database", databaseError.toException());
            }
        });
    }

    private void updateProfilePicture() {
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("profile_pictures/" + userId);
        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = uri.toString();
                Glide.with(getApplicationContext())
                        .load(downloadUrl)
                        .into(profilePictureImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to generate download URL
                Log.e("FirebaseError", "Error getting download URL: " + e.getMessage());
            }
        });
    }

    private void displayPosts() {
        FirebaseDatabase.getInstance().getReference().child("uploads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list to avoid duplicates
                adapter.clear();

                // Loop through all the images in the database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    Map<String, Object> userMap = (Map<String, Object>) snapshot.getValue();

                    // Create a JSONObject from the userMap
                    JSONObject postsObj = new JSONObject(userMap);
                    try {
                        if (postsObj.has("imageUrl") && Objects.equals(userId, postsObj.getString("uid"))) {
                            Image image = new Image(postsObj.getString("imageUrl"));
                            adapter.addImage(image);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private List<Image> mImages;

        public ImageAdapter() {
            mImages = new ArrayList<>();
        }

        public void clear() {
            mImages.clear();
            notifyDataSetChanged();
        }

        public void addImage(Image image) {
            mImages.add(image);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_image, parent, false);
            return new ImageAdapter.ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
            Image image = mImages.get(position);

            // Load the image using Glide or Picasso
            Glide.with(holder.itemView.getContext())
                    .load(image.getUrl())
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return mImages.size();
        }

        private class ImageViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_view);
            }
        }
    }

    private class Image {
        private String url;

        public Image(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}