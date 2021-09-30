package com.westernsydneyunipx.reseracher;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.westernsydneyunipx.participant.MyLogsFragment;
import com.westernsydneyunipx.participant.ParticipantActivity;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseFragment;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.voqual.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author PA1810.
 */
public class ChangePasswordFragment extends BaseFragment {

    @BindView(R.id.edtOldPassword)
    EditText edtOldPassword;
    @BindView(R.id.edtNewPassword)
    EditText edtNewPassword;
    @BindView(R.id.edtConfirmPassword)
    EditText edtConfirmPassword;

    @Override
    protected int setFragmentLayout() {
        return R.layout.fragment_change_password;
    }

    @Override
    protected void setContent(View rootView) {

    }

    @OnClick(R.id.btnSubmit)
    void submit() {
        if (edtOldPassword.getText().length() < 3) {
            showError("Please enter a valid old password.");
        } else if (edtNewPassword.getText().length() < 6) {
            showError("Password too short(Minimum length is 6 characters)");
        } else if (!edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            showError("Confirm password does not match.");
        } else {
            changePassword();
        }
    }

    private void changePassword() {
        showLoading(getString(R.string.please_wait));

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_id", new SessionManager(getActivity()).getUser().getId());
        hashMap.put("old_password", edtOldPassword.getText().toString().trim());
        hashMap.put("password", edtNewPassword.getText().toString().trim());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse> callApi = apiInterface.changePassword(hashMap);

        callApi.enqueue(new Callback<RestResponse>() {

            @Override
            public void onResponse(Call<RestResponse> call, final Response<RestResponse> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        SessionManager sessionManager = new SessionManager(getActivity());
                        //sessionManager.logout();

                        showSuccess(response.body().msg());

                        if (getActivity() instanceof ParticipantActivity){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frameLayout, new MyLogsFragment())
                                    .commit();
                        }else if (getActivity() instanceof ResearcherActivity){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frameLayout, new AllLogsFragment())
                                    .commit();
                        }

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
