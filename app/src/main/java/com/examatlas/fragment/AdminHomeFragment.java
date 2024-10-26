package com.examatlas.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.examatlas.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeFragment extends Fragment {

    public BottomNavigationView homeNavigation;
    public String currentFrag = "SALES";
    Fragment currentFragment ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_home, container, false);

        homeNavigation = view.findViewById(R.id.home_navigation);
        currentFragment = new AdminSalesFragment();

        loadFragment(currentFragment);
        homeNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.sales) {
                    currentFrag = "SALES";
                    currentFragment = new AdminSalesFragment();
                    loadFragment(new AdminSalesFragment());
                } else if (item.getItemId() == R.id.learner) {
                    currentFrag = "LEARNERS";
                    currentFragment = new AdminLearnersFragment();
                    loadFragment(new AdminLearnersFragment());
                } else if (item.getItemId() == R.id.order) {
                    currentFrag = "ORDERS";
                    currentFragment = new AdminOrderFragment();
                    loadFragment(new AdminOrderFragment());
                } else if (item.getItemId() == R.id.apps) {
                    currentFrag = "APPS";
                    currentFragment = new AdminAppsFragment();
                    loadFragment(new AdminAppsFragment());
                }
                return true;
            }
        });
        return view;
    }
    public void loadFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        currentFragment = fragment;
    }
}
