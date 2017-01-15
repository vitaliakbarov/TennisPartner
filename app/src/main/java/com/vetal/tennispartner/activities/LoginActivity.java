package com.vetal.tennispartner.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
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

public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText emailAddress;
    private EditText password;
    private TextView forgetPassword;
    private Button loginButton;
    private Button registerButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        firebaseAuth = FirebaseAuth.getInstance();

        checkUserConnected();
        init();

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
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
