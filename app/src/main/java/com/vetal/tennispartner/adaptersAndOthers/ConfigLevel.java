package com.vetal.tennispartner.adaptersAndOthers;

import android.content.Context;
import android.util.Log;

import com.vetal.tennispartner.R;


public class ConfigLevel {

    private String level;
    private Context context;

    public ConfigLevel(Context context, String level) {
        this.level = level;
        this.context = context;

    }

    public String fromDB(){
        switch (level){
            case "Beginner":
                level = context.getString(R.string.beginner);
                break;
            case "Advanced":
                level = context.getString(R.string.advanced);
                break;
            case "Professional":
                level = context.getString(R.string.professional);
                break;

        }
        return level;

    }
    public String toDB(){

        switch (level) {
            case "רמה":
                level = "";
                break;
            case "מתחיל":
                level = "Beginner";
                break;
            case "מנוסה":
                level = "Advanced";
                break;
            case "מקצוען":
                level = "Professional";
                break;
            case "Level":
                level = "";
                break;
            case "Beginner":
                level = "Beginner";
                break;
            case "Advanced":
                level = "Advanced";
                break;
            case "Professional":
                level = "Professional";
                break;
        }
        return level;
    }

    public String getLevel() {
        return level;
    }
}
