package com.westernsydneyunipx.consents;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.participant.MyLogsFragment;
import com.westernsydneyunipx.participant.ParticipantActivity;
import com.westernsydneyunipx.reseracher.AllLogsFragment;
import com.westernsydneyunipx.reseracher.ResearcherActivity;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.voqual.LoginActivity;
import com.westernsydneyunipx.voqual.R;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsentCheckActivity extends BaseActivity implements View.OnClickListener {
    private Button save;
    private CheckBox check_one,check_two,check_three,check_four;
    private int total_count,check_one_count,check_two_count,check_three_count,check_four_count;
    private String user_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_check);
        if (getIntent().hasExtra("user_id"))
        {
            user_id=getIntent().getStringExtra("user_id");
        }
        initView();
        openDialog();
    }

    private void openDialog() {
        final Dialog dialog = new Dialog(ConsentCheckActivity.this);
// Include dialog.xml file
        dialog.setContentView(R.layout.dailog);
        dialog.setCancelable(false);
        dialog.show();

        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
// if decline button is clicked, close the custom dialog
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });
    }

    @Override
    protected void setContent() {
    }
    private void initView() {
        save=findViewById(R.id.save);
        check_one=findViewById(R.id.check_one);
        check_two=findViewById(R.id.check_two);
        check_three=findViewById(R.id.check_three);
        check_four=findViewById(R.id.check_four);
        save.setOnClickListener(this);

        checkedButton();

    }

    private void checkedButton() {
        check_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                {
                   // check_one_count=1;
                    check_two.setVisibility(View.VISIBLE);
                    check_three.setVisibility(View.VISIBLE);
                    check_four.setVisibility(View.VISIBLE);
                }else
                    {
                      //  check_one_count=0;
                       // check_two_count=0;
                       // check_three_count=0;
                      //  check_four_count=0;
                        check_two.setVisibility(View.GONE);
                        check_three.setVisibility(View.GONE);
                        check_four.setVisibility(View.GONE);
                        check_two.setChecked(false);
                        check_three.setChecked(false);
                        check_four.setChecked(false);
                    }
                totalCalulated();
            }
        });
        check_two.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                {
                   // check_two_count=1;
                }else
                {
                    //check_two_count=0;
                }
                totalCalulated();

            }
        });
        check_three.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                {
                   // check_three_count=1;
                }else
                {
                    //check_three_count=0;
                }
                totalCalulated();

            }
        });
        check_four.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                {
                   // check_four_count=1;
                }else
                {
                   // check_four_count=0;
                }
                totalCalulated();

            }
        });
    }

    private void totalCalulated() {

       // total_count=check_one_count+check_two_count+check_three_count+check_four_count;
        if (check_one.isChecked())
        {
            total_count=1;
        }else
            {
                total_count=0;
            }
        if (check_one.isChecked()&&check_two.isChecked())
        {
            total_count=2;
        }
        if (check_one.isChecked()&&check_three.isChecked())
        {
            total_count=3;
        }
        if (check_one.isChecked()&&check_four.isChecked())
        {
            total_count=4;
        }
        if (check_one.isChecked()&&check_two.isChecked()&&check_three.isChecked())
        {
            total_count=5;
        }
        if (check_one.isChecked()&&check_two.isChecked()&&check_four.isChecked())
        {
            total_count=6;
        }
        if (check_one.isChecked()&&check_three.isChecked()&&check_four.isChecked())
        {
            total_count=7;
        }
        if (check_one.isChecked()&&check_two.isChecked()&&check_three.isChecked()&&check_four.isChecked())
        {
            total_count=8;
        }

        Log.e("total_check_count", String.valueOf(total_count));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.save:
                if (isNetworkConnected())
                {
                    callService();

                }else
                    {
                        showToast(getResources().getString(R.string.check_connection));

                    }
                break;
        }
    }

    private void callService() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_id",user_id);
        hashMap.put("consent_number", total_count);
        Log.e("sending_parametrs", String.valueOf(hashMap));

        showLoading(getString(R.string.please_wait));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse> callApi = apiInterface.saveConsent(new SessionManager(this).getAccessToken(),hashMap);
        callApi.enqueue(new Callback<RestResponse>() {

            @Override
            public void onResponse(Call<RestResponse> call, final Response<RestResponse> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        String res=response.body().toString();
                        String mes=response.body().msg();
                        Toast.makeText(ConsentCheckActivity.this, mes, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ConsentCheckActivity.this, LoginActivity.class));
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
