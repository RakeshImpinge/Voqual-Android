package com.westernsydneyunipx.audio;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.util.NetworkUtils;
import com.westernsydneyunipx.voqual.BuildConfig;
import com.westernsydneyunipx.voqual.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author PA1810.
 */
public class AudioPopupDialog extends Dialog implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.btnPlayPause)
    Button btnPlayPause;
    @BindView(R.id.btnStop)
    Button btnStop;
    private MediaData mediaData;
    private Context context;
    private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    private boolean isRestart;

    public AudioPopupDialog(@NonNull Context context, MediaData mediaData) {
        super(context);
        this.context = context;
        this.mediaData = mediaData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_audio_popup);

        ButterKnife.bind(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        seekBar.setOnSeekBarChangeListener(this);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (isNetworkConnected()) {
            new Player().execute();
        }else {
            Toast.makeText(context,
                    context.getResources().getString(R.string.check_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnPlayPause)
    void playAndPause() {
        if (mediaPlayer.isPlaying()) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                isRestart = false;
                btnPlayPause.setText(context.getText(R.string.play));
            }
        } else {
            if (mediaPlayer != null) {
                if (isRestart) {
                    new Player().execute();
                } else {
                    mediaPlayer.start();
                }
                btnPlayPause.setText(context.getText(R.string.pause));
            }
        }
    }

    @OnClick(R.id.btnStop)
    void stop() {
        if (mediaPlayer != null) {
            isRestart = true;
            mediaPlayer.stop();
            mediaPlayer.reset();
            seekBar.setProgress(0);
            btnPlayPause.setText(context.getText(R.string.play));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @SuppressLint("StaticFieldLeak")
    class Player extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            seekBar.setMax(mediaPlayer.getDuration());
            progressBar.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean prepared = null;
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(/*BuildConfig.MEDIA_URL*/APIClient.MEDIA_URL + mediaData.getMedia_link());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        seekBar.setProgress(0);
                        btnPlayPause.setText(context.getText(R.string.play));
                    }
                });

                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            seekBar.setProgress(0);
            seekBar.setMax(100);
            updateProgressBar();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        mediaPlayer.start();
                        btnPlayPause.setText(context.getText(R.string.pause));
                        progressBar.setVisibility(View.GONE);
                        seekBar.setVisibility(View.VISIBLE);
                    }

                }
            }, 2000);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mediaPlayer != null) {
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();

                // Updating progress bar
                int progress = getProgressPercentage(currentDuration, totalDuration);
                //Log.d("Progress", ""+progress);
                seekBar.setProgress(progress);

                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    private int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    private boolean isNetworkConnected() {
        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.error_internet), Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
