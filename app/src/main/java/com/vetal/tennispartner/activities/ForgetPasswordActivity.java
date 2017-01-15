package com.vetal.tennispartner.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.vetal.tennispartner.R;
import com.vetal.tennispartner.activities.LoginActivity;

public class ForgetPasswordActivity extends Activity implements View.OnClickListener {

    private EditText emailAddress;
    private Button createNewPasswordButton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        emailAddress = (EditText)findViewById(R.id.email_address_forget_password_activity);
        createNewPasswordButton = (Button)findViewById(R.id.create_password_button_forget_password_activity);
        createNewPasswordButton.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view == createNewPasswordButton){
            String mail = emailAddress.getText().toString().trim();
            // send new password for mail
            if(mail.isEmpty()){
                Toast.makeText(ForgetPasswordActivity.this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
            }
            else {
                firebaseAuth.sendPasswordResetEmail(mail);
                Toast.makeText(this, getString(R.string.reset_password_was_sent_to_your_email), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
