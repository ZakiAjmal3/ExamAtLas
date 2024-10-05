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

import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.listeners.MeetingEventListener;

public class JoinLiveClassActivity extends AppCompatActivity {
    String meetingID;
    private Meeting meeting;
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiIyMjU1NDRiYy0wMjc0LTQ5MTgtODlhNy0wODFkMDFiYzA0ZmIiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTcyNzI1MjY5NiwiZXhwIjoxNzI3ODU3NDk2fQ.r52Y0TIm1wePSw0dfJ2nhiLr7Ztf3u9Wxq72diIOtI8";
    private static final int PERMISSION_REQ_ID = 22;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_live_class);

        if (getIntent().getStringExtra("meetingID") != null){
//            meetingID = getIntent().getStringExtra("meetingID");
            meetingID = "4edd-ct67-yb4j";
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
        VideoSDK.initialize(getApplicationContext());

        // Configuration VideoSDK with Token
        VideoSDK.config(token);

        // Initialize VideoSDK Meeting
        meeting = VideoSDK.initMeeting(
                JoinLiveClassActivity.this, meetingID, localParticipantName,
                false, false, null, "VIEWER", false, null, null);

        // join Meeting
        meeting.join();
        // if mode is CONFERENCE than replace mainLayout with LiveClassSpeakerFragment otherwise with ViewerFragment
//        meeting.addEventListener(new MeetingEventListener() {
//            @Override
//            public void onMeetingJoined() {
////                Log.d("LiveClassesViewerFragment", "Meeting joined");
//
//                if (meeting != null) {
//                    //                    if (mode.equals("CONFERENCE")) {
////                        //pin the local participant
//////                        meeting.getLocalParticipant().pin("SHARE_AND_CAM");
//////                        getSupportFragmentManager()
//////                                .beginTransaction()
//////                                .replace(R.id.main, new LiveClassSpeakerFragment(), "MainFragment")
//////                                .commit();
////                    } else if (mode.equals("VIEWER")) {
//                        getSupportFragmentManager()
//                                .beginTransaction()
//                                .replace(R.id.main, new LiveClassesViewerFragment(), "viewerFragment")
//                                .commit();
//                }
//            }
//        });
//    }
        meeting.addEventListener(new MeetingEventListener() {
            @Override
            public void onMeetingJoined() {
                // Check if meeting is successfully joined
                if (meeting != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main, new LiveClassesViewerFragment(), "viewerFragment")
                            .commit();
                }
            }
        });
    }
        public Meeting getMeeting() {
        return meeting;
    }
}