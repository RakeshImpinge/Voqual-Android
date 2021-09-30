package com.westernsydneyunipx.voqual;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.westernsydneyunipx.consents.ConsentCheckActivity;
import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.participant.ForgotPasswordActivity;
import com.westernsydneyunipx.participant.ParticipantActivity;
import com.westernsydneyunipx.reseracher.ResearcherActivity;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.util.SessionManager;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author PA1810.
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.rbResearcher)
    RadioButton rbResearcher;
    @BindView(R.id.rbParticipate)
    RadioButton rbParticipate;
    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.tvSignUp)
    TextView tvSignUp;
    @BindView(R.id.tvContactAdmin)
    TextView tvContactAdmin;
    @BindView(R.id.cbKeepLogged)
    CheckBox cbKeepLogged;
    @BindView(R.id.tv_forgot_pass)
    TextView tv_forgot_pass;
    // private int type = 1;
    private int type = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void setContent() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbResearcher.isChecked()) {
                    type = 1;
                    tvContactAdmin.setVisibility(View.VISIBLE);
                    tvSignUp.setVisibility(View.GONE);
                    rbResearcher.setTextColor(Color.WHITE);
                    rbParticipate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    tv_forgot_pass.setVisibility(View.GONE);

                } else {
                    type = 2;
                    tvContactAdmin.setVisibility(View.GONE);
                    tvSignUp.setVisibility(View.VISIBLE);
                    rbResearcher.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    rbParticipate.setTextColor(Color.WHITE);
                    tv_forgot_pass.setVisibility(View.VISIBLE);

                }
            }
        });


        if (type == 2) {
            tvContactAdmin.setVisibility(View.GONE);
            tvSignUp.setVisibility(View.VISIBLE);
            tv_forgot_pass.setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R.id.btnSubmit)
    void submit() {
        if (edtUsername.getText().length() == 0) {
            showToast("Please enter the correct username.");
            return;
        } else if (edtPassword.getText().length() == 0) {
            showToast("Please enter the correct password.");
            return;
        } else {
            if (isNetworkConnected()) {
                login();
            } else {
                showToast(getResources().getString(R.string.check_connection));
            }
        }
    }

    @OnClick(R.id.tv_forgot_pass)
    void forgotPass() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    @OnClick(R.id.tvSignUp)
    void signUp() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    private void login() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", edtUsername.getText().toString());
        hashMap.put("password", edtPassword.getText().toString());
        hashMap.put("type", type);

        showLoading(getString(R.string.please_wait));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse<User>> callApi = apiInterface.login(hashMap);

        callApi.enqueue(new Callback<RestResponse<User>>() {

            @Override
            public void onResponse(Call<RestResponse<User>> call, final Response<RestResponse<User>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {


                        User user = response.body().data();
                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                        sessionManager.createLogin(user, cbKeepLogged.isChecked());

                        if (type == 2) {

                            startActivity(new Intent(LoginActivity.this, ParticipantActivity.class));
                        } else {

                            startActivity(new Intent(LoginActivity.this, ResearcherActivity.class));
                        }
                        showSuccess(response.body().msg());
                        finish();
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
