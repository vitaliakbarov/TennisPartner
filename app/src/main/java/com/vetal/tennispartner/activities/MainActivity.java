package com.vetal.tennispartner.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vetal.tennispartner.R;

import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText editTextEmailAddress;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView alreadyRegistered;
    private Button registerButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Button english;
    private Button hebrew;
    private Locale myLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        checkUserConnected();
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
        english = (Button)findViewById(R.id.english_main_activity);
        hebrew = (Button)findViewById(R.id.hebrew_main_activity);
        english.setOnClickListener(this);
        hebrew.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {

        if(view == alreadyRegistered){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if(view == registerButton){
            registerUser();

        }

        if(view == english){

            changeLang("en");
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            finish();
        }

        if(view == hebrew){

            changeLang("iw");
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            finish();
        }
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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //user is successfuly registred and logged in
                            //we will start the profile activity

                            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            progressDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
