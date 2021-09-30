package com.westernsydneyunipx.audio;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.westernsydneyunipx.localdata.AudioModel;
import com.westernsydneyunipx.localdata.DatabaseClient;
import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.voqual.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author PA1810.
 */
public class RecordAudioActivity extends BaseActivity implements Chronometer.OnChronometerTickListener {

    @BindView(R.id.tvRecordTime)
    TextView tvRecordTime;
    @BindView(R.id.ivWave)
    ImageView ivWave;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.edtTitle)
    EditText edtTitle;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.btnStartRecordAudio)
    Button btnStartRecordAudio;
    @BindView(R.id.btnStopRecordAudio)
    Button btnStopRecordAudio;
    private static final int THRESHOLD_EXERCISE = 300000;
    private String audioSavePathInDevice = null;
    private MediaRecorder mediaRecorder;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        sessionManager = new SessionManager(RecordAudioActivity.this);
    }

    @Override
    protected void setContent() {
        chronometer.setOnChronometerTickListener(this);
    }

    @OnClick(R.id.btnStartRecordAudio)
    void startRecording() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            String path = Environment.getExternalStorageDirectory() + File.separator
                                    + getString(R.string.app_name);
                            File folder = new File(path);
                            if (!folder.exists()) {
                                folder.mkdirs();
                            }

                            audioSavePathInDevice = path + "/" + "test" + ".mp3";
                            MediaRecorderReady();
                            try {
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                                btnStartRecordAudio.setVisibility(View.GONE);
                                //added after
                                chronometer.setVisibility(View.VISIBLE);
                                btnStopRecordAudio.setVisibility(View.VISIBLE);
                                tvRecordTime.setVisibility(View.GONE);
                                ivWave.setVisibility(View.VISIBLE);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                chronometer.start();
                                btnSave.setEnabled(false);
                                Glide.with(RecordAudioActivity.this)
                                        .load(R.raw.ic_wave)
                                        .into(ivWave);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @OnClick(R.id.btnStopRecordAudio)
    void stopRecording() {
        if (mediaRecorder != null) {
            btnStartRecordAudio.setVisibility(View.VISIBLE);
            btnStopRecordAudio.setVisibility(View.GONE);
            ivWave.setVisibility(View.GONE);
            mediaRecorder.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.stop();
            btnSave.setEnabled(true);

            Uri uri = Uri.parse(audioSavePathInDevice);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(RecordAudioActivity.this, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int millSecond = Integer.parseInt(durationStr);

            long seconds = millSecond / 1000;
            long minutes = seconds / 60;

            if (seconds > 60) {
                seconds = seconds - (60);
            }

            String time = minutes + ":" + seconds;

            if (seconds < 10) {
                time = minutes + ":0" + seconds + " \n Please save before leaving the app";
            }

            tvRecordTime.setVisibility(View.VISIBLE);
            tvRecordTime.setText(String.format(getString(R.string.total_audio), time));
            // added after
            chronometer.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btnSave)
    void saveAudio() {

        if (edtTitle.getText().length() > 3 && !isNetworkConnected()) {
            saveTask();
        }


        if (edtTitle.getText().length() > 3) {
            if (isNetworkConnected()) {

                if (audioSavePathInDevice != null) {
                    uploadMedia();
                } else {
                    showToast(getResources().getString(R.string.error_audio));
                }

            } else {
                showToast(getResources().getString(R.string.check_connection));
            }
        } else {
            showError("Title should be at least 3 characters");
        }
    }

    private void saveTask() {

        // final File file = new File(audioSavePathInDevice);


        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                AudioModel model = new AudioModel();
                model.setTask(audioSavePathInDevice);
                model.setName(edtTitle.getText().toString().trim());

                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .insert(model);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Your recording has been saved successfully when you will Reconnect to internet it will save autometically.", Toast.LENGTH_LONG).show();
                edtTitle.setText("");
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    @OnClick(R.id.btnCancel)
    void cancel() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            chronometer.stop();
        }
        finish();
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audioSavePathInDevice);
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        if (elapsedMillis > THRESHOLD_EXERCISE) {
            btnStartRecordAudio.setVisibility(View.VISIBLE);
            btnStopRecordAudio.setVisibility(View.GONE);
            ivWave.setVisibility(View.INVISIBLE);
            mediaRecorder.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.stop();
            btnSave.setEnabled(true);
            //added after
            ivWave.setVisibility(View.GONE);
            tvRecordTime.setVisibility(View.VISIBLE);
            tvRecordTime.setText("Max limit reached 5:00\n Save audio!!!");
            chronometer.setVisibility(View.GONE);
            saveAudio();

        }
    }

    private void uploadMedia() {
        showLoading(getString(R.string.please_wait));

        File file = new File(audioSavePathInDevice);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse<MediaData>> callApi = apiInterface.uploadMedia(sessionManager.getUser().getId(), 1,
                edtTitle.getText().toString(), body);


        callApi.enqueue(new Callback<RestResponse<MediaData>>() {

            @Override
            public void onResponse(Call<RestResponse<MediaData>> call, final Response<RestResponse<MediaData>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        showSuccess(response.body().msg());
                        finish();
                    } else {
                        showError(response.body().msg());
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<MediaData>> call, Throwable t) {
                hideLoading();
            }
        });
    }
}
