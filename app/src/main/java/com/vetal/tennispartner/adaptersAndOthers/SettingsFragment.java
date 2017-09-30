package com.vetal.tennispartner.adaptersAndOthers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.vetal.tennispartner.R;
import com.vetal.tennispartner.activities.LoginActivity;

/**
 * Created by vitaliakbarov on 21/09/2017.
 */

public class SettingsFragment extends DialogFragment{

    private Context context;
    private OnCompleteListener listener;
    private boolean isHebrew;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_settings, null);

        Button buttonSave = (Button) view.findViewById(R.id.hebrew);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHebrew = true;
                listener.onComplete(isHebrew);
                dismiss();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        Button buttonCancel = (Button) view.findViewById(R.id.english);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isHebrew = false;
                listener.onComplete(isHebrew);
                dismiss();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.listener = (OnCompleteListener) context;

    }
}
