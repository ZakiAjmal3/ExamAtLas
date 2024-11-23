package com.examatlas.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.fragment.AdminBlogCreateDeleteFragment;
import com.examatlas.fragment.AdminCoursesCreateDeleteFragment;
import com.examatlas.fragment.AdminCreateCategoryFragment;
import com.examatlas.fragment.AdminCreateLiveCoursesFragment;
import com.examatlas.fragment.AdminCreateSubCategoryFragment;
import com.examatlas.fragment.AdminCreateSubjectFragment;
import com.examatlas.fragment.AdminCreateCurrentAffairFragment;
import com.examatlas.fragment.AdminEBooksCreateDeleteFragment;
import com.examatlas.fragment.AdminHomeFragment;
import com.examatlas.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
        topBar = findViewById(R.id.topBar);

        sessionManager = new SessionManager(this);
        currentFragment = new AdminHomeFragment();
        loadFragment(new AdminHomeFragment());

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
    LinearLayout layoutHome,transLayer, layoutMaster,layoutLogout, layoutShare,layoutContent,layoutWebsiteApp,layoutMarketing,layoutUsers,layoutReports,layoutManage;
    MaterialCardView cardBack;
    private Spinner masterDropdown,contentDropdown,websiteAppDropdown,marketingDropdown,manageDropdown,usersDropdown,reportsDropdown;
    ImageView masterDropdownIcon,contentDropdownIcon,websiteAppDropdownIcon,marketingDropdownIcon,userDropdownIcon,reportDropdownIcon,manageDropdownIcon;
    String [] masterItems = {"Select from below","Subject","Category","Sub Category"};
    String [] contentItems = {"Select from below","Courses","Live Classes","Mock Test","Blog","Current Affairs","Test Series","Bundle","Batch","EBooks","Podcast","Webinar","Digital Products","Free Resource","Telegram","Utilities","Legacy Question Pool","Question Pool","Subscription","News Feed","Communities","Categories","Segments","Tags"};
    String [] websiteAppItems = {"Select from below","Website","Mobile App","Branding","Embeddable","Language","SignUp Settings","Custom Fields"};
    String [] marketingItems = {"Select from below","Messenger","Coupons","Wallet","Referral Code","Integrations","Affiliates","Forms", "CTA","Events"};
    String [] usersItems = {"Select from below","Learners","Groups","Sub-Admins","Contacts"};
    String [] manageItems = {"Select from below","Course Encryption","Discussions","Rating & Reviews","Legacy Answer Reviews","Learner Support"};
    String [] reportsItems = {"Select from below","Enrollments","Transactions","Payment Gateways","Invoices","Progress & Scores","Sales & Marketing","Live Class-Legacy","Live Class","Live Class-Legacy","Custom Fields","Digital Evaluation","Legacy Reports","Exports","Broadcast Message","Resource Usage","Messenger Insights","School Payout"};
    String masterItemsSelected,contentItemsSelected;
    private void showDrawerDialog() {
        drawerDialog = new Dialog(AdminDashboardActivity.this);
        drawerDialog.setContentView(R.layout.admin_custom_drawer_dialog);
        drawerDialog.setCancelable(true);

        masterDropdown = drawerDialog.findViewById(R.id.masterDropdown);
        contentDropdown = drawerDialog.findViewById(R.id.contentDropdown);
        websiteAppDropdown = drawerDialog.findViewById(R.id.websiteAppDropdown);
        marketingDropdown = drawerDialog.findViewById(R.id.marketingDropdown);
        usersDropdown = drawerDialog.findViewById(R.id.usersDropdown);
        reportsDropdown = drawerDialog.findViewById(R.id.reportsDropdown);
        manageDropdown = drawerDialog.findViewById(R.id.manageDropdown);

        masterDropdownIcon = drawerDialog.findViewById(R.id.masterDropDownIcon);
        contentDropdownIcon = drawerDialog.findViewById(R.id.contentDropDownIcon);
        websiteAppDropdownIcon = drawerDialog.findViewById(R.id.websiteAppDropDownIcon);
        marketingDropdownIcon = drawerDialog.findViewById(R.id.marketingDropDownIcon);
        userDropdownIcon = drawerDialog.findViewById(R.id.usersDropDownIcon);
        reportDropdownIcon = drawerDialog.findViewById(R.id.reportsDropDownIcon);
        manageDropdownIcon = drawerDialog.findViewById(R.id.manageDropDownIcon);

        layoutHome = drawerDialog.findViewById(R.id.layoutHome);
        transLayer = drawerDialog.findViewById(R.id.transLayer);
        layoutMaster = drawerDialog.findViewById(R.id.layoutMaster);
        layoutContent = drawerDialog.findViewById(R.id.layoutContent);
        layoutWebsiteApp = drawerDialog.findViewById(R.id.layoutWebsiteApp);
        layoutMarketing = drawerDialog.findViewById(R.id.layoutMarketing);
        layoutUsers = drawerDialog.findViewById(R.id.layoutUsers);
        layoutManage = drawerDialog.findViewById(R.id.layoutManage);
        layoutReports = drawerDialog.findViewById(R.id.layoutReports);

        layoutLogout = drawerDialog.findViewById(R.id.layoutLogout);
        layoutShare = drawerDialog.findViewById(R.id.layoutShare);
        cardBack = drawerDialog.findViewById(R.id.cardBack);

        setUpSpinners();

        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AdminHomeFragment());
                currentFragment = new AdminHomeFragment();
                drawerDialog.dismiss();

            }
        });

        layoutMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (masterDropdown.getVisibility() == View.VISIBLE) {
                    masterDropdown.setVisibility(View.GONE);
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown)
                            .placeholder(R.drawable.dropdown)
                            .error(R.drawable.dropdown)
                            .into(masterDropdownIcon);
                } else {
                    masterDropdown.setVisibility(View.VISIBLE);
                    masterDropdown.performClick();
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown_up)
                            .placeholder(R.drawable.dropdown_up)
                            .error(R.drawable.dropdown_up)
                            .into(masterDropdownIcon);
                }
            }
        });
        layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contentDropdown.getVisibility() == View.VISIBLE) {
                    contentDropdown.setVisibility(View.GONE);
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown)
                            .placeholder(R.drawable.dropdown)
                            .error(R.drawable.dropdown)
                            .into(contentDropdownIcon);
                } else {
                    contentDropdown.setVisibility(View.VISIBLE);
                    contentDropdown.performClick();
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown_up)
                            .placeholder(R.drawable.dropdown_up)
                            .error(R.drawable.dropdown_up)
                            .into(contentDropdownIcon);
                }
            }
        });
        layoutWebsiteApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (websiteAppDropdown.getVisibility() == View.VISIBLE) {
                    websiteAppDropdown.setVisibility(View.GONE);
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown)
                            .placeholder(R.drawable.dropdown)
                            .error(R.drawable.dropdown)
                            .into(websiteAppDropdownIcon);
                } else {
                    websiteAppDropdown.setVisibility(View.VISIBLE);
                    websiteAppDropdown.performClick();
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown_up)
                            .placeholder(R.drawable.dropdown_up)
                            .error(R.drawable.dropdown_up)
                            .into(websiteAppDropdownIcon);
                }
            }
        });
        layoutMarketing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marketingDropdown.getVisibility() == View.VISIBLE) {
                    marketingDropdown.setVisibility(View.GONE);
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown)
                            .placeholder(R.drawable.dropdown)
                            .error(R.drawable.dropdown)
                            .into(marketingDropdownIcon);
                } else {
                    marketingDropdown.setVisibility(View.VISIBLE);
                    marketingDropdown.performClick();
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown_up)
                            .placeholder(R.drawable.dropdown_up)
                            .error(R.drawable.dropdown_up)
                            .into(marketingDropdownIcon);
                }
            }
        });
        layoutUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usersDropdown.getVisibility() == View.VISIBLE) {
                    usersDropdown.setVisibility(View.GONE);
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown)
                            .placeholder(R.drawable.dropdown)
                            .error(R.drawable.dropdown)
                            .into(userDropdownIcon);
                } else {
                    usersDropdown.setVisibility(View.VISIBLE);
                    usersDropdown.performClick();
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown_up)
                            .placeholder(R.drawable.dropdown_up)
                            .error(R.drawable.dropdown_up)
                            .into(userDropdownIcon);
                }
            }
        });
        layoutReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reportsDropdown.getVisibility() == View.VISIBLE) {
                    reportsDropdown.setVisibility(View.GONE);
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown)
                            .placeholder(R.drawable.dropdown)
                            .error(R.drawable.dropdown)
                            .into(reportDropdownIcon);
                } else {
                    reportsDropdown.setVisibility(View.VISIBLE);
                    reportsDropdown.performClick();
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown_up)
                            .placeholder(R.drawable.dropdown_up)
                            .error(R.drawable.dropdown_up)
                            .into(reportDropdownIcon);
                }
            }
        });
        layoutManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manageDropdown.getVisibility() == View.VISIBLE) {
                    manageDropdown.setVisibility(View.GONE);
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown)
                            .placeholder(R.drawable.dropdown)
                            .error(R.drawable.dropdown)
                            .into(manageDropdownIcon);
                } else {
                    manageDropdown.setVisibility(View.VISIBLE);
                    manageDropdown.performClick();
                    Glide.with(AdminDashboardActivity.this)
                            .load(R.drawable.dropdown_up)
                            .placeholder(R.drawable.dropdown_up)
                            .error(R.drawable.dropdown_up)
                            .into(manageDropdownIcon);
                }
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                quitDialog();
            }
        });

        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                shareApplication();
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
    @SuppressLint("ResourceAsColor")
    private void setUpSpinners() {
        setupSpinner(masterDropdown, masterItems, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    masterItemsSelected = masterItems[position];
                    if (masterItemsSelected.equals("Subject")){
                        loadFragment(new AdminCreateSubjectFragment());
                        currentFragment = new AdminCreateSubjectFragment();
                        drawerDialog.dismiss();
                    }else if (masterItemsSelected.equals("Category")){
                        loadFragment(new AdminCreateCategoryFragment());
                        currentFragment = new AdminCreateCategoryFragment();
                        drawerDialog.dismiss();
                    }else if (masterItemsSelected.equals("Sub Category")){
                        loadFragment(new AdminCreateSubCategoryFragment());
                        currentFragment = new AdminCreateSubCategoryFragment();
                        drawerDialog.dismiss();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Handle no selection
                    masterDropdown.setVisibility(View.GONE);
                }
            });
        setupSpinner(contentDropdown, contentItems, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Handle item selection
                    contentItemsSelected = contentItems[position];
                    if (contentItemsSelected.equals("Blog")){
                        loadFragment(new AdminBlogCreateDeleteFragment());
                        currentFragment = new AdminBlogCreateDeleteFragment();
                        drawerDialog.dismiss();
                    } else if(contentItemsSelected.equals("Current Affairs")){
                        loadFragment(new AdminCreateCurrentAffairFragment());
                        currentFragment = new AdminCreateCurrentAffairFragment();
                        drawerDialog.dismiss();
                    } else if (contentItemsSelected.equals("Courses")){
                        loadFragment(new AdminCoursesCreateDeleteFragment());
                        currentFragment = new AdminCoursesCreateDeleteFragment();
                        drawerDialog.dismiss();
                    } else if (contentItemsSelected.equals("EBooks")){
                        loadFragment(new AdminEBooksCreateDeleteFragment());
                        currentFragment = new AdminEBooksCreateDeleteFragment();
                        drawerDialog.dismiss();
                    } else if (contentItemsSelected.equals("Live Classes")){
                        loadFragment(new AdminCreateLiveCoursesFragment());
                        currentFragment = new AdminCreateLiveCoursesFragment();
                        drawerDialog.dismiss();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Handle no selection
                    contentDropdown.setVisibility(View.GONE);
                }
            });
        setupSpinner(websiteAppDropdown, websiteAppItems, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Handle item selection
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Handle no selection
                    websiteAppDropdown.setVisibility(View.GONE);
                }
            });
        setupSpinner(marketingDropdown, marketingItems, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Handle item selection
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Handle no selection
                    marketingDropdown.setVisibility(View.GONE);
                }
            });
        setupSpinner(usersDropdown, usersItems, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Handle item selection
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Handle no selection
                    usersDropdown.setVisibility(View.GONE);
                }
            });
        setupSpinner(reportsDropdown, reportsItems, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Handle item selection
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Handle no selection
                    reportsDropdown.setVisibility(View.GONE);
                }
            });
        setupSpinner(manageDropdown, manageItems, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Handle item selection
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Handle no selection
                    manageDropdown.setVisibility(View.GONE);
                }
            });
        }

    private void setupSpinner(Spinner spinner, String[] items, AdapterView.OnItemSelectedListener listener) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.admin_spinner_items, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(listener);
    }

    private void quitDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout this session?")
                .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SessionManager sessionManager = new SessionManager(AdminDashboardActivity.this);
                        sessionManager.logout();
                        startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
                        Toast.makeText(AdminDashboardActivity.this, "Logout Successfully...", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(AdminDashboardActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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