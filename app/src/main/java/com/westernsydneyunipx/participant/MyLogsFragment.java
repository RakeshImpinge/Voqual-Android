package com.westernsydneyunipx.participant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.westernsydneyunipx.adapter.ParticipantMediaAdapter;
import com.westernsydneyunipx.audio.RecordAudioActivity;
import com.westernsydneyunipx.localdata.AudioModel;
import com.westernsydneyunipx.localdata.DatabaseClient;
import com.westernsydneyunipx.localdata.VideoModel;
import com.westernsydneyunipx.model.DeletePostResponse;
import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.ListResponse;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseFragment;
import com.westernsydneyunipx.util.NetworkUtils;
import com.westernsydneyunipx.util.OnPlayClickListener;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.voqual.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.internal.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author PA1810.
 */
public class MyLogsFragment extends BaseFragment {

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.rbAudio)
    RadioButton rbAudio;
    @BindView(R.id.rbVideo)
    RadioButton rbVideo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private int userId;
    private String delete_user_id = "";
    private int delete_position;
    private OnPlayClickListener onPlayClickListener;
    private ArrayList<MediaData> audioArrayList = new ArrayList<>();
    private ArrayList<MediaData> videoArrayList = new ArrayList<>();
    ParticipantMediaAdapter adapter;
    private NetworkReceiver networkReceiver;
    File filee;
    File video_file;
    private SessionManager sessionManager;
    List<String> data_title = new ArrayList<>();
    List<String> video_title = new ArrayList<>();
    ArrayList<MultipartBody.Part> partList = new ArrayList<>();
    ArrayList<MultipartBody.Part> partList_video = new ArrayList<>();
    ArrayList<File> list = new ArrayList<>();
    ArrayList<File> video_list = new ArrayList<>();
    JSONArray jsonArray = new JSONArray();
    JSONArray VideoArray = new JSONArray();


    public static MyLogsFragment getInstance(OnPlayClickListener onPlayClickListener, int userId) {
        MyLogsFragment myLogsFragment = new MyLogsFragment();
        myLogsFragment.onPlayClickListener = onPlayClickListener;
        myLogsFragment.userId = userId;
        return myLogsFragment;

    }


    @Override
    protected int setFragmentLayout() {
        return R.layout.fragment_my_logs;

    }


    @Override
    protected void setContent(View rootView) {

        sessionManager = new SessionManager(getActivity());
        networkReceiver = new NetworkReceiver();
        getActivity().registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        setLinearRecyclerView(recyclerView);
        onPlayClickListener = (OnPlayClickListener) getActivity();
        adapter = new ParticipantMediaAdapter(getActivity(), audioArrayList, onPlayClickListener);
        recyclerView.setAdapter(adapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbAudio.isChecked()) {

                    rbAudio.setTextColor(Color.WHITE);
                    rbVideo.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    if (isNetworkConnected()) {
                        getAudioList();
                    } else {
                        getTasks();
                        showToast(getResources().getString(R.string.check_connection));
                    }
                } else if (rbVideo.isChecked()) {

                    rbAudio.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    rbVideo.setTextColor(Color.WHITE);
                    if (isNetworkConnected()) {
                        getVideoList();
                    } else {
                        getofflineVideo();
                        showToast(getResources().getString(R.string.check_connection));
                    }
                }
            }
        });


    }


    private void getofflineVideo() {

        class GetVideoTask extends AsyncTask<Void, Void, List<VideoModel>> {

            @Override
            protected List<VideoModel> doInBackground(Void... voids) {
                List<VideoModel> taskList = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .taskDao()
                        .getAllVideo();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<VideoModel> tasks) {
                super.onPostExecute(tasks);

                for (int i = 0; i < tasks.size(); i++) {
                    video_file = new File(tasks.get(i).getVideo());

                    video_list.add(video_file);
//                    video_title.add(tasks.get(i).getTitle());

                    VideoArray.put(tasks.get(i).getTitle());
                }

                Log.e("offline_video_size", String.valueOf(tasks.size()));
            }
        }

        GetVideoTask gt = new GetVideoTask();
        gt.execute();


    }


    private void getAudioList() {


        showLoading(getString(R.string.please_wait));

        if (userId == 0) {
            if (getActivity() != null) {
                userId = new SessionManager(getActivity()).getUser().getId();

            }
        }

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListResponse<MediaData>> callApi = apiInterface.audioList(userId);


        callApi.enqueue(new Callback<ListResponse<MediaData>>() {

            @Override
            public void onResponse(Call<ListResponse<MediaData>> call, final Response<ListResponse<MediaData>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        audioArrayList = response.body().data();
                        adapter = new ParticipantMediaAdapter(getActivity(), audioArrayList, onPlayClickListener);
                        //recyclerView.setAdapter(new ParticipantMediaAdapter(getActivity(), audioArrayList, onPlayClickListener));
                        recyclerView.setAdapter(adapter);
                        adapter.onClik(new ParticipantMediaAdapter.clickInterface() {
                            @Override
                            public void onDelete(String id, int pos) {

                                delete_user_id = id;
                                delete_position = pos;
                                if (NetworkUtils.isNetworkConnected(getActivity())) {
                                    callServicee("audio");
                                } else {
                                    showToast(getResources().getString(R.string.check_connection));
                                }
                            }

                        });
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), response.body().msg(), Toast.LENGTH_LONG).show();
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<ListResponse<MediaData>> call, Throwable t) {
                hideLoading();
            }
        });
    }


    private void callServicee(final String tagList) {
        showLoading(getString(R.string.please_wait));
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<DeletePostResponse> call = apiInterface.deletePost(delete_user_id);

        call.enqueue(new Callback<DeletePostResponse>() {
            @Override
            public void onResponse(Call<DeletePostResponse> call, Response<DeletePostResponse> response) {
                if (response.body() != null) {

                    DeletePostResponse deletePostResponse = response.body();
                    if (deletePostResponse.getStatus() == 1) {

                        hideLoading();


                        if (tagList.equalsIgnoreCase("audio")) {

                            if (audioArrayList.size() > 0) {
                                audioArrayList.remove(delete_position);
                                Toast.makeText(getActivity(), "audio deleted successfully", Toast.LENGTH_SHORT).show();

                            }
                        } else if (tagList.equalsIgnoreCase("video")) {

                            if (videoArrayList.size() > 0) {
                                videoArrayList.remove(delete_position);
                                Toast.makeText(getActivity(), "video deleted successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {

                        hideLoading();
                        Toast.makeText(getActivity(), deletePostResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeletePostResponse> call, Throwable t) {
                hideLoading();
            }
        });


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", delete_user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getVideoList() {

        showLoading(getString(R.string.please_wait));
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListResponse<MediaData>> callApi = apiInterface.videoList(userId);

        callApi.enqueue(new Callback<ListResponse<MediaData>>() {
            @Override
            public void onResponse(Call<ListResponse<MediaData>> call, final Response<ListResponse<MediaData>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {

                        getofflineVideo();
                        videoArrayList = response.body().data();
                        adapter = new ParticipantMediaAdapter(getActivity(), videoArrayList, onPlayClickListener);
                        recyclerView.setAdapter(adapter);
                        adapter.onVideodeleteClick(new ParticipantMediaAdapter.VideoDeleteInterface() {
                            @Override
                            public void onVideoDelete(String idd, int poss) {
                                delete_user_id = idd;
                                delete_position = poss;
                                if (NetworkUtils.isNetworkConnected(getActivity())) {
                                    callServicee("video");
                                } else {
                                    showToast(getResources().getString(R.string.check_connection));
                                }
                            }
                        });

                        adapter.notifyDataSetChanged();


                    } else {
                        Toast.makeText(getActivity(), response.body().msg(), Toast.LENGTH_LONG).show();
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<ListResponse<MediaData>> call, Throwable t) {
                hideLoading();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isNetworkConnected()) {
            if (rbAudio.isChecked()) {
                getAudioList();

            } else {
                getVideoList();
            }
        } else {
            showToast(getResources().getString(R.string.check_connection));
            if (rbAudio.isChecked()) {
                getTasks();

            } else {
                getofflineVideo();
            }


        }
    }


    private void getTasks() {

        class GetTasks extends AsyncTask<Void, Void, List<AudioModel>> {

            @Override
            protected List<AudioModel> doInBackground(Void... voids) {
                List<AudioModel> taskList = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<AudioModel> tasks) {
                super.onPostExecute(tasks);

                for (int i = 0; i < tasks.size(); i++) {
                    filee = new File(tasks.get(i).getTask());

                    list.add(filee);

                    jsonArray.put(tasks.get(i).getName());
                    data_title.add(tasks.get(i).getName());
                }

                Log.e("offline_audio_size", String.valueOf(tasks.size()));
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }


    public class NetworkReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {


            if (isNetworkConnected()) {


                if (rbAudio.isChecked()) {
                    if (list.size() > 0) {
                        if (getActivity() != null) {
                            callService();
                        }
                    } else {
                        Toast.makeText(context, "no  offline audio is saved", Toast.LENGTH_SHORT).show();
                    }

                } else if (rbVideo.isChecked()) {


                    if (video_list.size() > 0) {


                        if (getActivity() != null) {
                            callServiceVideo();
                        }

                    } else {
                        Toast.makeText(context, "no offline video is saved", Toast.LENGTH_SHORT).show();
                    }


                }


            } else {

            }
        }
    }


    private void callServiceVideo() {

        showLoading(getString(R.string.please_wait));

        for (int i = 0; i < video_list.size(); i++) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), video_list.get(i));
            partList_video.add(MultipartBody.Part.createFormData("file[" + i + "]", video_list.get(i).getName(), reqFile));
        }

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse<MediaData>> callApi = apiInterface.uploadMediaoffline(sessionManager.getUser().getId(), 2, VideoArray, partList_video);

        Log.e("video_id", String.valueOf(sessionManager.getUser().getId()));
        Log.e("video_title", String.valueOf(VideoArray));
        Log.e("video_partList", String.valueOf(partList_video));


        callApi.enqueue(new Callback<RestResponse<MediaData>>() {

            @Override
            public void onResponse(Call<RestResponse<MediaData>> call, final Response<RestResponse<MediaData>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {

                        showSuccess(response.body().msg());
                        deleteVideos();
                        for (int i = 0; i < VideoArray.length(); i++) {
                            VideoArray.remove(i);
                        }

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


    private void deleteVideos() {

        class deleteVideos extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {


                //deleting to database
                DatabaseClient.getInstance(getActivity()).getAppDatabase()
                        .taskDao()
                        .deleteVideo();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getActivity(), "All saved videos are uploaded", Toast.LENGTH_LONG).show();
                getVideoList();

            }
        }

        deleteVideos dv = new deleteVideos();
        dv.execute();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void callService() {


        showLoading(getString(R.string.please_wait));


        for (int i = 0; i < list.size(); i++) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), list.get(i));
            partList.add(MultipartBody.Part.createFormData("file[" + i + "]", list.get(i).getName(), reqFile));
        }
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse<MediaData>> callApi = apiInterface.uploadMediaoffline(sessionManager.getUser().getId(), 1, jsonArray, partList);

        Log.e("audio_id", String.valueOf(sessionManager.getUser().getId()));
        Log.e("audio_title", String.valueOf(jsonArray));
        Log.e("audio_partList", String.valueOf(partList));


        callApi.enqueue(new Callback<RestResponse<MediaData>>() {

            @Override
            public void onResponse(Call<RestResponse<MediaData>> call, final Response<RestResponse<MediaData>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {

                        showSuccess(response.body().msg());
                        deleteData();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonArray.remove(i);
                        }


                    } else {
                        showError(response.body().msg());
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<MediaData>> call, Throwable t) {

                Log.e("Service_failuree", t.getMessage());

                hideLoading();


            }
        });

    }


    private void deleteData() {

        class deleteData extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {


                //deleting to database
                DatabaseClient.getInstance(getActivity()).getAppDatabase()
                        .taskDao()
                        .deleteAudio();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getActivity(), "All saved audios are uploaded", Toast.LENGTH_LONG).show();
                getAudioList();
            }
        }

        deleteData dt = new deleteData();
        dt.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(networkReceiver);
    }
}
