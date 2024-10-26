package com.examatlas.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.examatlas.R;
import com.examatlas.activities.DashboardActivity;
import com.examatlas.activities.JoinLiveClassActivity;
import com.examatlas.activities.LiveCoursesClassesListActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.listeners.MeetingEventListener;

public class LiveClassesViewerFragment extends Fragment {

    private Meeting meeting;
    protected StyledPlayerView playerView;
    private TextView waitingLayout;
    protected @Nullable ExoPlayer player;

    private DefaultHttpDataSource.Factory dataSourceFactory;
    private boolean startAutoPlay = true;
    private String playbackHlsUrl = "";
    private Activity mActivity;
    private Context mContext;

    public LiveClassesViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore playback URL if fragment is recreated
        if (savedInstanceState != null) {
            playbackHlsUrl = savedInstanceState.getString("playbackHlsUrl", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_classes_viewer_fragment, container, false);

        playerView = view.findViewById(R.id.player_view);
        waitingLayout = view.findViewById(R.id.waitingLayout);

        if (meeting != null) {
            ((TextView) view.findViewById(R.id.meetingId)).setText("Meeting Id : " + meeting.getMeetingId());
            Button leave = view.findViewById(R.id.btnLeave);
            leave.setOnClickListener(v -> {
                meeting.leave();
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                startActivity(intent);
            });
        }

        // Initialize player if the playback URL is available
        if (!playbackHlsUrl.isEmpty()) {
            initializePlayer();
        }

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
            meeting = ((JoinLiveClassActivity) mActivity).getMeeting();
        }
    }

    private final MeetingEventListener meetingEventListener = new MeetingEventListener() {
        @Override
        public void onMeetingLeft() {
            if (isAdded()) {
                Intent intent = new Intent(mContext, LiveCoursesClassesListActivity.class);
                intent.putExtra("course_id", ((JoinLiveClassActivity) mActivity).getCourseId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                mActivity.finish();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onHlsStateChanged(JSONObject HlsState) {
            if (HlsState.has("status")) {
                try {
                    String status = HlsState.getString("status");
                    if ("HLS_PLAYABLE".equals(status) && HlsState.has("playbackHlsUrl")) {
                        playbackHlsUrl = HlsState.getString("playbackHlsUrl");
                        waitingLayout.setVisibility(View.GONE);
                        playerView.setVisibility(View.VISIBLE);
                        initializePlayer();
                    } else if ("HLS_STOPPED".equals(status)) {
                        releasePlayer();
                        waitingLayout.setText("Host has stopped the live streaming");
                        waitingLayout.setVisibility(View.VISIBLE);
                        playerView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    protected void initializePlayer() {
        if (player == null && !playbackHlsUrl.isEmpty()) {
            dataSourceFactory = new DefaultHttpDataSource.Factory();
            HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(playbackHlsUrl)));

            player = new ExoPlayer.Builder(mContext).build();
            playerView.setPlayer(player);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setMediaSource(mediaSource);
            player.prepare();
            player.setPlayWhenReady(startAutoPlay);
        }
    }

    protected void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            playerView.setPlayer(null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (meeting != null) {
            meeting.addEventListener(meetingEventListener);
        }
    }

    @Override
    public void onStop() {
        if (meeting != null) {
            meeting.removeEventListener(meetingEventListener);
        }
        releasePlayer(); // Release player when stopped
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mActivity = null;
        playbackHlsUrl = null;
        releasePlayer();
        if (meeting != null) {
            meeting.removeAllListeners();
            meeting = null;
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("playbackHlsUrl", playbackHlsUrl);
    }
}
