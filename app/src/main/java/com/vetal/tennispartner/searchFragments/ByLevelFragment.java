package com.vetal.tennispartner.searchFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.vetal.tennispartner.adaptersAndOthers.ConfigLevel;


public class ByLevelFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private Button searchButton;
    private FirebaseAuth firebaseAuth;

    private TextView textViewEmail;
    private Spinner levelSpinner;
    private String level;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_by_level, container, false);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        textViewEmail = (TextView)rootView.findViewById(R.id.text_view_email_fragment_by_level);
        searchButton = (Button)rootView.findViewById(R.id.search_button_fragment_by_level);
        searchButton.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        levelSpinner = (Spinner)rootView.findViewById(R.id.level_spinner_fragment_by_level);


         levelSpinner = (Spinner)rootView.findViewById(R.id.level_spinner_fragment_by_level);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.level_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setSelection(0);
// Apply the adapter to the spinner

        levelSpinner.setAdapter(adapter);
        levelSpinner.setOnItemSelectedListener(this);

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

        if(view == searchButton){
            ConfigLevel configLevel = new ConfigLevel(getActivity(),level);
            level = configLevel.toDB();

            if(level.isEmpty()){
                Toast.makeText(getActivity(), getString(R.string.choose_level), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(),ResultActivity.class);
            intent.putExtra("SEARCH_BY", "level");
            intent.putExtra("SEARCH_FOR", level);
            startActivity(intent);
            levelSpinner.setSelection(0);

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        level = levelSpinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
