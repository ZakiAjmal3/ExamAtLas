package com.examatlas.activities.Admin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.examatlas.R;
import com.examatlas.fragment.Admin.AdminCreateBlogsDeleteFragment;
import com.examatlas.fragment.Admin.AdminCreateBooksFragment;
import com.examatlas.fragment.Admin.AdminCreateCategoryFragment;
import com.examatlas.fragment.Admin.AdminCreateCurrentAffairFragment;
import com.examatlas.fragment.Admin.AdminCreateSubCategoryFragment;
import com.examatlas.fragment.Admin.AdminHomeFragment;
import com.examatlas.fragment.Admin.AdminOrdersFragment;
import com.examatlas.fragment.Admin.AdminTrashedDataFragment;
import com.examatlas.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminDashboardActivity extends AppCompatActivity {

    ImageView imgMenu;
    RelativeLayout topBar;
    public BottomNavigationView bottom_navigation;
    public String currentFrag = "HOME";
    SessionManager sessionManager;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        imgMenu = findViewById(R.id.imgMenu);
        imgMenu.setEnabled(false);
        topBar = findViewById(R.id.topBar);

        sessionManager = new SessionManager(this);
        currentFragment = new AdminHomeFragment();
        loadFragment(new AdminHomeFragment());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imgMenu.setEnabled(true);
            }
        },2000);

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrawerDialog();
            }
        });
// Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentFragment instanceof AdminHomeFragment) {
                    // If on AdminHomeFragment, use the default behavior
                    setEnabled(false); // Disable this callback
                } else {
                    // If on another fragment, navigate back to AdminHomeFragment
                    loadFragment(new AdminHomeFragment());
                }
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        currentFragment = fragment;
    }

    Dialog drawerDialog;
    LinearLayout layoutHome,layoutCategory, layoutSubCategory,layoutBlogs,layoutCurrentAffairs,
            layoutBooks,layoutOrders,layoutTrashedData;
    CardView backCardBtn;
    private void showDrawerDialog() {
        drawerDialog = new Dialog(AdminDashboardActivity.this);
        drawerDialog.setContentView(R.layout.admin_custom_drawer_dialog);
        drawerDialog.setCancelable(true);

        layoutHome = drawerDialog.findViewById(R.id.layoutHome);
        layoutCategory = drawerDialog.findViewById(R.id.layoutCategory);
        layoutSubCategory = drawerDialog.findViewById(R.id.layoutSubCategory);
        layoutBlogs = drawerDialog.findViewById(R.id.layoutBlogs);
        layoutCurrentAffairs = drawerDialog.findViewById(R.id.layoutCurrentAffairs);
        layoutBooks = drawerDialog.findViewById(R.id.layoutBooks);
        layoutOrders = drawerDialog.findViewById(R.id.layoutOrders);
        layoutTrashedData = drawerDialog.findViewById(R.id.layoutTrashedData);
        backCardBtn = drawerDialog.findViewById(R.id.cardBack);

        backCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
            }
        });

        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AdminHomeFragment());
                drawerDialog.dismiss();
            }
        });
        layoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AdminCreateCategoryFragment());
                drawerDialog.dismiss();
            }
        });
        layoutSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AdminCreateSubCategoryFragment());
                drawerDialog.dismiss();
            }
        });
        layoutBlogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AdminCreateBlogsDeleteFragment());
                drawerDialog.dismiss();
            }
        });
        layoutCurrentAffairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AdminCreateCurrentAffairFragment());
                drawerDialog.dismiss();
            }
        });
        layoutBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AdminCreateBooksFragment());
                drawerDialog.dismiss();
            }
        });
        layoutOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AdminOrdersFragment());
                drawerDialog.dismiss();
            }
        });
        layoutTrashedData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AdminTrashedDataFragment());
                drawerDialog.dismiss();
            }
        });

        drawerDialog.show();
        drawerDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        drawerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        drawerDialog.getWindow().setGravity(Gravity.TOP);
        drawerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawerDialog.getWindow().setStatusBarColor(getColor(R.color.seed));
        }
    }

    private void shareApplication() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage = "The best mock test application currently i am using right now for my exam preparation. You should download it and try some of its cool features.";
            shareMessage = shareMessage + "http://play.google.com/store/apps/details?id=" + getApplication().getPackageName() + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }
}