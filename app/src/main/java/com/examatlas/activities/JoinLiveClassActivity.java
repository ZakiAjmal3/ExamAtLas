package com.examatlas.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.examatlas.R;
import com.examatlas.fragment.LiveClassesViewerFragment;
import com.google.android.exoplayer2.util.Log;

//import live.videosdk.rtc.android.Meeting;
//import live.videosdk.rtc.android.VideoSDK;
//import live.videosdk.rtc.android.listeners.MeetingEventListener;

public class JoinLiveClassActivity extends AppCompatActivity {
    String meetingID = "";
//    private Meeting meeting;
    String token = "";
    private static final int PERMISSION_REQ_ID = 22;
    String courseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_live_class);

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
        String mode = "VIEWER";
        String localParticipantName = "Zaki Ajmal";
        boolean streamEnable = mode.equals("CONFERENCE");

        // initialize VideoSDK
//        VideoSDK.initialize(getApplicationContext());
//
//        // Configuration VideoSDK with Token
//        VideoSDK.config(token);
//
//        // Initialize VideoSDK Meeting
//        meeting = VideoSDK.initMeeting(
//                JoinLiveClassActivity.this, meetingID, localParticipantName,
//                false, false, null, "VIEWER", false, null, null);
//
//        // join Meeting
//        meeting.join();
//        meeting.addEventListener(new MeetingEventListener() {
//            @Override
//            public void onMeetingJoined() {
//                // Check if meeting is successfully joined
//                if (meeting != null) {
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.main, new LiveClassesViewerFragment(), "viewerFragment")
//                            .commit();
//                }
//            }
//        });
    }
//    public Meeting getMeeting() {
//        return meeting;
//    }
    public String getCourseId() {
        return courseId;
    }
}