package com.examatlas.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.examatlas.R;
import com.examatlas.activities.Books.EBooks.EBookSeachingActivity;
import com.examatlas.activities.Books.MyBookOrderHistory;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.fragment.BooksFragment;
import com.examatlas.fragment.EbookLibraryFragment;
import com.examatlas.fragment.HomeFragment;
import com.examatlas.fragment.ProfileFragment;
import com.examatlas.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {
    ImageView imgMenu, imgSearch;
    RelativeLayout topBar, liveNotificationLayout;
    MaterialCardView cardJoin;
    LottieAnimationView animationView;
    public BottomNavigationView bottom_navigation;
    public String currentFrag = "HOME";
    SessionManager sessionManager;
    Fragment currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasbboard);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        imgMenu = findViewById(R.id.imgMenu);
        imgSearch = findViewById(R.id.imgSearch);
        topBar = findViewById(R.id.topBar);
        cardJoin = findViewById(R.id.cardJoin);
        animationView = findViewById(R.id.animationView);
        liveNotificationLayout = findViewById(R.id.liveNotificationLayout);
        sessionManager = new SessionManager(this);
        currentFragment = new HomeFragment();
        loadFragment(new HomeFragment());

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    currentFrag = "HOME";
                    topBar.setVisibility(View.VISIBLE);
                    loadFragment(new HomeFragment());
                } else if (item.getItemId() == R.id.blogs) {
                    currentFrag = "BLOGS";
                    topBar.setVisibility(View.GONE);
                    loadFragment(new BlogFragment());
//                } else if (item.getItemId() == R.id.live) {
//                    currentFrag = "LIVE";
//                    topBar.setVisibility(View.VISIBLE);
//                    loadFragment(new LiveCoursesFragment());
                } else if (item.getItemId() == R.id.books) {
                    currentFrag = "BOOKS";
                    topBar.setVisibility(View.GONE);
                    loadFragment(new BooksFragment());
                } else if (item.getItemId() == R.id.library) {
                    currentFrag = "EBOOKS";
                    topBar.setVisibility(View.GONE);
                    loadFragment(new EbookLibraryFragment());
                } else {
                    if (sessionManager.IsLoggedIn()) {
                        currentFrag = "PROFILE";
                        topBar.setVisibility(View.GONE);
                        loadFragment(new ProfileFragment());
                    }else {
                        Intent intent = new Intent(DashboardActivity.this, LoginWithEmailActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                return true;
            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrawerDialog();
            }
        });
//        imgSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(DashboardActivity.this, SearchActivity.class));
//            }
//        });

        cardJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(DashboardActivity.this, LiveClassActivity.class));
            }
        });
// Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentFragment instanceof HomeFragment) {
                    // If on HomeFragment, use the default behavior
                    setEnabled(false); // Disable this callback
                } else {
                    // If on another fragment, navigate back to HomeFragment
                    loadFragment(new HomeFragment());
                    bottom_navigation.setSelectedItemId(R.id.home);
                }
            }
        });
    }

    public void showTopToolBar(String callingThisMethodFrom){
        topBar.setVisibility(View.VISIBLE);
        if (callingThisMethodFrom.equals("BlogFragment")){
            bottom_navigation.setSelectedItemId(R.id.home);
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        currentFragment = fragment;
    }
    public void loadCurrentAffairActivity(){
        Intent intent = new Intent(this, CurrentAffairsActivity.class);
        startActivity(intent);
    }
//    public void loadCourse() {
//        currentFrag = "TEST";
//        loadFragment(new HomeFragment());
//        bottom_navigation.setSelectedItemId(R.id.test);
//        bottom_navigation.setSelected(true);
//    }

    Dialog drawerDialog;
    LinearLayout transLayer, layoutHome, layoutBlogs, layoutPurchaseBooks,layoutCurrentAffairs, layoutLogout,layoutLogin, layoutShare,layoutEbook,layoutAdminDashboard,
             layoutOrderHistory;
    TextView txtUsername, txtUserEmail;
    CircleImageView imgUser;
    MaterialCardView cardBack;

    private void showDrawerDialog() {
        drawerDialog = new Dialog(DashboardActivity.this);
        drawerDialog.setContentView(R.layout.custom_drawer_dialog);
        drawerDialog.setCancelable(true);

        transLayer = drawerDialog.findViewById(R.id.transLayer);
        layoutHome = drawerDialog.findViewById(R.id.layoutHome);
        layoutBlogs = drawerDialog.findViewById(R.id.layoutBlogs);
        layoutPurchaseBooks = drawerDialog.findViewById(R.id.layoutPurchaseBooks);
        layoutEbook = drawerDialog.findViewById(R.id.layoutEbook);
        layoutCurrentAffairs = drawerDialog.findViewById(R.id.layoutCurrentAffairs);
        layoutLogout = drawerDialog.findViewById(R.id.layoutLogout);
        layoutLogin = drawerDialog.findViewById(R.id.layoutLogin);
        layoutShare = drawerDialog.findViewById(R.id.layoutShare);
        txtUsername = drawerDialog.findViewById(R.id.txtUsername);
        txtUserEmail = drawerDialog.findViewById(R.id.txtUserEmail);
        layoutOrderHistory = drawerDialog.findViewById(R.id.layoutMyOrders);
        cardBack = drawerDialog.findViewById(R.id.cardBack);
        imgUser = drawerDialog.findViewById(R.id.imgUser);

        layoutAdminDashboard = drawerDialog.findViewById(R.id.layoutAdminDashboard);
        if (sessionManager.getUserData().get("role").equalsIgnoreCase("student")){
            layoutAdminDashboard.setVisibility(View.GONE);
        }else {
            layoutAdminDashboard.setVisibility(View.VISIBLE);
        }

        String firstName = sessionManager.getUserData().get("firstName");
        String lastName = sessionManager.getUserData().get("lastName");
        txtUsername.setText(firstName + " " + lastName);
        txtUserEmail.setText(sessionManager.getUserData().get("email"));

        if (sessionManager.IsLoggedIn()){
            layoutLogout.setVisibility(View.VISIBLE);
            layoutOrderHistory.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
        }else {
            layoutLogout.setVisibility(View.GONE);
            layoutOrderHistory.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        }

        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                if (!currentFrag.equals("HOME")) {
                    currentFrag = "HOME";
                    loadFragment(new HomeFragment());
                    bottom_navigation.setSelectedItemId(R.id.home);
                    bottom_navigation.setSelected(true);
                }
            }
        });

        layoutBlogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                if (!currentFrag.equals("BLOGS")) {
                    currentFrag = "BLOGS";
                    topBar.setVisibility(View.GONE);
                    loadFragment(new BlogFragment());
                    bottom_navigation.setSelectedItemId(R.id.blogs);
                    bottom_navigation.setSelected(true);
                }
            }
        });

        layoutPurchaseBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                currentFrag = "BOOKS";
                topBar.setVisibility(View.GONE);
                loadFragment(new BooksFragment());
                bottom_navigation.setSelectedItemId(R.id.books);
                bottom_navigation.setSelected(true);
            }
        });

        layoutEbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                startActivity(new Intent(DashboardActivity.this, EBookSeachingActivity.class));
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                quitDialog();
            }
        });
        layoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                startActivity(new Intent(DashboardActivity.this, LoginWithEmailActivity.class));
                finish();
            }
        });
        layoutOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                startActivity(new Intent(DashboardActivity.this, MyBookOrderHistory.class));
            }
        });

        layoutCurrentAffairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, CurrentAffairsActivity.class);
                startActivity(intent);
            }
        });

        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
            }
        });

        transLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
            }
        });
        layoutAdminDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,AdminDashboardActivity.class));
                finish();
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

        private void quitDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout this session?")
                .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SessionManager sessionManager = new SessionManager(DashboardActivity.this);
                        sessionManager.logout();
                        Toast.makeText(DashboardActivity.this, "Logout Successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(DashboardActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }).show();

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