package com.westernsydneyunipx.participant;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.westernsydneyunipx.croping.Croping;
import com.westernsydneyunipx.model.Participant;
import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.retrofit.APIInterface;
import com.westernsydneyunipx.retrofit.response.RestResponse;
import com.westernsydneyunipx.util.BaseFragment;
import com.westernsydneyunipx.util.Constants;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.voqual.R;
import com.westernsydneyunipx.voqual.SignUpActivity;

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

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.edtFirstName)
    EditText edtFirstName;
    @BindView(R.id.edtLastName)
    EditText edtLastName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtMobile)
    EditText edtMobile;
    @BindView(R.id.edtAge)
    EditText edtAge;
    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.profile_pic)
    ImageView profile_pic;
    private SessionManager sessionManager;
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
    private boolean isPanelShown = false;
    View root;
    ViewGroup hiddenPanel;
    Animation up, down;
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 1011;
    File file;
    Bitmap bitmap;
    String imagepath;
    MultipartBody.Part body;
    RequestBody req;
    HashMap<String, RequestBody> mmap;

    @Override
    protected int setFragmentLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void setContent(View rootView) {
        //checkPermission();
        sessionManager = new SessionManager(getActivity());
        User user = sessionManager.getUser();
        edtFirstName.setText(user.getFirst_name());
        edtLastName.setText(user.getLast_name());
        edtEmail.setText(user.getEmail());
        edtMobile.setText(user.getMobile());
        edtAge.setText(String.valueOf(user.getAge()));
        edtUsername.setText(user.getUsername());

        if (user.getProfile_pic() != null) {
            Picasso.with(getActivity()).load(APIClient.MEDIA_URL + "/profile_pic/" + user.getProfile_pic()).into(profile_pic);
        }


       /* hiddenPanel = (LinearLayout) getActivity().findViewById(R.id.show_layout);
        View someView = getActivity().findViewById(R.id.activity_register);
        if (someView != null) {
            root = someView.getRootView();
        }
        up = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
        down = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);*/
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasInternetPermission = getActivity().checkSelfPermission(android.Manifest.permission.INTERNET);
            int hasCameraPermission = getActivity().checkSelfPermission(Manifest.permission.CAMERA);
            int hasReadPermission = getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasWritePermission = getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

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

    @OnClick(R.id.btnUpdate)
    void submit() {
        if (!isValidEmail(edtEmail.getText())) {
            showToast("Please enter the correct Email.");
            return;
        }/* else if (edtFirstName.getText().length() == 0) {
            showToast("Please enter the correct first name.");
            return;
        } else if (edtLastName.getText().length() == 0) {
            showToast("Please enter the correct last name.");
            return;
        } else if (edtMobile.getText().length() == 0) {
            showToast("Please enter the correct mobile number.");
            return;
        } else if (edtAge.getText().length() == 0) {
            showToast("Please enter the correct age.");
            return;
        } */ else if (edtUsername.getText().length() == 0) {
            showToast("Please enter the correct username.");
            return;
        } else {
            if (isNetworkConnected()) {
                updateProfile();
            } else {
                showToast(getResources().getString(R.string.check_connection));
            }

        }
    }

    @OnClick(R.id.profile_pic)
    void imageUpload() {

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void updateProfile() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_id", new SessionManager(getActivity()).getUser().getId());
        hashMap.put("email", edtEmail.getText().toString());
        //  hashMap.put("first_name", edtFirstName.getText().toString());
        // hashMap.put("last_name", edtLastName.getText().toString());
        // hashMap.put("mobile", edtMobile.getText().toString());
        //hashMap.put("age", edtAge.getText().toString());
        showLoading(getString(R.string.please_wait));
        Log.e("EDIT_PROFILE_DATA", String.valueOf(hashMap));


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestResponse<User>> callApi = apiInterface.saveProfile(hashMap);
        callApi.enqueue(new Callback<RestResponse<User>>() {

            @Override
            public void onResponse(Call<RestResponse<User>> call, final Response<RestResponse<User>> response) {
                if (response.body() != null) {
                    if (response.body().status() == 1) {
                        sessionManager.createLogin(response.body().data(), true);
                        showSuccess(response.body().msg());
                        getActivity().startActivity(new Intent(getActivity(), ParticipantActivity.class));

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
