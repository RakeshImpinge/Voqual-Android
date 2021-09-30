package com.westernsydneyunipx.reseracher;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.westernsydneyunipx.audio.AudioPopupDialog;
import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.model.Participant;
import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.util.BaseActivity;
import com.westernsydneyunipx.util.OnParticipantClickListener;
import com.westernsydneyunipx.util.OnPlayClickListener;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.video.VideoPlayerActivity;
import com.westernsydneyunipx.voqual.R;

import butterknife.BindView;

public class ResearcherActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnPlayClickListener, OnParticipantClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher);
    }

    @Override
    protected void setContent() {



        setSupportActionBar(toolbar);
        fab.setVisibility(View.GONE);

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
        SessionManager sessionManager = new SessionManager(ResearcherActivity.this);
        User user = sessionManager.getUser();
        tvName.setText(user.toString());
        tvEmail.setText(user.getEmail());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new AllLogsFragment());
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
            case R.id.nav_allLogs:
                getSupportActionBar().setTitle(getString(R.string.all_logs));
                fragment = new AllLogsFragment();
                break;
            case R.id.nav_myParticipate:
                getSupportActionBar().setTitle(getString(R.string.my_participant));
                fragment = new MyParticipantFragment();
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
        AudioPopupDialog audioPopupDialog = new AudioPopupDialog(ResearcherActivity.this, mediaData);
        audioPopupDialog.show();
    }

    @Override
    public void onVideoPlay(MediaData mediaData) {
        Intent intent = new Intent(ResearcherActivity.this, VideoPlayerActivity.class);
        intent.putExtra("media", mediaData);
        startActivity(intent);
    }

    @Override
    public void onParticipantClick(Participant participant) {
        Intent intent = new Intent(ResearcherActivity.this, ParticipantListActivity.class);
        intent.putExtra("userId", participant.getId());
        startActivity(intent);
    }

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResearcherActivity.this);
        builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SessionManager sessionManager = new SessionManager(ResearcherActivity.this);
                        sessionManager.logout();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
