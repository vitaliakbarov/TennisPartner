package com.vetal.tennispartner.searchFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;
import com.vetal.tennispartner.R;
import com.vetal.tennispartner.activities.LoginActivity;
import com.vetal.tennispartner.adaptersAndOthers.ConfigGender;
import com.vetal.tennispartner.adaptersAndOthers.ConfigLevel;
import com.vetal.tennispartner.adaptersAndOthers.ConfigLocation;
import com.vetal.tennispartner.adaptersAndOthers.FirebaseDownloadThread;
import com.vetal.tennispartner.adaptersAndOthers.OnDownloadCompleteListener;
import com.vetal.tennispartner.adaptersAndOthers.User;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by vitaliakbarov on 22/09/2017.
 */

public class ByProfileFragment extends Fragment implements View.OnClickListener,OnDownloadCompleteListener, AdapterView.OnItemSelectedListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView fotoProfile;
    private ImageView imageView;
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
    private View rootView;
    private OnDownloadCompleteListener listener;
    private String androidId;
    private RadioButton male;
    private RadioButton female;
    private Context context;
    private boolean newImage = false;
    private ProgressDialog progressDialog;
    private TextView textViewLogout;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_profile, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        init();
        initSpinners();
        androidId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseDownloadThread thread = new FirebaseDownloadThread(this, getContext(), androidId);
        thread.start();


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getText(R.string.loading_profile));
        progressDialog.show();

        // firebase inits

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();




        return rootView;
    }



    private void init() {

        registerButton = (Button)rootView.findViewById(R.id.register_button_register_activity2);
        registerButton.setOnClickListener(this);
        fotoProfile = (ImageView)rootView.findViewById(R.id.profile_image_profile_activity2);
        fotoProfile.setOnClickListener(this);
        firstNameEditText = (EditText)rootView.findViewById(R.id.first_name_edit_text_register_activity2);
        lastNameEditText = (EditText)rootView.findViewById(R.id.last_name_edit_text_register_activity2);
        ageEditText = (EditText)rootView.findViewById(R.id.age_edit_text_register_activity2);
        telEditText = (EditText)rootView.findViewById(R.id.tel_edit_text_register_activity2);
        locationSpinner = (Spinner)rootView.findViewById(R.id.location_spinner_register_activity2);
        levelSpinner = (Spinner)rootView.findViewById(R.id.level_spinner_register_activity2);
        male = (RadioButton)rootView.findViewById(R.id.radio_male_register_activity2);
        female = (RadioButton)rootView.findViewById(R.id.radio_female_register_activity2);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        context = getContext();
        textViewLogout = (TextView)rootView.findViewById(R.id.logout_fragment_by_name);
        textViewLogout.setOnClickListener(this);

    }

    private void initSpinners() {

        levelSpinner = (Spinner) rootView.findViewById(R.id.level_spinner_register_activity2);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.level_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        levelSpinner.setAdapter(adapter);
        levelSpinner.setSelection(0);
        levelSpinner.setOnItemSelectedListener(this);


        locationSpinner = (Spinner) rootView.findViewById(R.id.location_spinner_register_activity2);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(getContext(),
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
        ConfigLevel configLevel = new ConfigLevel(getContext(),level);
        level = configLevel.toDB();
        ConfigLocation configLocation = new ConfigLocation(getContext(),location);
        location = configLocation.toDB();
        //config
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        location = "";
        level = "";
    }



    @Override
    public void onClick(View view) {
        if(view == registerButton) {
            //validate and register on DB
            if(male.isChecked()){
                Log.d("D", "male");
                gender = "Male";
            }
            if(female.isChecked()){
                Log.d("D", "fmale");
                gender = "Female";
            }
            if(validate()) {
                if(newImage) {
                    uploadImageToDB();
                }
                writeNewUser();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();

            }

        }
        if(view == fotoProfile){
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
            Log.d("HI", "entr");
            newImage = true;
        }
        if (view == textViewLogout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
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
            tel = telEditText.getText().toString().trim();
            age = ageEditText.getText().toString().trim();
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
           // imageUriInFirebase = "https://firebasestorage.googleapis.com/v0/b/tennispartner-7f25e.appspot.com/o/images%2Fempty.gif?alt=media&token=038890d2-af9a-4e50-84aa-5ecb254f80d6";
            writeNewUser();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                fotoProfile = (ImageView) rootView.findViewById(R.id.profile_image_profile_activity2);
                // Set the Image in ImageView after decoding the String
                fotoProfile.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));



                fotoProfile.setImageURI(selectedImage);
                uploadImageToDB();
                validate();



            } else {

                Toast.makeText(getActivity(), getString(R.string.not_picked_image), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG)
                    .show();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();


    }


    private boolean validate() {
        firstName = firstNameEditText.getText().toString().trim();
        lastName = lastNameEditText.getText().toString().trim();
        tel = telEditText.getText().toString();
        age = ageEditText.getText().toString().trim();

        if(firstName.isEmpty() || lastName.isEmpty() || tel.isEmpty() || age.isEmpty() ){

            Toast.makeText(getActivity(), getString(R.string.please_fill_all_empty_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (level.isEmpty() || location.isEmpty()){
            Toast.makeText(getActivity(), getString(R.string.please_chose_level_and_location), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(validatePhoneNumber(tel))){
            Toast.makeText(getActivity(), getString(R.string.bad_phone_number), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), getString(R.string.you_are_too_young), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (value > 80){
            Toast.makeText(getActivity(), getString(R.string.you_are_too_Old_for_tennis), Toast.LENGTH_SHORT).show();
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

        ConfigGender configGender = new ConfigGender(getContext(),gender);
        gender = configGender.toDB();

        DatabaseReference userToAddRef = databaseReference.child("users");
        newUser = new User(firstName, lastName, age, formatedPhoneNumber, level,location,gender, imageUriInFirebase);
        FirebaseUser user1 = firebaseAuth.getCurrentUser();
        fullName = newUser.getFirstName() + " " + newUser.getLastName();
        databaseReference.child("users").child(androidId).setValue(newUser);
    }


    @Override
    public void displayUserProfile(User user) {



        this.firstNameEditText.setText(user.getFirstName());
        this.lastNameEditText.setText(user.getLastName());
        this.ageEditText.setText(user.getAge());
        this.telEditText.setText(user.getTelephone());
        if(user.getGender().equals("Male") ){
            male.setChecked(true);
        }else {
            female.setChecked(true);
        }
        switch (user.getLevel()){
            case "Beginner":
                levelSpinner.setSelection(1);
                break;
            case "Advanced":
                levelSpinner.setSelection(2);
                break;
            case "Professional":
                levelSpinner.setSelection(3);
                break;
        }

        switch (user.getLocation()){
            case "North":
                locationSpinner.setSelection(1);
                break;
            case "Center":
                locationSpinner.setSelection(2);
                break;
            case "South":
                locationSpinner.setSelection(3);
                break;



        }
        imageUriInFirebase = user.getImageUri();
        Picasso.with(context).load(user.getImageUri()).into(fotoProfile);

        progressDialog.dismiss();
    }



}
