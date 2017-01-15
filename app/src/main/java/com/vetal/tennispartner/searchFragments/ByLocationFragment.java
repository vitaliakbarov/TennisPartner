package com.vetal.tennispartner.searchFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vetal.tennispartner.R;
import com.vetal.tennispartner.activities.LoginActivity;
import com.vetal.tennispartner.activities.ResultActivity;
import com.vetal.tennispartner.adaptersAndOthers.ConfigLocation;


public class ByLocationFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button searchButton;
    private FirebaseAuth firebaseAuth;
    private TextView textViewLogout;
    private TextView textViewEmail;
    private Spinner locationSpinner;

    private String location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_by_location, container, false);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        textViewEmail = (TextView)rootView.findViewById(R.id.text_view_email_fragment_by_location);
        textViewLogout = (TextView)rootView.findViewById(R.id.logout_fragment_by_location);
        textViewLogout.setOnClickListener(this);
        searchButton = (Button)rootView.findViewById(R.id.search_button_fragment_by_location);
        searchButton.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

        locationSpinner = (Spinner)rootView.findViewById(R.id.location_spinner_fragment_by_location);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.location_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setSelection(0);
// Apply the adapter to the spinner
        locationSpinner.setAdapter(adapter);
        locationSpinner.setOnItemSelectedListener(this);

        checkUser();

        return rootView;
    }

    @Override
    public void onClick(View view) {

        if(view == searchButton){
            ConfigLocation configLocation = new ConfigLocation(getActivity(),location);
            location = configLocation.toDB();
            if(location.isEmpty()){
                Toast.makeText(getActivity(), getString(R.string.choose_location), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(),ResultActivity.class);
            intent.putExtra("SEARCH_BY", "location");
            intent.putExtra("SEARCH_FOR", location);
            startActivity(intent);
            locationSpinner.setSelection(0);
        }
        else if(view == textViewLogout){
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        location = locationSpinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
