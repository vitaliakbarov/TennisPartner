package com.vetal.tennispartner.adaptersAndOthers;


import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private String age;
    private String gender;
    private String email;
    private String level;
    private String location;
    private String telephone;
    private String imageUri;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String firstName, String lastName, String age, String telephone, String level, String location, String gender, String imageUri){
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.telephone = telephone;
        this.level = level;
        this.location = location;
        this.gender = gender;
        this.imageUri = imageUri;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
