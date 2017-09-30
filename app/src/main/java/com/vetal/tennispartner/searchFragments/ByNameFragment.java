package com.vetal.tennispartner.searchFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vetal.tennispartner.R;
import com.vetal.tennispartner.activities.LoginActivity;
import com.vetal.tennispartner.activities.ResultActivity;


public class ByNameFragment extends Fragment implements View.OnClickListener {

    private Button searchButton;
    private FirebaseAuth firebaseAuth;

    private TextView textViewEmail;
    private EditText firstNameEditText;
    private String firstName;
    private View rootView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_by_name, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        textViewEmail = (TextView)rootView.findViewById(R.id.text_view_email_fragment_by_name);
        firstNameEditText = (EditText)rootView.findViewById(R.id.first_name_fragment_by_name);
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
        if (view == searchButton) {
            if (getValues()) {
                saveValues();
            }
        }
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

