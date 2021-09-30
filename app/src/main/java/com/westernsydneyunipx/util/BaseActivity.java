package com.westernsydneyunipx.util;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.westernsydneyunipx.voqual.R;

import butterknife.ButterKnife;

public abstract class
BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        setContent();
    }

    protected abstract void setContent();

    public boolean isNetworkConnected() {
        if (!NetworkUtils.isNetworkConnected(BaseActivity.this)) {
            showError(getString(R.string.error_internet));
            return false;
        } else {
            return true;
        }
    }

    public void showError(String errorMsg) {
        Toast.makeText(BaseActivity.this, errorMsg, Toast.LENGTH_LONG).show();
    }

    public void showSuccess(String successMsg) {
        Toast.makeText(BaseActivity.this, successMsg, Toast.LENGTH_LONG).show();
    }

    protected RecyclerView setLinearRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        return recyclerView;
    }

    protected RecyclerView setGridRecyclerView(RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        return recyclerView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showLoading(String message) {
        mProgressDialog = new ProgressDialog(BaseActivity.this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}
