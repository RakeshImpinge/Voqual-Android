package com.westernsydneyunipx.video;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

//import com.iceteck.silicompressorr.SiliCompressor;
//import com.iceteck.silicompressorr.SiliCompressor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//import com.vincent.videocompressor.VideoCompress;
import com.vincent.videocompressor.VideoCompress;
import com.westernsydneyunipx.localdata.AudioModel;
import com.westernsydneyunipx.localdata.DatabaseClient;
import com.westernsydneyunipx.localdata.VideoModel;
import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.util.VideoCompressionUtil;
import com.westernsydneyunipx.voqual.BuildConfig;
import com.westernsydneyunipx.voqual.R;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
public class RecordVideoActivity extends BaseActivity {

    @BindView(R.id.tvMessage)
    TextView tvMessage;
    @BindView(R.id.edtTitle)
    EditText edtTitle;
    @BindView(R.id.videoView)
    VideoView videoView;
    public static int VIDEO_CAPTURED = 999;
    private Uri videoPath;
    private String path;
    private String mVideoPath;


    public static final String FILE_PROVIDER_AUTHORITY = ".provider";
    private static final int TYPE_VIDEO = 2;

    Uri capturedUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String d = formatter.format(date);
        Log.d("TAG", "onCreate: date "+d);
    }

    @Override
    protected void setContent() {

    }

    @OnClick(R.id.btnVideo)
    void startRecording() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent captureVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            startActivityForResult(captureVideoIntent, VIDEO_CAPTURED);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    @OnClick(R.id.btnSave)
    void uploadVideo() {
        if (edtTitle.getText().length() > 3) {
            if (isNetworkConnected()) {
                if (path != null) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        videoView.stopPlayback();
                    } else {
                        videoView.stopPlayback();
                    }
                    compressVideo();
                }
            } else {

                if (!isNetworkConnected()) {
                    if (path != null) {


                        if (videoView.isPlaying()) {
                            videoView.pause();
                            videoView.stopPlayback();
                        } else {
                            videoView.stopPlayback();
                        }
                        compressVideo();
                        //saveTask();
                    }
                }


                showToast(getResources().getString(R.string.check_connection));
            }
        } else {
            showError("Title should be at least 3 characters");
        }
    }

    private void saveTask(String destPath) {

        final File file = new File(destPath);

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                VideoModel model = new VideoModel();
                model.setVideo(String.valueOf(file));
                model.setTitle(edtTitle.getText().toString().trim());

                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .insertvideo(model);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                hideLoading();
                edtTitle.setText("");
                Toast.makeText(getApplicationContext(), "Your recording has been saved successfully when you will Reconnect to internet it will save autometically.", Toast.LENGTH_LONG).show();

            }
        }

        SaveTask st = new SaveTask();
        st.execute();

    }

    private void compressVideo() {

        String fileName = "VID_";
        File storageDir = new File(outputDir + "/" + getString(R.string.app_name));
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }


        //File file = new File(storageDir, "VID_" + ".mp4");


        final String destPath = storageDir + File.separator + fileName + new SimpleDateFormat("yyyyMMdd_HHmmss", getLocale()).format(new Date()) + ".mp4";
        VideoCompress.compressVideoLow(path, destPath, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                showLoading(getString(R.string.please_wait));
            }

            @Override
            public void onSuccess() {
                System.out.println("=--------onSuccess--------");

                if (isNetworkConnected()) {
                    uploadMedia(destPath);
                } else if (!isNetworkConnected()) {

                    saveTask(destPath);

                }


            }

            @Override
            public void onFail() {
                hideLoading();
            }

            @Override
            public void onProgress(float percent) {
                System.out.println("=--------percent--------" + percent);
            }
        });

        Log.e("compressss", "calll_huaaaaa");
    }

    private Locale getLocale() {
        Configuration config = getResources().getConfiguration();
        Locale sysLocale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = getSystemLocale(config);
        } else {
            sysLocale = getSystemLocaleLegacy(config);
        }

        return sysLocale;
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }


    @OnClick(R.id.btnCancel)
    void goBack() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED) {
            videoPath = data.getData();
            try {
                path = VideoCompressionUtil.getFilePath(this, data.getData());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadMedia(String compressedFilePath) {
        File file = new File(compressedFilePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String d = formatter.format(date);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse<MediaData>> callApi = apiInterface.uploadMedia(new SessionManager(this).getAccessToken(), new SessionManager(
                RecordVideoActivity.this).getUser().getId(), 2, edtTitle.getText().toString(), body);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }

    private File createMediaFile() throws IOException {

        // Create an image file name
        String fileName = "VID_";
        File storageDir = new File(Environment.getExternalStorageDirectory()
                + "/" + getString(R.string.app_name));
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }

        /* Environment.getExternalStorageDirectory()}/logcat.txt*/

       /* File file = File.createTempFile(
                fileName,  *//* prefix *//*
               ".mp4",         *//* suffix *//*
                storageDir      *//* directory *//*
        );*/

        File file = new File(storageDir, "VID_" + ".mp4");

        // Get the path of the file created
        Log.d("RecordVideoActivity", "mCurrentPhotoPath: ");
        return file;
    }
}
