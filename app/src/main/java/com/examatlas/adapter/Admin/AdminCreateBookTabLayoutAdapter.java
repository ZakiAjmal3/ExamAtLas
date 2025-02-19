package com.examatlas.adapter.Admin;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.examatlas.fragment.Admin.AdminCreateBookTabFragment;
import com.examatlas.fragment.Admin.AdminCreateBooksFragment;
import com.examatlas.fragment.Admin.AdminCreateEBookTabFragment;

public class AdminCreateBookTabLayoutAdapter extends FragmentStateAdapter {
    public AdminCreateBookTabLayoutAdapter(AdminCreateBooksFragment fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("ViewPagerAdapter", "Creating fragment for position: " + position);
        switch (position) {
            case 1:
                return new AdminCreateEBookTabFragment();
            default:
                return new AdminCreateBookTabFragment();
        }
    }
    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
}
