package com.vetal.tennispartner.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.vetal.tennispartner.R;
import com.vetal.tennispartner.adaptersAndOthers.ConfigGender;
import com.vetal.tennispartner.adaptersAndOthers.ConfigLevel;
import com.vetal.tennispartner.adaptersAndOthers.ConfigLocation;
import com.vetal.tennispartner.adaptersAndOthers.User;

public class PlayerProfileActivity extends Activity implements View.OnClickListener {

    private TextView fullName;
    private TextView age;
    private TextView location;
    private TextView level;
    private TextView gender;
    private TextView phoneNumber;

    private ImageView profileImage;
    private User u;
    private FirebaseAuth firebaseAuth;
    private boolean secondClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        Intent intent = getIntent();
        u = (User) intent.getSerializableExtra("player");

        init();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();

        fullName = (TextView) findViewById(R.id.full_name_player_profile_activity);
        age = (TextView) findViewById(R.id.age_player_profile_activity);
        location = (TextView) findViewById(R.id.location_player_profile_activity);
        level = (TextView) findViewById(R.id.level_player_profile_activity);
        gender = (TextView) findViewById(R.id.gender_player_profile_activity);
        phoneNumber = (TextView) findViewById(R.id.phone_player_profile_activity);
        profileImage = (ImageView) findViewById(R.id.player_profile_image_view);
        String lev = u.getLevel();
        String loc = u.getLocation();
        String gen = u.getGender();
        ConfigLevel configLevel = new ConfigLevel(this,lev);
        ConfigLocation configLocation = new ConfigLocation(this,loc);
        ConfigGender configGender = new ConfigGender(this,gen);



        fullName.setText(u.getFirstName() + " " + u.getLastName());
        age.setText(getString(R.string.ageP) + ": " + u.getAge());
        location.setText(getString(R.string.location) + ": " + configLocation.fromDB());
        level.setText(getString(R.string.player_level) + ": " + configLevel.fromDB());
        gender.setText(configGender.fromDB());
        phoneNumber.setOnClickListener(this);
        phoneNumber.setPaintFlags(phoneNumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Picasso.with(this).load(u.getImageUri()).into(profileImage);
    }


    @Override
    public void onClick(View view) {


        if (view == phoneNumber && secondClick == true) {

            phoneNumber.setText(u.getTelephone());
            secondClick = false;
        } else if (view == phoneNumber && secondClick == false) {
            // opens the call application with the phone number ready
            String num = phoneNumber.getText().toString().trim();
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + num));
            startActivity(callIntent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
