package com.examatlas.activities.Books;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.examatlas.R;
import com.examatlas.fragment.DeliveryAddressInput1Fragment;
import com.examatlas.fragment.DeliveryAddressInput2Fragment;
import com.examatlas.utils.SessionManager;

public class DeliveryAddressInputActivity extends AppCompatActivity {
    public String currentFrag = "HOME";
    SessionManager sessionManager;
    Fragment currentFragment;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address_input);

        backBtn = findViewById(R.id.backBtn);

        // Initialize the fragment based on the intent extra
        if (getIntent().getBooleanExtra("addAddress", false)) {
            currentFragment = new DeliveryAddressInput2Fragment();
            loadFragment(new DeliveryAddressInput2Fragment());
        } else {
            currentFragment = new DeliveryAddressInput1Fragment();
            String id = getIntent().getStringExtra("id");
            Bundle bundle = new Bundle();
            bundle.putString("id",id);
            currentFragment.setArguments(bundle);
            loadFragment(currentFragment);
        }

        // Handle the back button click event
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();  // Calls onBackPressed() when the back button is clicked
            }
        });

        // Handle the back press behavior via OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentFragment instanceof DeliveryAddressInput2Fragment) {
                    // If on DeliveryAddressInput2Fragment, navigate to DeliveryAddressInput1Fragment
                    loadFragment(new DeliveryAddressInput1Fragment());
                } else if (currentFragment instanceof DeliveryAddressInput1Fragment) {
                    // If on DeliveryAddressInput1Fragment, fall back to the previous activity
                    setEnabled(false); // Disable this callback to use default back press behavior
                    DeliveryAddressInputActivity.super.onBackPressed(); // This will finish the activity (back to previous)
                }
            }
        });
    }
    // Method to load a new fragment and replace the current one
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)  // Replace the container with the new fragment
                .commit();
        currentFragment = fragment;  // Update the current fragment reference
    }

    // Method to edit the delivery address, which loads DeliveryAddressInput2Fragment with a bundle
    public void editingAddress(String id, String addresstype, String firstname, String lastname, String email, String phone, String apartment, String street, String city, String state, String country, String pincode) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("addressType", addresstype);
        bundle.putString("firstName", firstname);
        bundle.putString("lastName", lastname);
        bundle.putString("email", email);
        bundle.putString("phone", phone);
        bundle.putString("apartment", apartment);
        bundle.putString("street", street);  // Fixed: used correct 'street' instead of 'state'
        bundle.putString("city", city);
        bundle.putString("state", state);
        bundle.putString("country", country);
        bundle.putString("pincode", pincode);

        // Create a new DeliveryAddressInput2Fragment instance with the bundle arguments
        DeliveryAddressInput2Fragment fragment = new DeliveryAddressInput2Fragment();
        fragment.setArguments(bundle);

        // Replace the current fragment with the new one that contains the arguments
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)  // Replace with the new fragment
                .commit();

        currentFragment = fragment;  // Update currentFragment to the new one
    }
}
