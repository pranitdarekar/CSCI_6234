package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SearchUser extends AppCompatActivity {

    EditText usernameEditText;
    Button submitButton;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_user);

        // Get references to the EditText and Button views
        usernameEditText = findViewById(R.id.usernameEditText);
        submitButton = findViewById(R.id.submitBtn);
        toolbar = findViewById(R.id.toolbar_search_user);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Get a reference to the Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        // Set an OnClickListener for the Button view
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(SearchUser.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Objects.equals(firebaseAuth.getCurrentUser().getEmail(), username)) {
                    Toast.makeText(SearchUser.this, "Search for other username", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if user exists in Firebase Auth table
                firebaseAuth.fetchSignInMethodsForEmail(username)
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()) {
                                    SignInMethodQueryResult result = task.getResult();
                                    if (result != null && result.getSignInMethods() != null
                                            && result.getSignInMethods().contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                                        // User exists
                                        Intent intent = new Intent(SearchUser.this, OtherUser.class);
                                        intent.putExtra("seachedUsername", username);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // User does not exist
                                        Toast.makeText(SearchUser.this, "User does not exist", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Error occurred
                                    Toast.makeText(SearchUser.this, "Error occurred: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
