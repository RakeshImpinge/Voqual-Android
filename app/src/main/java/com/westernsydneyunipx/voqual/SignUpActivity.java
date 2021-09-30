package com.westernsydneyunipx.voqual;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.westernsydneyunipx.adapter.SpinnerAdapter;
import com.westernsydneyunipx.consents.ConsentCheckActivity;
import com.westernsydneyunipx.consents.LessConsentActivity;
import com.westernsydneyunipx.croping.Croping;
import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.model.SignUpResponse;
import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.participant.ParticipantActivity;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.ListResponse;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.util.Constants;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.video.RecordVideoActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
public class SignUpActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edtFirstName)
    EditText edtFirstName;
    @BindView(R.id.edtLastName)
    EditText edtLastName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtMobile)
    EditText edtMobile;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtAge)
    EditText edtAge;
    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.spinnerResearcher)
    Spinner spinnerResearcher;
    @BindView(R.id.cbIAgree)
    CheckBox cbIAgree;

    @BindView(R.id.check_privacy_parts)
    CheckBox check_privacy_parts;
    @BindView(R.id.check_privacy_audio)
    CheckBox check_privacy_audio;
    @BindView(R.id.check_privacy_video)
    CheckBox check_privacy_video;

    @BindView(R.id.user_image)
    ImageView user_image;
    @BindView(R.id.layer_dummy)
    RelativeLayout relativeLayout;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.gallery)
    TextView gallery;
    @BindView(R.id.camera)
    TextView camera;
    @BindView(R.id.choose_pic)
    TextView choose_pic;
    private ArrayList<User> researcherList = new ArrayList<>();
    private int researcherId;
    private boolean isPanelShown = false;
    View root;
    ViewGroup hiddenPanel;
    Animation up, down;
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 1011;
    File file;
    Bitmap bitmap;
    String imagepath;
    MultipartBody.Part bodyy;
    RequestBody req;
    HashMap<String, RequestBody> mmap;
    private String project_name="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        checkChecked();
    }

    private void checkChecked() {
        cbIAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    check_privacy_parts.setVisibility(View.VISIBLE);
                    check_privacy_audio.setVisibility(View.VISIBLE);
                    check_privacy_video.setVisibility(View.VISIBLE);
                } else {
                    check_privacy_parts.setVisibility(View.GONE);
                    check_privacy_audio.setVisibility(View.GONE);
                    check_privacy_video.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void setContent() {

        // checkPermission();

        if (isNetworkConnected()) {
            researcherList();
        } else {
            showToast(getResources().getString(R.string.check_connection));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        spinnerResearcher.setOnItemSelectedListener(this);

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasInternetPermission = checkSelfPermission(android.Manifest.permission.INTERNET);
            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            int hasReadPermission = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasWritePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            ArrayList<String> permissionList = new ArrayList<String>();

            if (hasInternetPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.INTERNET);
            }
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.CAMERA);
            }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty()) {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), 2);
            }
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
        }
        if (!isValidEmail(edtEmail.getText())) {
            showToast("Please enter the correct Email.");
            return;
        } /*else if (!cbIAgree.isChecked()) {
            showToast("Please agree to My recording will be added to a research database, to help identify common themes across people’s stories.");
            return;
        } else if (!check_privacy_parts.isChecked()) {
            showToast("Please agree that a written version of my recording, or parts of my recording, may be used in reports, or public discussions of the research (e.g. the transcript might be used or adapted to create a script performed by actors, or may be quoted in presentations)");
            return;
        } else if (!check_privacy_audio.isChecked()) {
            showToast("Please agree that my voice,or parts of my voice recording, may be included in presentations or other public discussions of the research (e.g. the voice recording might be played, in whole or in part, during a presentation)");
            return;
        } else if (!check_privacy_video.isChecked()) {
            showToast("Please agree that my video recording, or parts of my video recording, may be used in presentations or other public discussions of the research (e.g. the video might be played, in whole or in part, during a presentation)");
            return;
        }*/ else {
            if (isNetworkConnected()) {
                signUp();
            } else {
                showToast(getResources().getString(R.string.check_connection));
            }
        }
    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void researcherList() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListResponse<User>> callApi = apiInterface.researcherList();

        callApi.enqueue(new Callback<ListResponse<User>>() {

            @Override
            public void onResponse(Call<ListResponse<User>> call, final Response<ListResponse<User>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        researcherList = response.body().data();
                        SpinnerAdapter customAdapter = new SpinnerAdapter(SignUpActivity.this, researcherList);
                        spinnerResearcher.setAdapter(customAdapter);




                    } else {
                        showError(response.body().msg());
                    }
                }
            }

            @Override
            public void onFailure(Call<ListResponse<User>> call, Throwable t) {

                hideLoading();
            }
        });
    }

    private void signUp() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("email", edtEmail.getText().toString());
        hashMap.put("password", edtPassword.getText().toString());
        // hashMap.put("first_name", edtFirstName.getText().toString());
        // hashMap.put("last_name", edtLastName.getText().toString());
        //hashMap.put("mobile", edtMobile.getText().toString());
        // hashMap.put("age", edtAge.getText().toString());
        hashMap.put("username", edtUsername.getText().toString());
        hashMap.put("type", 2);
        hashMap.put("researcher_id", researcherId);
        showLoading(getString(R.string.please_wait));
        Log.e("SIGNUP_DATA", String.valueOf(hashMap));


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse<User>> callApi = apiInterface.signup(hashMap);
        callApi.enqueue(new Callback<RestResponse<User>>() {

            @Override
            public void onResponse(Call<RestResponse<User>> call, final Response<RestResponse<User>> response) {


                if (response.body() != null) {


                    if (response.body().status() == 1) {

                        if (project_name!=null)
                        {
                            //if (project_name.equalsIgnoreCase("The RESPCCT Study"))
                            if (project_name.equalsIgnoreCase("RESPCCT study Canada"))
                            {
                                String user_id= String.valueOf(response.body().data().getId());
                                startActivity(new Intent(SignUpActivity.this, ConsentCheckActivity.class).putExtra("user_id",user_id));

                            }else
                                {
                                    String user_id= String.valueOf(response.body().data().getId());
                                    startActivity(new Intent(SignUpActivity.this, LessConsentActivity.class).putExtra("user_id",user_id));

                                }
                        }

                      showSuccess(response.body().msg());
                        finish();
                    } else {

                        Toast.makeText(SignUpActivity.this, response.body().msg(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        researcherId = researcherList.get(position).getId();
        project_name = researcherList.get(position).getUsername();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
