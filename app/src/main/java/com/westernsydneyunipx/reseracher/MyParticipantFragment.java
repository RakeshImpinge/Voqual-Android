package com.westernsydneyunipx.reseracher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.westernsydneyunipx.adapter.MyParticipantAdapter;
import com.westernsydneyunipx.model.Participant;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.ListResponse;
import com.westernsydneyunipx.util.BaseFragment;
import com.westernsydneyunipx.util.OnParticipantClickListener;
import com.westernsydneyunipx.voqual.R;
import com.westernsydneyunipx.util.SessionManager;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author PA1810.
 */
public class MyParticipantFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private OnParticipantClickListener onParticipantClickListener;
    private ArrayList<Participant> participantArrayList = new ArrayList<>();

    @Override
    protected int setFragmentLayout() {
        return R.layout.fragment_my_participant;
    }

    @Override
    protected void setContent(View rootView) {
        setLinearRecyclerView(recyclerView);
        onParticipantClickListener = (OnParticipantClickListener) getActivity();

        if (isNetworkConnected()) {
            getParticipantList();
        }else {
            showToast(getResources().getString(R.string.check_connection));
        }
    }

    private void getParticipantList() {
        showLoading(getString(R.string.please_wait));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListResponse<Participant>> callApi = apiInterface.participantList(new SessionManager(getActivity()).getUser().getId());

        callApi.enqueue(new Callback<ListResponse<Participant>>() {

            @Override
            public void onResponse(Call<ListResponse<Participant>> call, final Response<ListResponse<Participant>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        participantArrayList = response.body().data();
                        recyclerView.setAdapter(new MyParticipantAdapter(getActivity(), participantArrayList, onParticipantClickListener));
                    } else {
                        Toast.makeText(getActivity(), response.body().msg(), Toast.LENGTH_LONG).show();
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<ListResponse<Participant>> call, Throwable t) {
                hideLoading();
            }
        });
    }
}
