package com.westernsydneyunipx.voqual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.westernsydneyunipx.util.SessionManager;

/**
 * @author PA1810.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);

        SessionManager sessionManager = new SessionManager(SplashActivity.this);
        sessionManager.checkLogin();
        finish();
    }
}
