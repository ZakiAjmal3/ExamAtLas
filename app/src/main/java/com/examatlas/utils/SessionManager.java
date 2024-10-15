package com.examatlas.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
    public static final String Name = "name";
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
        user.put("name", pref.getString(Name, Default_Value));
        user.put("mobile", pref.getString(Mobile,Default_Value));
        user.put("email", pref.getString(Email, Default_Value));
        user.put("user_id",pref.getString(User_id,Default_Value));
        user.put("role",pref.getString(Role,Default_Value));
        user.put("authToken",pref.getString(AuthToken,Default_Value));
        user.put("createdAt",pref.getString(CreatedAt,Default_Value));
        user.put("updatedAt",pref.getString(UpdatedAt,Default_Value));
        return user;
    }

    public boolean logout() {
        editor.clear();
        editor.apply();
        return true;
    }

    public void saveLoginDetails(String user_id, String name, String email, String mobile,String role, String authToken, String createdAt, String updatedAt) {
        editor.putString(User_id, user_id);
        editor.putString(Mobile, mobile);
        editor.putString(Name, name);
        editor.putString(Email, email);
        editor.putString(Role, role);
        editor.putString(AuthToken, authToken);
        editor.putString(UpdatedAt, updatedAt);
        editor.putString(CreatedAt, createdAt);
        editor.putBoolean(IsLogin, true);
        editor.apply();
    }
    public boolean IsLoggedIn() {
        return pref.getBoolean(IsLogin, false);
    }
}
