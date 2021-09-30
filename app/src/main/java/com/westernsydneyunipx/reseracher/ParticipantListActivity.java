package com.westernsydneyunipx.reseracher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.westernsydneyunipx.adapter.ParticipantMediaAdapter;
import com.westernsydneyunipx.audio.AudioPopupDialog;
import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.participant.MyLogsFragment;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.util.OnPlayClickListener;
import com.westernsydneyunipx.video.VideoPlayerActivity;
import com.westernsydneyunipx.voqual.R;

/**
 * @author PA1810.
 */
public class ParticipantListActivity extends BaseActivity implements OnPlayClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
    }

    @Override
    protected void setContent() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        int userId = getIntent().getIntExtra("userId", 0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, MyLogsFragment.getInstance(this, userId));
        fragmentTransaction.commit();
    }

    @Override
    public void onAudioPlay(MediaData mediaData) {
        AudioPopupDialog audioPopupDialog = new AudioPopupDialog(ParticipantListActivity.this, mediaData);
        audioPopupDialog.show();
    }

    @Override
    public void onVideoPlay(MediaData mediaData) {
        Intent intent = new Intent(ParticipantListActivity.this, VideoPlayerActivity.class);
        intent.putExtra("media", mediaData);
        startActivity(intent);
    }
}
