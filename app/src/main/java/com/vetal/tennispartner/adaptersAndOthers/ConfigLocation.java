package com.vetal.tennispartner.adaptersAndOthers;

import android.content.Context;
import android.util.Log;

import com.vetal.tennispartner.R;

public class ConfigLocation {

    private String location;
    private Context context;

    public ConfigLocation(Context context, String location) {
        this.location = location;
        this.context = context;

    }

    public String fromDB(){

        switch (location){
            case "North":
                location = context.getString(R.string.north);
                break;
            case "Center":
                location = context.getString(R.string.center);
                break;
            case "South":
                location = context.getString(R.string.south);
                break;

        }

        return location;
    }

    public String toDB(){

        switch (location) {
            case "מיקום":
                location = "";
                break;
            case "צפון":
                location = "North";
                break;
            case "מרכז":
                location = "Center";
                break;
            case "דרום":
                location = "South";
                break;
            case "Location":
                location = "";
                break;
            case "North":
                location = "North";
                break;
            case "Center":
                location = "Center";
                break;
            case "South":
                location = "South";
                break;
        }

        return location;
    }

    public String getLocation() {
        return location;
    }
}
