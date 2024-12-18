package com.examatlas.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SessionManager {

    public Context ctx;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public static final String pref_name = "examatlas";
    public int PRIVATE_MODE = 0;
    public static final String Default_Value = "DEFAULT";
    public static final String AuthToken = "authToken";
    public static final String Mobile = "mobile";
    public static final String FirstName = "firstName";
    public static final String LastName = "lastName";
    public static final String State = "state";
    public static final String Organisation = "organisation";
    public static final String City = "city";
    public static final String Step = "1";
    public static final String IsActive = "isActive";
    public static final String Email = "email";
    public static final String Role = "role";
    public static final String User_id = "user_id";
    public static final String CreatedAt = "createdAt";
    public static final String UpdatedAt = "updatedAt";
    public static final String IsLogin = "IsLoggedIn";

    public SessionManager(Context context) {
        ctx = context;
        pref = ctx.getSharedPreferences(pref_name, PRIVATE_MODE);
        editor = pref.edit();
    }

    public HashMap<String, String> getUserData() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put("firstName", pref.getString(FirstName, Default_Value));
        user.put("lastName", pref.getString(LastName, Default_Value));
        user.put("state", pref.getString(State, Default_Value));
        user.put("city", pref.getString(City, Default_Value));
        user.put("step", pref.getString(Step, Default_Value));
        user.put("isActive", pref.getString(IsActive, Default_Value));
        user.put("mobile", pref.getString(Mobile, Default_Value));
        user.put("email", pref.getString(Email, Default_Value));
        user.put("user_id", pref.getString(User_id, Default_Value));
        user.put("role", pref.getString(Role, Default_Value));
        user.put("authToken", pref.getString(AuthToken, Default_Value));
        user.put("createdAt", pref.getString(CreatedAt, Default_Value));
        user.put("updatedAt", pref.getString(UpdatedAt, Default_Value));

        Log.d("SessionManager", "User data: " + user.toString());
        return user;
    }

    public boolean logout() {
        editor.clear();
        editor.apply();
        Log.d("SessionManager", "Logged out, session cleared.");
        return true;
    }

    public void saveLoginDetails(String user_id, String name, String email, String mobile, String role, String authToken, String createdAt, String updatedAt) {
        editor.putString(User_id, user_id);
        editor.putString(Mobile, mobile);
        editor.putString(FirstName, name);
        editor.putString(Email, email);
        editor.putString(Role, role);
        editor.putString(AuthToken, authToken);
        editor.putString(UpdatedAt, updatedAt);
        editor.putString(CreatedAt, createdAt);
        editor.putBoolean(IsLogin, true);
        editor.apply();

        Log.d("SessionManager", "Login details saved for UserID: " + user_id);
    }

    public void saveLoginDetails2(String user_id, String firstName, String lastName, String email, String state, String city, String role, String isActive, String step, String authToken, String createdAt, String updatedAt, String organisation) {
        editor.putString(User_id, user_id);
        editor.putString(FirstName, firstName);
        editor.putString(LastName, lastName);
        editor.putString(Email, email);
        editor.putString(State, state);
        editor.putString(City, city);
        editor.putString(IsActive, isActive);
        editor.putString(Step, step);
        editor.putString(Role, role);
        editor.putString(AuthToken, authToken);
        editor.putString(UpdatedAt, updatedAt);
        editor.putString(CreatedAt, createdAt);
        editor.putString(Organisation, organisation);
        editor.putBoolean(IsLogin, true);
        editor.apply();

        Log.d("SessionManager", "Login details saved for UserID: " + user_id);
    }

    public boolean IsLoggedIn() {
        boolean isLoggedIn = pref.getBoolean(IsLogin, false);
        Log.d("SessionManager", "IsLoggedIn: " + isLoggedIn);
        return isLoggedIn;
    }
}
