package com.vetal.tennispartner.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import java.util.Locale;

public class RegisterActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText editTextEmailAddress;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView alreadyRegistered;
    private ProgressDialog progressDialog;
    private Locale myLocale;
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
    private String androidId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        initSpinners();
        checkUserConnected();
        passwordMask();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    private void passwordMask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isRTL(getApplicationContext())) {

            // Force a right-aligned text entry, otherwise latin character input,
            // like "abc123", will jump to the left and may even disappear!
            editTextPassword.setTextDirection(View.TEXT_DIRECTION_RTL);
            editTextConfirmPassword.setTextDirection(View.TEXT_DIRECTION_RTL);
            // Make the "Enter password" hint display on the right hand side
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }


        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {

            boolean inputTypeChanged;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                // Workaround https://code.google.com/p/android/issues/detail?id=201471 for Android 4.4+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isRTL(getApplicationContext())) {
                    if (s.length() > 0) {
                        if (!inputTypeChanged) {

                            // When a character is typed, dynamically change the EditText's
                            // InputType to PASSWORD, to show the dots and conceal the typed characters.
                            editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                            // Move the cursor to the correct place (after the typed character)
                            editTextConfirmPassword.setSelection(s.length());

                            inputTypeChanged = true;
                        }
                    } else {

                        // Reset EditText: Make the "Enter password" hint display on the right
                        editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                        inputTypeChanged = false;
                    }
                }

            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {

            boolean inputTypeChanged;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                // Workaround https://code.google.com/p/android/issues/detail?id=201471 for Android 4.4+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isRTL(getApplicationContext())) {
                    if (s.length() > 0) {
                        if (!inputTypeChanged) {

                            // When a character is typed, dynamically change the EditText's
                            // InputType to PASSWORD, to show the dots and conceal the typed characters.
                            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                            // Move the cursor to the correct place (after the typed character)
                            editTextPassword.setSelection(s.length());

                            inputTypeChanged = true;
                        }
                    } else {

                        // Reset EditText: Make the "Enter password" hint display on the right
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                        inputTypeChanged = false;
                    }
                }

            }
        });
    }
    public static boolean isRTL(Context context) {
        // Define a boolean resource as "true" in res/values-ldrtl
        // and "false" in res/values
        return context.getResources().getConfiguration().getLayoutDirection()
                == View.LAYOUT_DIRECTION_RTL;
        // Another way:
        // return context.getResources().getConfiguration().getLayoutDirection()
        //     == View.LAYOUT_DIRECTION_RTL;
    }



    private void checkUserConnected() {
        if(firebaseAuth.getCurrentUser() != null){
            //search activity here
            Intent intent = new Intent(getApplicationContext(), SearchPlayerFragments.class);
            startActivity(intent);
            finish();
        }
    }

    private void init(){

        editTextEmailAddress = (EditText)findViewById(R.id.email_address_main_activity);
        editTextPassword = (EditText)findViewById(R.id.password_main_activity);
        editTextConfirmPassword = (EditText)findViewById(R.id.confirm_password_main_activity);
        alreadyRegistered = (TextView)findViewById(R.id.already_registered_main_activity);
        registerButton = (Button)findViewById(R.id.register_button_main_activity);
        alreadyRegistered.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

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

        if(view == alreadyRegistered){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(view == fotoProfile){
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }
        else if(view == registerButton){
            if(validate()) {
                uploadImageToDB();
                registerUser();
                Intent intent = new Intent(this, SearchPlayerFragments.class);
                startActivity(intent);
                finish();
            }


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
//            firstName = firstNameEditText.getText().toString().trim();
//            lastName = lastNameEditText.getText().toString().trim();
//            fullName = firstName + " " + lastName;
            String fileName = androidId + ".jpg";
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

    private boolean validate() {
        firstName = firstNameEditText.getText().toString().trim();
        lastName = lastNameEditText.getText().toString().trim();
        tel = telEditText.getText().toString();
        age = ageEditText.getText().toString().trim();

        if(firstName.isEmpty() || lastName.isEmpty() || tel.isEmpty() || age.isEmpty() ){

            Toast.makeText(RegisterActivity.this, getString(R.string.please_fill_all_empty_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (level.isEmpty() || location.isEmpty()){
            Toast.makeText(RegisterActivity.this, getString(R.string.please_chose_level_and_location), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(validatePhoneNumber(tel))){
            Toast.makeText(RegisterActivity.this, getString(R.string.bad_phone_number), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(RegisterActivity.this, getString(R.string.you_are_too_young), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (value > 80){
            Toast.makeText(RegisterActivity.this, getString(R.string.you_are_too_Old_for_tennis), Toast.LENGTH_SHORT).show();
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
        databaseReference.child("users").child(androidId).setValue(newUser);
    }

    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }


    private void registerUser() {
        String email = editTextEmailAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
            //stopping the function
            return;
        }
        if(TextUtils.isEmpty(password)){
            // pass is empty
            Toast.makeText(this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(confirmPassword)){
            // pass is empty
            Toast.makeText(this, getString(R.string.please_confirm_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!(password.equals(confirmPassword))){
            Toast.makeText(this, getString(R.string.incorrect_confirmation), Toast.LENGTH_SHORT).show();
            editTextPassword.setText("");
            editTextConfirmPassword.setText("");
            return;
        }
        //if validation is ok show progressbar

        progressDialog.setMessage(getString(R.string.registering_User));
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new com.google.android.gms.tasks.OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //user is successfuly registred and logged in
                            //we will start the profile activity

                            Intent intent = new Intent(RegisterActivity.this,SearchPlayerFragments.class);
                            startActivity(intent);
                            finish();
                        }else {
                            progressDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}
