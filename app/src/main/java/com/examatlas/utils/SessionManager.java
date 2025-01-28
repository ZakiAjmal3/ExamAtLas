package com.examatlas.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.activities.Books.CartViewActivity;
import com.examatlas.adapter.books.CartViewItemAdapter;
import com.examatlas.models.Books.CartItemModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    public Context ctx;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public static final String pref_name = "examatlas";
    public int PRIVATE_MODE = 0;
    public static final String Default_Value = "User";
    public static final String AuthToken = "authToken";
    public static final String Mobile = "mobile";
    public static final String FirstName = "firstName";
    public static final String LastName = "lastName";
    public static final String State = "state";
    public static final String Organisation = "organisation";
    public static final String City = "city";
    public static final String Step = "0";
    public static final String IsActive = "isActive";
    public static final String Email = "email";
    public static final String Role = "role";
    public static final String User_id = "user_id";
    public static final String CreatedAt = "createdAt";
    public static final String UpdatedAt = "updatedAt";
    public static final String IsLogin = "IsLoggedIn";
    public static String CartQuantity = "0";

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

    public void saveLoginDetails2(String user_id, String firstName, String lastName, String mobile, String email, String state, String city, String role, String isActive, String step, String authToken, String createdAt, String updatedAt, String organisation) {
        editor.putString(User_id, user_id);
        editor.putString(FirstName, firstName);
        editor.putString(LastName, lastName);
        editor.putString(Mobile, mobile);
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
    public void saveWithOutToken(String user_id, String firstName, String lastName, String email, String state, String city, String role, String isActive, String step, String createdAt, String updatedAt, String organisation) {
        editor.putString(User_id, user_id);
        editor.putString(FirstName, firstName);
        editor.putString(LastName, lastName);
        editor.putString(Email, email);
        editor.putString(State, state);
        editor.putString(City, city);
        editor.putString(IsActive, isActive);
        editor.putString(Step, step);
        editor.putString(Role, role);
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
    public void setCartItemQuantity(){
        String paginatedURL = Constant.BASE_URL + "v1/cart";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                        progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("data");
                                String cartId = jsonObject.getString("_id");
                                JSONArray itemsArray = jsonObject.getJSONArray("items");

                                // Count the cart items
                                int cartItemCount = itemsArray.length();
                                editor.remove(CartQuantity);
                                editor.putString(CartQuantity, String.valueOf(cartItemCount));  // Store in SharedPreferences
                                editor.apply();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("BlogFetchError", errorMessage);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + getUserData().get("authToken"));
                return headers;
            }
        };
        MySingleton.getInstance(ctx.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    public String getCartQuantity(){
        return pref.getString(CartQuantity,"0");
    }
}
