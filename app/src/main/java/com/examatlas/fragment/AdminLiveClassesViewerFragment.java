package com.examatlas.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.activities.AdminDashboardActivity;
import com.examatlas.activities.AdminJoinLiveClassActivity;
import com.examatlas.activities.AdminLiveCoursesClassesActivity;
import com.examatlas.activities.DashboardActivity;
import com.examatlas.adapter.AdminLiveClassesViewerAdapter;

import org.json.JSONObject;

import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.lib.JsonUtils;
import live.videosdk.rtc.android.lib.transcription.PostTranscriptionConfig;
import live.videosdk.rtc.android.lib.transcription.SummaryConfig;

public class AdminLiveClassesViewerFragment extends Fragment {

    private static Activity mActivity;
    private static Context mContext;
    private static Meeting meeting;
    private boolean micEnabled = true;
    private boolean webcamEnabled = true;
    private boolean hlsEnabled = false;
    private Button btnMic, btnWebcam, btnHls, btnLeave;
    private TextView tvMeetingId, tvHlsState;

    public AdminLiveClassesViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
            // getting meeting object from Meeting Activity
            meeting = ((AdminJoinLiveClassActivity) mActivity).getMeeting();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_live_classes_viewer, container, false);
        btnMic = view.findViewById(R.id.btnMic);
        btnWebcam = view.findViewById(R.id.btnWebcam);
        btnHls = view.findViewById(R.id.btnHLS);
        btnLeave = view.findViewById(R.id.btnLeave);

        tvMeetingId = view.findViewById(R.id.tvMeetingId);
        tvHlsState = view.findViewById(R.id.tvHlsState);

        if (meeting != null) {
            tvMeetingId.setText("Meeting Id : " + meeting.getMeetingId());
            setActionListeners();
            final RecyclerView rvParticipants = view.findViewById(R.id.rvParticipants);
            rvParticipants.setLayoutManager(new GridLayoutManager(mContext, 2));
            rvParticipants.setAdapter(new AdminLiveClassesViewerAdapter(meeting));
        }
        return view;
    }

    private void setActionListeners() {
        btnMic.setOnClickListener(v -> {
            if (micEnabled) {
                meeting.muteMic();
                Toast.makeText(mContext,"Mic Muted",Toast.LENGTH_SHORT).show();
            } else {
                meeting.unmuteMic();
                Toast.makeText(mContext,"Mic Enabled",Toast.LENGTH_SHORT).show();
            }
            micEnabled=!micEnabled;
        });

        btnWebcam.setOnClickListener(v -> {
            if (webcamEnabled) {
                meeting.disableWebcam();
                Toast.makeText(mContext,"Webcam Disabled",Toast.LENGTH_SHORT).show();
            } else {
                meeting.enableWebcam();
                Toast.makeText(mContext,"Webcam Enabled",Toast.LENGTH_SHORT).show();
            }
            webcamEnabled=!webcamEnabled;
        });

        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meeting.leave();
                startActivity(new Intent(getContext(), AdminDashboardActivity.class));
            }
        });

        btnHls.setOnClickListener(v -> {
            if (!hlsEnabled) {
                try {
                    JSONObject config = new JSONObject();
                    JSONObject layout = new JSONObject();
                    JsonUtils.jsonPut(layout, "type", "SPOTLIGHT");
                    JsonUtils.jsonPut(layout, "priority", "PIN");
                    JsonUtils.jsonPut(layout, "gridSize", 4);
                    JsonUtils.jsonPut(config, "layout", layout);
                    JsonUtils.jsonPut(config, "orientation", "portrait");
                    JsonUtils.jsonPut(config, "theme", "DARK");
                    JsonUtils.jsonPut(config, "quality", "high");

                    // Create SummaryConfig instance with appropriate parameters
                    boolean someBooleanValue = true; // Adjust as needed
                    String someStringValue = "your_summary_string"; // Replace with an appropriate string
                    SummaryConfig summaryConfig = new SummaryConfig(someBooleanValue, someStringValue);

                    // Create an instance of PostTranscriptionConfig
                    PostTranscriptionConfig transcriptionConfig = new PostTranscriptionConfig(someBooleanValue, summaryConfig, someStringValue);

                    // Starting HLS stream
                    meeting.startHls(config, transcriptionConfig);
                    hlsEnabled = true; // Update the state
                    Toast.makeText(mContext, "HLS Started", Toast.LENGTH_SHORT).show();
                    Log.d("HLS", "HLS stream started with config: " + config.toString());
                } catch (Exception e) {
                    Log.e("HLS", "Error starting HLS: ", e);
                    Toast.makeText(mContext, "Error starting HLS", Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    meeting.stopHls();
                    hlsEnabled = false; // Update the state
                    Toast.makeText(mContext, "HLS Stopped", Toast.LENGTH_SHORT).show();
                    Log.d("HLS", "HLS stream stopped");
                } catch (Exception e) {
                    Log.e("HLS", "Error stopping HLS: ", e);
                    Toast.makeText(mContext, "Error stopping HLS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}