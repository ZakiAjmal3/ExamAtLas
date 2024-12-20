package com.examatlas.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.examatlas.R;
import com.examatlas.fragment.AdminLiveClassesViewerFragment;
import com.examatlas.utils.SessionManager;


import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.listeners.MeetingEventListener;

public class AdminJoinLiveClassActivity extends AppCompatActivity {
    String meetingID = "";
    private Meeting meeting;
    String token = "";
    private static final int PERMISSION_REQ_ID = 22;
    String courseId;
    SessionManager sessionManager;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_join_live_class);

        sessionManager = new SessionManager(this);
        userName = sessionManager.getUserData().get("firstName") + " " + sessionManager.getUserData().get("lastName");

        if (getIntent().getStringExtra("meetingID") != null && getIntent().getStringExtra("token") != null){
            meetingID = getIntent().getStringExtra("meetingID");
            token = getIntent().getStringExtra("token");
        }
        if (getIntent().getStringExtra("course_id") != null){
            courseId = getIntent().getStringExtra("course_id");
        }
        checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID);
        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID);
        joinMeeting();
    }
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    private void checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
        }else {
//            joinMeeting();
        }
    }
    public void joinMeeting() {
        String mode = "CONFERENCE";
        boolean streamEnable = mode.equals("CONFERENCE");

        // initialize VideoSDK
        VideoSDK.initialize(getApplicationContext());

        // Configuration VideoSDK with Token
        VideoSDK.config(token);

        // Initialize VideoSDK Meeting
        meeting = VideoSDK.initMeeting(
                AdminJoinLiveClassActivity.this, meetingID, userName,
                true, true, null, "CONFERENCE", true, null, null);

        // join Meeting
        meeting.join();
        meeting.addEventListener(new MeetingEventListener() {
            @Override
            public void onMeetingJoined() {
                // Check if meeting is successfully joined
                if (meeting != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main, new AdminLiveClassesViewerFragment(), "viewerFragment")
                            .commit();
                }
            }
        });
    }
    public Meeting getMeeting() {
        return meeting;
    }
    public String getCourseId() {
        return courseId;
    }
}