package com.westernsydneyunipx.participant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.westernsydneyunipx.audio.AudioPopupDialog;
import com.westernsydneyunipx.audio.RecordAudioActivity;
import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.reseracher.ChangePasswordFragment;
import com.westernsydneyunipx.reseracher.ResearcherActivity;
import com.westernsydneyunipx.retrofit.APIClient;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.util.OnPlayClickListener;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.video.RecordVideoActivity;
import com.westernsydneyunipx.video.VideoPlayerActivity;
import com.westernsydneyunipx.voqual.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author PA1810.
 */
public class ParticipantActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnPlayClickListener, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);
    }


    @Override
    protected void setContent() {

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView tvName = header.findViewById(R.id.tvName);
        TextView tvEmail = header.findViewById(R.id.tvEmail);
        ImageView imageView = header.findViewById(R.id.imageView);
        SessionManager sessionManager = new SessionManager(ParticipantActivity.this);
        User user = sessionManager.getUser();
        //tvName.setText(user.toString());
        tvName.setText(user.getUsername());
        if (user.getProfile_pic() != null) {
            Picasso.with(this).load(APIClient.MEDIA_URL + "/profile_pic/" + user.getProfile_pic()).into(imageView);
        }
        tvEmail.setText(user.getEmail());
        imageView.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, MyLogsFragment.getInstance(this,
                new SessionManager(ParticipantActivity.this).getUser().getId()));
        fragmentTransaction.commit();

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_myLogs:
                getSupportActionBar().setTitle(getString(R.string.my_logs));
                fragment = MyLogsFragment.getInstance(this, new SessionManager(ParticipantActivity.this).getUser().getId());
                break;
            case R.id.nav_userDetails:
                getSupportActionBar().setTitle(getString(R.string.user_details));
                fragment = new ProfileFragment();
                break;
            case R.id.nav_researcherInfo:
                getSupportActionBar().setTitle(getString(R.string.researcher_info));
                fragment = new ResearcherInfoFragment();
                break;
            case R.id.nav_changePass:
                getSupportActionBar().setTitle(getString(R.string.change_password));
                fragment = new ChangePasswordFragment();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAudioPlay(MediaData mediaData) {
        AudioPopupDialog audioPopupDialog = new AudioPopupDialog(ParticipantActivity.this, mediaData);
        audioPopupDialog.show();
    }

    @Override
    public void onVideoPlay(MediaData mediaData) {
        Intent intent = new Intent(ParticipantActivity.this, VideoPlayerActivity.class);
        intent.putExtra("media", mediaData);
        startActivity(intent);
    }

    @OnClick(R.id.fab)
    void fabClick() {
        CharSequence options[] = new CharSequence[]{getString(R.string.audio), getString(R.string.video)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on options[which]
                if (which == 0) {
                    startActivity(new Intent(ParticipantActivity.this, RecordAudioActivity.class));
                } else {
                    startActivity(new Intent(ParticipantActivity.this, RecordVideoActivity.class));
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //the user clicked on Cancel
            }
        });
        builder.show();
    }

    public void logout() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ParticipantActivity.this);
        builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SessionManager sessionManager = new SessionManager(ParticipantActivity.this);
                        sessionManager.logout();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, new ProfileFragment());
                transaction.commit();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
    }
}
