package com.vetal.tennispartner.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.vetal.tennispartner.R;
import com.vetal.tennispartner.adaptersAndOthers.ConfigGender;
import com.vetal.tennispartner.adaptersAndOthers.ConfigLevel;
import com.vetal.tennispartner.adaptersAndOthers.ConfigLocation;
import com.vetal.tennispartner.adaptersAndOthers.User;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView fotoProfile;
    private Button registerButton;
    private Spinner locationSpinner;
    private Spinner levelSpinner;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText ageEditText;
    private EditText telEditText;
    private String firstName;
    private String lastName;
    private String gender = "Male";
    private String level;
    private String location;
    private String age;
    private String tel;
    private String formatedPhoneNumber;
    private String fullName;
    private String imgDecodableString;
    private Uri selectedImage = null;
    private String imageUriInFirebase;
    private StorageReference storageRef;
    private FirebaseUser user;
    private User newUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init();
        initSpinners();
        // firebase inits

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();


    }

    private void init() {

    //    registerButton = (Button)findViewById(R.id.register_button_register_activity);
        registerButton.setOnClickListener(this);
        fotoProfile = (ImageView)findViewById(R.id.profile_image_profile_activity);
        fotoProfile.setOnClickListener(this);
        firstNameEditText = (EditText)findViewById(R.id.first_name_edit_text_register_activity);
        lastNameEditText = (EditText)findViewById(R.id.last_name_edit_text_register_activity);
        ageEditText = (EditText)findViewById(R.id.age_edit_text_register_activity);
        telEditText = (EditText)findViewById(R.id.tel_edit_text_register_activity);
        locationSpinner = (Spinner)findViewById(R.id.location_spinner_register_activity);
        levelSpinner = (Spinner)findViewById(R.id.level_spinner_register_activity);
    }

    private void initSpinners() {

        levelSpinner = (Spinner) findViewById(R.id.level_spinner_register_activity);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.level_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        levelSpinner.setAdapter(adapter);
        levelSpinner.setSelection(0);
        levelSpinner.setOnItemSelectedListener(this);


        locationSpinner = (Spinner) findViewById(R.id.location_spinner_register_activity);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        locationSpinner.setAdapter(locationAdapter);
        levelSpinner.setSelection(0);
        locationSpinner.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        location = locationSpinner.getSelectedItem().toString();
        level = levelSpinner.getSelectedItem().toString();
        ConfigLevel configLevel = new ConfigLevel(this,level);
        level = configLevel.toDB();
        ConfigLocation configLocation = new ConfigLocation(this,location);
        location = configLocation.toDB();
        //config
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        location = "";
        level = "";
    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male_register_activity:
                if (checked)
                    // male
                    gender = "Male";
                    break;
            case R.id.radio_female_register_activity:
                if (checked)
                    // female
                    gender = "Female";
                    break;
        }
    }

    @Override
    public void onClick(View view) {
        if(view == registerButton) {
            //validate and register on DB
            if(validate()) {
                uploadImageToDB();
                Intent intent = new Intent(this, SearchPlayerFragments.class);
                startActivity(intent);
                finish();
            }
        }
        if(view == fotoProfile){
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }
    }

    private void uploadImageToDB() {

        if(selectedImage != null) {
            // Points to the root reference
            StorageReference imagesRef = storageRef.child("images");
            StorageReference fullNameRef = storageRef.child("images/space.jpg");
            // Points to "images"
            imagesRef = storageRef.child("images");

            // Points to "images/space.jpg"
            // Note that you can use variables to create child values
            firstName = firstNameEditText.getText().toString().trim();
            lastName = lastNameEditText.getText().toString().trim();
            fullName = firstName + " " + lastName;
            String fileName = fullName + ".jpg";
            fullNameRef = imagesRef.child(fileName);

            // File path is "images/space.jpg"
            String path = fullNameRef.getPath();
            // File name is "space.jpg"
            String name = fullNameRef.getName();
            // Points to "images"
            imagesRef = fullNameRef.getParent();

            // Get the data from an ImageView as bytes
            fotoProfile.setDrawingCacheEnabled(true);
            fotoProfile.buildDrawingCache();
            Bitmap bitmap = fotoProfile.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = fullNameRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("UPLOAD", "fail" + exception);
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imageUriInFirebase = downloadUrl.toString();

                    writeNewUser();
                }
            });
        }
        else {
            imageUriInFirebase = "https://firebasestorage.googleapis.com/v0/b/tennispartner-7f25e.appspot.com/o/images%2Fempty.gif?alt=media&token=038890d2-af9a-4e50-84aa-5ecb254f80d6";
            writeNewUser();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                fotoProfile = (ImageView) findViewById(R.id.profile_image_profile_activity);
                // Set the Image in ImageView after decoding the String
                fotoProfile.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));
                fotoProfile.setImageURI(selectedImage);

            } else {
                Toast.makeText(this, getString(R.string.not_picked_image), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG)
                    .show();
        }

    }


    private boolean validate() {
        firstName = firstNameEditText.getText().toString().trim();
        lastName = lastNameEditText.getText().toString().trim();
        tel = telEditText.getText().toString();
        age = ageEditText.getText().toString().trim();

        if(firstName.isEmpty() || lastName.isEmpty() || tel.isEmpty() || age.isEmpty() ){

            Toast.makeText(ProfileActivity.this, getString(R.string.please_fill_all_empty_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (level.isEmpty() || location.isEmpty()){
            Toast.makeText(ProfileActivity.this, getString(R.string.please_chose_level_and_location), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(validatePhoneNumber(tel))){
            Toast.makeText(ProfileActivity.this, getString(R.string.bad_phone_number), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(validateAge())){
            ageEditText.setText("");
            return false;
        }

        return true;
    }

    private boolean validateAge(){
        final int value = Integer.valueOf(ageEditText.getText().toString());
        if (value < 8) {
            Toast.makeText(ProfileActivity.this, getString(R.string.you_are_too_young), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (value > 80){
            Toast.makeText(ProfileActivity.this, getString(R.string.you_are_too_Old_for_tennis), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber(String number){
        // google API
        PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber pn = null;
        try {
            pn = pnu.parse(number, "IL");
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        boolean b = pnu.isValidNumber(pn);
        if(b){
            formatedPhoneNumber = pnu.formatInOriginalFormat(pn, "IL");
            return true;
        }
        return false;
    }

    private void writeNewUser() {

        ConfigGender configGender = new ConfigGender(this,gender);
        gender = configGender.toDB();

        DatabaseReference userToAddRef = databaseReference.child("users");
        newUser = new User(firstName, lastName, age, formatedPhoneNumber, level,location,gender, imageUriInFirebase);
        FirebaseUser user1 = firebaseAuth.getCurrentUser();
        fullName = newUser.getFirstName() + " " + newUser.getLastName();
        databaseReference.child("users").child(fullName).setValue(newUser);
    }

    public void onRadioButtonClicked2(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male_register_activity:
                if (checked)
                    // male
                    gender = "Male";
                break;
            case R.id.radio_female_register_activity:
                if (checked)
                    // female
                    gender = "Female";
                Log.d("D", "female");
                break;
        }
    }
}
