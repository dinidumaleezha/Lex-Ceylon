package com.dinidu.lexceylon.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDataManager {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private SharedPrefManager prefManager;
    private static final String TAG = "UserDataManager";
    public UserDataManager(Context context) {
        prefManager = new SharedPrefManager(context);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            fetchUserData();
        } else {
            Log.e(TAG, "User not logged in");
        }
    }

    private void fetchUserData() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class); // example

                    if (name != null && email != null) {
                        prefManager.setName(name);
                        prefManager.setEmail(email);
                        prefManager.setUID(mAuth.getCurrentUser().getUid());

                        Log.d(TAG, "User data saved to SharedPref");
                    } else {
                        Log.d(TAG, "Name or email is null in snapshot");
                    }
                } else {
                    Log.d(TAG, "Snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
}
