package com.vetal.tennispartner.adaptersAndOthers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by vitaliakbarov on 23/09/2017.
 */



public class FirebaseDownloadThread extends Thread {
    private String androidId;
    private DatabaseReference mDatabase;
    private OnDownloadCompleteListener listener;
    private Handler handler;
    private User user;

    public FirebaseDownloadThread(OnDownloadCompleteListener listener, Context context, String androidId){
        this.androidId = androidId;
        this.listener = listener;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        handler = new Handler();
    }

    @Override
    public void run() {

        Query myQuery = mDatabase.child("users").child(androidId);
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("D", dataSnapshot.toString());
                user = new User((DataSnapshot) dataSnapshot);
                // download complete, calls to show the list
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.displayUserProfile(user);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
