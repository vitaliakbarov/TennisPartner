package com.vetal.tennispartner.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.vetal.tennispartner.adaptersAndOthers.CompleteInterface;
import com.vetal.tennispartner.R;
import com.vetal.tennispartner.adaptersAndOthers.ConfigLevel;
import com.vetal.tennispartner.adaptersAndOthers.ConfigLocation;
import com.vetal.tennispartner.adaptersAndOthers.User;
import com.vetal.tennispartner.adaptersAndOthers.UsersAdapter;

import java.util.ArrayList;

public class ResultActivity extends Activity implements AdapterView.OnItemClickListener , CompleteInterface {

    private UsersAdapter adapter;
    private Firebase mRef;
    private ArrayList<User> users = new ArrayList<>();
    private String searchBy;
    private String searchFor;
    private ProgressDialog progressDialog;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        listView = (ListView)findViewById(R.id.list_view_result_activity);
        RelativeLayout result = (RelativeLayout)findViewById(R.id.result_activity);
        listView.setOnItemClickListener(this);

        Intent intent = getIntent();

        searchBy = intent.getStringExtra("SEARCH_BY");
        searchFor = intent.getStringExtra("SEARCH_FOR");
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getText(R.string.searching));
        progressDialog.show();
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://tennispartner-7f25e.firebaseio.com/users");
        Query myQuery = mRef.orderByChild(searchBy).equalTo(searchFor).limitToLast(20);
        myQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    String firstName = (String) userSnapshot.child("firstName").getValue();
                    String lastName = (String) userSnapshot.child("lastName").getValue();
                    String age = (String) userSnapshot.child("age").getValue();
                    String level = (String) userSnapshot.child("level").getValue();
                    Log.d("LEVEL", level);
                    String location = (String) userSnapshot.child("location").getValue();
                    String telephone = (String) userSnapshot.child("telephone").getValue();
                    String gender = (String) userSnapshot.child("gender").getValue();
                    String url = (String)userSnapshot.child("imageUri").getValue();

                    User user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setAge(age);
                    ConfigLevel configLevel = new ConfigLevel(ResultActivity.this,level);
                    user.setLevel(configLevel.fromDB());
                    ConfigLocation configLocation = new ConfigLocation(ResultActivity.this, location);
                    user.setLocation(configLocation.fromDB());
                   // user.setLocation(location);
                    user.setTelephone(telephone);
                    user.setGender(gender);
                    user.setImageUri(url);
                    users.add(user);
                    displayList(users);
                    progressDialog.dismiss();
                }
                if(users.isEmpty()){
                    Toast.makeText(ResultActivity.this, getString(R.string.there_is_no_players_found), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        displayList(users);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        User u = new User();
        u = (User) adapterView.getItemAtPosition(i);
        Intent intent = new Intent(this, PlayerProfileActivity.class);
        intent.putExtra("player",u);
        startActivity(intent);
        finish();
    }

    public void displayList(ArrayList<User> userList){
        adapter = new UsersAdapter(this,userList);
        listView.setAdapter(adapter);
    }
}
