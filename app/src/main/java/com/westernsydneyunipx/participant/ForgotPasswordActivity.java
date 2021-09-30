package com.westernsydneyunipx.participant;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.westernsydneyunipx.model.DeletePostResponse;
import com.westernsydneyunipx.model.ForgotPassword;
import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.util.NetworkUtils;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.voqual.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back_button)
    ImageView back_button;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    Context context = this;
    private String Email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

    }

    @Override
    protected void setContent() {
        title.setText("Forgot Password");
    }

    @OnClick(R.id.btnSubmit)
    void btnSubmit() {
        if (NetworkUtils.isNetworkConnected(ForgotPasswordActivity.this)) {
            if (email.getText().toString().trim().isEmpty()) {
                showToast(getResources().getString(R.string.please_enter_email));
            } else if (!isValidEmail(email.getText().toString().trim())) {
                showToast("Please enter the correct Email.");
            } else {
                Email = email.getText().toString().trim();
                callService();
            }
        } else {
            showToast(getResources().getString(R.string.check_connection));
        }
    }

    @OnClick(R.id.back_button)
    void onBackButton() {
        finish();
    }

    private void callService() {
        showLoading(getString(R.string.please_wait));
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ForgotPassword> callApi = apiInterface.forgotPass(Email);
        callApi.enqueue(new Callback<ForgotPassword>() {
            @Override
            public void onResponse(Call<ForgotPassword> call, Response<ForgotPassword> response) {
                if (response.body() != null) {
                    ForgotPassword forgotPassword = response.body();
                    if (forgotPassword.getStatus() == 1) {
                        hideLoading();
                        Toast.makeText(context, forgotPassword.getMsg(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showError(forgotPassword.getMsg());

                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<ForgotPassword> call, Throwable t) {
            }
        });


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", Email);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
