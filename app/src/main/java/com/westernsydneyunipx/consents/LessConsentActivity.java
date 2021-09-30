package com.westernsydneyunipx.consents;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.voqual.LoginActivity;
import com.westernsydneyunipx.voqual.R;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessConsentActivity extends BaseActivity implements View.OnClickListener {
    private Button save;
    private String user_id="";
    private CheckBox check_one;
    private String selected_value="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_less_consent);
        if (getIntent().hasExtra("user_id"))
        {
            user_id=getIntent().getStringExtra("user_id");
        }
        findid();
    }

    @Override
    protected void setContent() {

    }

    private void findid() {
        save=findViewById(R.id.save);
        check_one=findViewById(R.id.check_one);
        save.setOnClickListener(this);
        check_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                {
                    selected_value="1";
                }else
                    {
                        selected_value="0";
                    }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.save:

                if (isNetworkConnected())
                {
                    saveData();
                }else
                {
                    showToast(getResources().getString(R.string.check_connection));

                }



                break;
        }

    }

    private void saveData() {


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_id",user_id);
        hashMap.put("consent_number", selected_value);
        Log.e("sending_parametrs", String.valueOf(hashMap));

        showLoading(getString(R.string.please_wait));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse> callApi = apiInterface.saveConsent(hashMap);
        callApi.enqueue(new Callback<RestResponse>() {

            @Override
            public void onResponse(Call<RestResponse> call, final Response<RestResponse> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        String res=response.body().toString();
                        String mes=response.body().msg();
                        Toast.makeText(LessConsentActivity.this, mes, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LessConsentActivity.this, LoginActivity.class));
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
