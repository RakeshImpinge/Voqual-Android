package com.westernsydneyunipx.reseracher;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.westernsydneyunipx.adapter.ResearcherMediaAdapter;
import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.ListResponse;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseFragment;
import com.westernsydneyunipx.util.OnPlayClickListener;
import com.westernsydneyunipx.util.RecyclerViewClickListener;
import com.westernsydneyunipx.util.RecyclerViewTouchListener;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.voqual.R;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author PA1810.
 */
public class AllLogsFragment extends BaseFragment {

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.rbAudio)
    RadioButton rbAudio;
    @BindView(R.id.rbVideo)
    RadioButton rbVideo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private OnPlayClickListener onPlayClickListener;
    private ArrayList<MediaData> mediaArrayList = new ArrayList<>();

    @Override
    protected int setFragmentLayout() {
        return R.layout.fragment_my_logs;
    }

    @Override
    protected void setContent(View rootView) {
        setLinearRecyclerView(recyclerView);
        onPlayClickListener = (OnPlayClickListener) getActivity();

        if (isNetworkConnected()) {
            getAudioList();
        }else {
            showToast(getResources().getString(R.string.check_connection));
        }

        recyclerView.setAdapter(new ResearcherMediaAdapter(getActivity(), mediaArrayList, onPlayClickListener));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbAudio.isChecked()) {
                    rbAudio.setTextColor(Color.WHITE);
                    rbVideo.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    if (isNetworkConnected()) {
                        getAudioList();
                    }else {
                        showToast(getResources().getString(R.string.check_connection));
                    }
                } else {
                    rbAudio.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    rbVideo.setTextColor(Color.WHITE);
                    if (isNetworkConnected()) {
                        getVideoList();
                    }else {
                        showToast(getResources().getString(R.string.check_connection));
                    }
                }
            }
        });

//        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onLongClick(View view, final int position) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setMessage("Are you sure you want to delete media?")
//                        .setCancelable(false)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                if (isNetworkConnected()) {
//                                    deleteMedia(mediaArrayList.get(position).getId());
//                                }
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//        }));
    }

    private void getAudioList() {
        showLoading(getString(R.string.please_wait));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        SessionManager sessionManager = new SessionManager(getActivity());
        Call<ListResponse<MediaData>> callApi = apiInterface.audioList(sessionManager.getAccessToken(),sessionManager.getUser().getId());

        callApi.enqueue(new Callback<ListResponse<MediaData>>() {

            @Override
            public void onResponse(Call<ListResponse<MediaData>> call, final Response<ListResponse<MediaData>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        mediaArrayList = response.body().data();
                        recyclerView.setAdapter(new ResearcherMediaAdapter(getActivity(), mediaArrayList, onPlayClickListener));
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

    private void getVideoList() {
        showLoading(getString(R.string.please_wait));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        SessionManager sessionManager = new SessionManager(requireActivity());
        Call<ListResponse<MediaData>> callApi = apiInterface.videoList(sessionManager.getAccessToken(), sessionManager.getUser().getId());

        callApi.enqueue(new Callback<ListResponse<MediaData>>() {

            @Override
            public void onResponse(Call<ListResponse<MediaData>> call, final Response<ListResponse<MediaData>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        mediaArrayList = response.body().data();
                        recyclerView.setAdapter(new ResearcherMediaAdapter(getActivity(), mediaArrayList, onPlayClickListener));
                    } else {
                        showError(response.body().msg());
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

    private void deleteMedia(int id) {
        showLoading(getString(R.string.please_wait));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse> callApi = apiInterface.deleteMedia(new SessionManager(requireActivity()).getAccessToken(), id);

        callApi.enqueue(new Callback<RestResponse>() {

            @Override
            public void onResponse(Call<RestResponse> call, final Response<RestResponse> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        showSuccess(response.body().msg());
                    } else {
                        showError(response.body().msg());
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                hideLoading();
            }
        });
    }
}
