package com.examatlas.fragment.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.viewpager2.widget.ViewPager2;

import com.examatlas.R;
import com.examatlas.adapter.Admin.AdminCreateBookTabLayoutAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class AdminCreateBooksFragment extends Fragment {
    String bookTypeTabSelected = "book";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BooksFragment", "onCreate called");
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_create_books, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        AdminCreateBookTabLayoutAdapter adapter = new AdminCreateBookTabLayoutAdapter(AdminCreateBooksFragment.this);
        viewPager.setAdapter(adapter);

        // Link TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Books");
                        bookTypeTabSelected = "books";
                        break;
                    case 1:
                        tab.setText("EBooks");
                        bookTypeTabSelected = "ebooks";
                        break;
                }
                // Log when a tab is clicked
                tab.view.setOnClickListener(v -> {
                    Log.d("TabClicked", "Tab " + position + " clicked");
                });
            }
        }).attach();
        return view;
    }
    public String getBookTypeTabSelected(){
        return bookTypeTabSelected;
    }
}