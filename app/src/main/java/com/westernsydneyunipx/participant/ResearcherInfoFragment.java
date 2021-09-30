package com.westernsydneyunipx.participant;

import android.view.View;
import android.widget.TextView;

import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseFragment;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.voqual.R;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author PA1810.
 */

public class ResearcherInfoFragment extends BaseFragment {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvPhone)
    TextView tvPhone;

    @Override
    protected int setFragmentLayout() {
        return R.layout.fragment_researcher_info;
    }

    @Override
    protected void setContent(View rootView) {

        if (isNetworkConnected()) {
            getResearcherInfo();
        } else {
            showToast(getResources().getString(R.string.check_connection));
        }
    }

    private void getResearcherInfo() {
        showLoading(getString(R.string.please_wait));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse<User>> callApi = apiInterface.getResearcherInfo(new SessionManager(getActivity()).getUser().getResearcher_id());

        callApi.enqueue(new Callback<RestResponse<User>>() {

            @Override
            public void onResponse(Call<RestResponse<User>> call, final Response<RestResponse<User>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        User user = response.body().data();
                        //tvName.setText(user.toString());
                        tvName.setText(user.getUsername());
                        tvEmail.setText(user.getEmail());
                        //tvPhone.setText(user.getMobile());
                    } else {
                        showError(response.body().msg());
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<User>> call, Throwable t) {
                hideLoading();
            }
        });
    }
}
