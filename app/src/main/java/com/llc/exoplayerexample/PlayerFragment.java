package com.llc.exoplayerexample;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.leo.simplearcloader.SimpleArcLoader;

import java.net.URL;

public class PlayerFragment extends Fragment {

    private View view;
    PlayerCallback playerCallback;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private Boolean playing = false;
    Boolean isDownload = false;
    public boolean first = true;
    private boolean shouldAutoPlay;
    private DefaultTrackSelector trackSelector;
    private String URL = "https://aio.digitalenter10.com/uploads/mp4/d447caf81671a5bbcf6cc9fa27fb5b30.mp4";
    private String local;
    Context c;
    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private BandwidthMeter bandwidthMeter;
    private int currentApiVersion;
    private SimpleArcLoader simple_arc_loader_exo;
    private ImageView exo_pause,exo_play;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {

            if (isVisibleToUser) {
                initializePlayer();
            } else {
                releasePlayer();
                playing = false;
            }
        }

    }


    public PlayerFragment() {

    }

    public PlayerFragment(PlayerCallback callback) {
        playerCallback = callback;
    }

    public void initView(View view) {
        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(getActivity().getApplicationContext(), Util.getUserAgent(getActivity().getApplicationContext(), "mediaPlayerSample"));
        window = new Timeline.Window();
        simple_arc_loader_exo = (SimpleArcLoader) view.findViewById(R.id.simple_arc_loader_exo);
        exo_pause = (ImageView) view.findViewById(R.id.exo_pause);
        exo_play = (ImageView) view.findViewById(R.id.exo_play);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.c = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideStatuBar();
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_player_view, container, false);

        Bundle bundle = this.getArguments();
        this.local = bundle.getString("local");
        initView(view);
        URL = local;

        if (first) {
            initializePlayer();
        }


        return view;
    }

    void hideStatuBar() {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getActivity().getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }
    }


    private void initializePlayer() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        //Initialize simpleExoPlayerView
        simpleExoPlayerView = view.findViewById(R.id.video_view);
        simpleExoPlayerView.setPlayer(player);
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "CloudinaryExoplayer"));

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        Uri videoUri = isDownload ? Uri.parse(this.local) : Uri.parse(URL);
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);

        player.prepare(loopingSource);
        if (!first)
            player.setPlayWhenReady(true);

        simple_arc_loader_exo.setVisibility(View.VISIBLE);

        simpleExoPlayerView.setControllerShowTimeoutMs(1500);


        player.addListener(new Player.EventListener() {


            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                    if (playbackState == ExoPlayer.STATE_READY) {
//
//                        playing = true;
//
//                    }
//                    if (playbackState == ExoPlayer.STATE_BUFFERING) {
//
//                    }
                if (playbackState == Player.STATE_ENDED) {
                    player.setPlayWhenReady(false);
                    player.stop();
                    player.seekTo(0);
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                // MARK: Move to Next Video
                // Player: Step 2: Detect the player activity
                if (reason == Player.DISCONTINUITY_REASON_PERIOD_TRANSITION) {
                    player.setPlayWhenReady(false);
                    player.stop();
                    player.seekTo(0);
                    // Player: Step 3: Inform Player Activity to move to next pager
                    if (playerCallback != null) {
                        playerCallback.onPlayingCompleted();
                    }
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }


            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

        });


    }

    public void release() {

        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void releasePlayer() {

        if (player != null) {
            Log.v("VideoPlayer", "player not null");
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    public void run() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onResume() {
//        first = false;
        super.onResume();
        if ((player == null)) {
            if (playing)
                initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT > 23) {
            Log.v("VideoPlayer", "onPause");
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
            Log.v("VideoPlayer", "onStop");
        }
    }

}
