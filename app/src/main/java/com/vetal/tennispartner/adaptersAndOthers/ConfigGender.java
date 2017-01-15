package com.vetal.tennispartner.adaptersAndOthers;

import android.content.Context;

import com.vetal.tennispartner.R;


public class ConfigGender {

    private String gender;
    private Context context;

    public ConfigGender(Context context, String gender){
        this.gender = gender;
        this.context = context;
    }

    public String fromDB(){

        switch (gender){
            case "Male":
                gender = context.getString(R.string.male);
                break;
            case "Female":
                gender = context.getString(R.string.female);
                break;
        }

        return gender;
    }

    public String toDB(){

        switch (gender){

            case "זכר":
                gender = "Male";
                break;
            case "נקבה":
                gender = "Female";
                break;
            case "Male":
                gender = "Male";
                break;
            case "Female":
                gender = "Female";
                break;
        }

        return gender;
    }




}
