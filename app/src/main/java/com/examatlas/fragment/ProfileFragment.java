package com.examatlas.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.examatlas.R;
import com.examatlas.activities.AdminDashboardActivity;
import com.examatlas.activities.Books.MyBookOrderHistory;
import com.examatlas.activities.DashboardActivity;
import com.examatlas.activities.LoginWithEmailActivity;
import com.examatlas.activities.MainActivity;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.hdodenhof.circleimageview.CircleImageView;
public class ProfileFragment extends Fragment {

    SessionManager sessionManager;
    RelativeLayout loginLayout,logoutLayout, layoutMyOrders,layoutAdminDashboard;
    CircleImageView imgUser;
    ImageView imgEdit;
    ProgressBar profileProgress;
    TextView txtName, txtEmail, txtNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.seed));

        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtNumber = view.findViewById(R.id.txtNumber);
        loginLayout = view.findViewById(R.id.loginLayout);
        logoutLayout = view.findViewById(R.id.logoutLayout);
        profileProgress = view.findViewById(R.id.profileProgress);
        layoutMyOrders = view.findViewById(R.id.layoutMyOrders);
        layoutAdminDashboard = view.findViewById(R.id.layoutAdminDashboard);
        if (sessionManager.getUserData().get("role").equalsIgnoreCase("student")){
            layoutAdminDashboard.setVisibility(View.GONE);
        }else {
            layoutAdminDashboard.setVisibility(View.VISIBLE);
        }
        imgUser = view.findViewById(R.id.imgUser);
        imgEdit = view.findViewById(R.id.imgEdit);
        sessionManager = new  SessionManager(getContext());

        txtName.setText(sessionManager.getUserData().get("firstName") + " " + sessionManager.getUserData().get("lastName"));
        txtEmail.setText(sessionManager.getUserData().get("email"));
        txtNumber.setText(sessionManager.getUserData().get("mobile"));

        if (sessionManager.IsLoggedIn()){
            logoutLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
        }else {
            logoutLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
        }

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getContext().getApplicationContext(), EditProfileActivity.class));
            }
        });

        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LoginWithEmailActivity.class));
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog();
            }
        });

        layoutMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext().getApplicationContext(), MyBookOrderHistory.class));
            }
        });
        layoutAdminDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AdminDashboardActivity.class));
            }
        });

        return view;
    }

//    @Override
//    public void onResume() {
////        getProfile(sessionManager.getToken());
//        super.onResume();
//    }

    private void logoutDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout this session?")
                .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sessionManager.logout();
                        startActivity(new Intent(getContext().getApplicationContext(), MainActivity.class));
                        getActivity().finishAffinity();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }).show();

    }

//    public void getProfile(String token) {
//        profileProgress.setVisibility(View.VISIBLE);
//        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
//        Call<ProfileRequest> call = api.getProfile(token);
//        call.enqueue(new Callback<ProfileRequest>() {
//            @Override
//            public void onResponse(Call<ProfileRequest> call, Response<ProfileRequest> response) {
//                profileProgress.setVisibility(View.GONE);
//                if (response.body() != null) {
//                    if (response.body().getStatus()) {
//                        Profile profile = response.body().getProfile();
//                        Glide.with(getContext())
//                                .load(profile.getPhoto())
//                                .error(R.drawable.noimage)
//                                .into(imgUser);
//                        txtName.setText(profile.getName());
//                        txtEmail.setText(profile.getEmail());
//                        txtNumber.setText(profile.getMobile());
//                        sessionManager.saveProfileDetails(profile.getMobile()
//                                , profile.getName()
//                                , profile.getEmail()
//                                , profile.getPhoto());
//                    }
//                } else {
//                    Log.e("BODY", "Body is null");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ProfileRequest> call, Throwable t) {
//                Log.e("EXCEPTION", t.getLocalizedMessage());
//                profileProgress.setVisibility(View.GONE);
//            }
//        });
//    }

}