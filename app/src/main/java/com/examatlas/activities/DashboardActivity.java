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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.examatlas.R;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.fragment.BooksFragment;
import com.examatlas.fragment.CourseFragment;
import com.examatlas.fragment.LiveCoursesFragment;
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
                    topBar.setVisibility(View.VISIBLE);
                    loadFragment(new BlogFragment());
//                } else if (item.getItemId() == R.id.live) {
//                    currentFrag = "LIVE";
//                    topBar.setVisibility(View.VISIBLE);
//                    loadFragment(new LiveCoursesFragment());
                } else if (item.getItemId() == R.id.books) {
                    currentFrag = "BOOKS";
                    topBar.setVisibility(View.GONE);
                    loadFragment(new BooksFragment());
                } else {
                    currentFrag = "PROFILE";
                    topBar.setVisibility(View.GONE);
                    loadFragment(new ProfileFragment());
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
//
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
    LinearLayout transLayer, layoutHome, layoutBlogs, layoutPurchaseBooks, layoutLiveClasses,
            layoutEbook,layoutCurrentAffairs, layoutLogout,layoutLogin, layoutShare, layoutAboutUs, layoutPrivacy,
            layoutTerms, layoutOrderHistory, layoutFaq, layoutServices;
    TextView txtUsername, txtUserEmail;
    CircleImageView imgUser;
    MaterialCardView cardBack;

    private void showDrawerDialog() {
        drawerDialog = new Dialog(DashboardActivity.this);
        drawerDialog.setContentView(R.layout.custom_drawer_dialog);
        drawerDialog.setCancelable(true);

        transLayer = drawerDialog.findViewById(R.id.transLayer);
        layoutHome = drawerDialog.findViewById(R.id.layoutHome);
        layoutLiveClasses = drawerDialog.findViewById(R.id.layoutLiveClasses);
        layoutBlogs = drawerDialog.findViewById(R.id.layoutBlogs);
        layoutPurchaseBooks = drawerDialog.findViewById(R.id.layoutPurchaseBooks);
//        layoutEbook = drawerDialog.findViewById(R.id.layoutEbook);
        layoutCurrentAffairs = drawerDialog.findViewById(R.id.layoutCurrentAffairs);
        layoutLogout = drawerDialog.findViewById(R.id.layoutLogout);
        layoutLogin = drawerDialog.findViewById(R.id.layoutLogin);
        layoutShare = drawerDialog.findViewById(R.id.layoutShare);
        txtUsername = drawerDialog.findViewById(R.id.txtUsername);
        txtUserEmail = drawerDialog.findViewById(R.id.txtUserEmail);
//        layoutAboutUs = drawerDialog.findViewById(R.id.layoutAboutUs);
//        layoutPrivacy = drawerDialog.findViewById(R.id.layoutPrivacy);
//        layoutTerms = drawerDialog.findViewById(R.id.layoutTerms);
//        layoutOrderHistory = drawerDialog.findViewById(R.id.layoutOrderHistory);
        cardBack = drawerDialog.findViewById(R.id.cardBack);
        imgUser = drawerDialog.findViewById(R.id.imgUser);
//        layoutFaq = drawerDialog.findViewById(R.id.layoutFaq);
//        layoutServices = drawerDialog.findViewById(R.id.layoutServices);

        String firstName = sessionManager.getUserData().get("firstName");
        String lastName = sessionManager.getUserData().get("lastName");
        txtUsername.setText(firstName + " " + lastName);
        txtUserEmail.setText(sessionManager.getUserData().get("email"));

        if (sessionManager.IsLoggedIn()){
            layoutLogout.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
        }else {
            layoutLogout.setVisibility(View.GONE);
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
                startActivity(new Intent(DashboardActivity.this, HardBookECommPurchaseActivity.class));
            }
        });

//        layoutLiveClasses.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerDialog.dismiss();
//                if (!currentFrag.equals("LIVE")) {
//                    currentFrag = "LIVE";
//                    loadFragment(new LiveCoursesFragment());
//                    bottom_navigation.setSelectedItemId(R.id.live);
//                    bottom_navigation.setSelected(true);
//                }
//            }
//        });

        layoutEbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                startActivity(new Intent(DashboardActivity.this, EbookActivity.class));
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
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();
            }
        });

        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                shareApplication();
            }
        });

//        layoutAboutUs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerDialog.dismiss();
//                Intent intent = new Intent(DashboardActivity.this, WebViewActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("title", "About Us");
//                intent.putExtra("url", "https://examatlas.com");
//                startActivity(intent);
//            }
//        });

//        layoutPrivacy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerDialog.dismiss();
//                Intent intent = new Intent(DashboardActivity.this, WebViewActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("title", "Privacy Policy");
//                intent.putExtra("url", "https://examatlas.com");
//                startActivity(intent);
//            }
//        });

//        layoutTerms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerDialog.dismiss();
//                Intent intent = new Intent(DashboardActivity.this, WebViewActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("title", "Terms & Conditions");
//                intent.putExtra("url", "https://examatlas.com");
//                startActivity(intent);
//            }
//        });

        layoutOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                Intent intent = new Intent(DashboardActivity.this, BookOrderHistoryActivity.class);
                startActivity(intent);
            }
        });
        layoutCurrentAffairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, CurrentAffairsActivity.class);
                startActivity(intent);
            }
        });

//        layoutFaq.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerDialog.dismiss();
//                Intent intent = new Intent(DashboardActivity.this, WebActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("title", "FAQs");
//                intent.putExtra("url", "https://examatlas.com/faq");
//                startActivity(intent);
//            }
//        });
//        layoutServices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerDialog.dismiss();
//                Intent intent = new Intent(DashboardActivity.this, WebActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("title", "Our Services");
//                intent.putExtra("url", "https://examatlas.com/services");
//                startActivity(intent);
//            }
//        });

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