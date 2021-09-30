package com.westernsydneyunipx.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.voqual.BuildConfig;
import com.westernsydneyunipx.voqual.R;

import butterknife.BindView;

/**
 * @author PA1810.
 */
public class VideoPlayerActivity extends BaseActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.videoView)
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
    }

    @Override
    protected void setContent() {
        if (isNetworkConnected()) {
            MediaData mediaData = (MediaData) getIntent().getSerializableExtra("media");
            videoView.setVideoURI(Uri.parse(/*BuildConfig.MEDIA_URL*/APIClient.MEDIA_URL + mediaData.getMedia_link()));
            MediaController mc = new MediaController(this);
            videoView.setMediaController(mc);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    mp.start();
                }
            });
        }else {
            showToast(getResources().getString(R.string.check_connection));
        }
    }
}
