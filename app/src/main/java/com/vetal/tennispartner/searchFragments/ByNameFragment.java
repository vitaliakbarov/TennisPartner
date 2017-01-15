package com.vetal.tennispartner.searchFragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vetal.tennispartner.R;
import com.vetal.tennispartner.activities.LoginActivity;
import com.vetal.tennispartner.activities.ResultActivity;
import com.vetal.tennispartner.activities.SearchPlayerFragments;

import java.util.Locale;


public class ByNameFragment extends Fragment implements View.OnClickListener {

    private Button searchButton;
    private FirebaseAuth firebaseAuth;
    private TextView textViewLogout;
    private TextView textViewEmail;
    private EditText firstNameEditText;
    private String firstName;
    private ImageView swipeImage;
    private Button english;
    private Button hebrew;
    private Locale myLocale;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loadLocale();
        View rootView = inflater.inflate(R.layout.fragment_by_name, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        textViewEmail = (TextView)rootView.findViewById(R.id.text_view_email_fragment_by_name);
        textViewLogout = (TextView)rootView.findViewById(R.id.logout_fragment_by_name);
        textViewLogout.setOnClickListener(this);
        firstNameEditText = (EditText)rootView.findViewById(R.id.first_name_fragment_by_name);
        swipeImage = (ImageView)rootView.findViewById(R.id.swipe_image_view);

        english = (Button)rootView.findViewById(R.id.english_by_name_fragment);
        hebrew = (Button)rootView.findViewById(R.id.hebrew_by_name_fragment);
        english.setOnClickListener(this);
        hebrew.setOnClickListener(this);

        searchButton = (Button)rootView.findViewById(R.id.search_button_fragment_by_name);
        searchButton.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        return rootView;
    }


    private void checkUser() {
        if(firebaseAuth.getCurrentUser() == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        else {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            textViewEmail.setText(getString(R.string.welcome) + " : " + user.getEmail());
        }
    }


    @Override
    public void onClick(View view) {

        if(view == english){
            changeLang("en");
            Intent refresh = new Intent(getContext(), SearchPlayerFragments.class);
            startActivity(refresh);
            getActivity().finish();
        }

        if(view == hebrew){
            changeLang("iw");
            Intent refresh = new Intent(getContext(), SearchPlayerFragments.class);
            startActivity(refresh);
            getActivity().finish();
        }

        if (view == searchButton) {
            if (getValues()) {
                saveValues();
            }
        } else if (view == textViewLogout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getContext().getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
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
        getActivity().getBaseContext().getResources().updateConfiguration(config,getActivity().getBaseContext().getResources().getDisplayMetrics());
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getContext().getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }
    private boolean getValues() {

        firstName = firstNameEditText.getText().toString().trim();

        if(firstName.isEmpty() ){
            //search fields are empty
            Toast.makeText(getActivity(), getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveValues() {

        Intent intent = new Intent(getActivity(),ResultActivity.class);
        intent.putExtra("SEARCH_BY", "firstName");
        intent.putExtra("SEARCH_FOR", firstName);
        startActivity(intent);
    }

    }

