package com.vetal.tennispartner.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vetal.tennispartner.R;
import com.vetal.tennispartner.adaptersAndOthers.SettingsFragment;

import java.util.Locale;

public class LoginActivity extends Activity implements View.OnClickListener, com.vetal.tennispartner.adaptersAndOthers.OnCompleteListener{

    private EditText emailAddress;
    private EditText password;
    private TextView forgetPassword;
    private Button loginButton;
    private Button registerButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private ImageView settingsImage;
    private Locale myLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        firebaseAuth = FirebaseAuth.getInstance();

        checkUserConnected();
        init();
        passwordMask();

    }

    private void passwordMask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isRTL(getApplicationContext())) {

            // Force a right-aligned text entry, otherwise latin character input,
            // like "abc123", will jump to the left and may even disappear!
            password.setTextDirection(View.TEXT_DIRECTION_RTL);

            // Make the "Enter password" hint display on the right hand side
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        password.addTextChangedListener(new TextWatcher() {

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
                            password.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                            // Move the cursor to the correct place (after the typed character)
                            password.setSelection(s.length());

                            inputTypeChanged = true;
                        }
                    } else {

                        // Reset EditText: Make the "Enter password" hint display on the right
                        password.setInputType(InputType.TYPE_CLASS_TEXT |
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
            finish();
            Intent intent = new Intent(getApplicationContext(), SearchPlayerFragments.class);
            startActivity(intent);
        }
    }

    private void init(){

        emailAddress = (EditText)findViewById(R.id.email_address_login_activity);
        password = (EditText)findViewById(R.id.password_login_activity);
        forgetPassword = (TextView)findViewById(R.id.forget_password_login_activity);
        loginButton = (Button)findViewById(R.id.login_button_login_activity);
        registerButton = (Button)findViewById(R.id.register_button_login_activity);
        forgetPassword.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        settingsImage = (ImageView)findViewById(R.id.settings);
        settingsImage.setOnClickListener(this);
    }

    private void userLogin(){
        String email = emailAddress.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
            //stopping the function
            return;
        }
        if(TextUtils.isEmpty(pass)){
            // pass is empty
            Toast.makeText(this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            return;
        }
        //if validation is ok we will show progressbar

        progressDialog.setMessage(getString(R.string.check_profile));
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                       // progressDialog.dismiss();
                        if(task.isSuccessful()){
                            // start the search activity=
                            Intent intent = new Intent(getApplicationContext() , SearchPlayerFragments.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                            Log.d("TAG", error);
                            emailAddress.setText("");
                            password.setText("");
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == forgetPassword){
            // go to ForgetPasswordActivity
            Intent intent = new Intent(this,ForgetPasswordActivity.class);
            startActivity(intent);
        }
        else if(view == loginButton){
            // validate user
            userLogin();

        }
        else if(view == registerButton){
            // go to ProfileActivity
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        if(view == settingsImage){

            SettingsFragment fragment = new SettingsFragment();
            fragment.show(getFragmentManager(),"");


        }
    }

    @Override
    public void onComplete(boolean result) {


        if(result){
            changeLang("iw");
            Intent refresh = new Intent(this, LoginActivity.class);
            startActivity(refresh);
            finish();
        }
        if (!result){
            changeLang("en");
            Intent refresh = new Intent(this, LoginActivity.class);
            startActivity(refresh);
            finish();
        }
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

}
