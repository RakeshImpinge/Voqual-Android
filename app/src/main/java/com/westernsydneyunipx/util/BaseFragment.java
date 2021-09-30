package com.westernsydneyunipx.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.westernsydneyunipx.voqual.R;

import butterknife.ButterKnife;

import static android.view.View.GONE;

public abstract class BaseFragment extends Fragment {

    private View rootView;
    private ProgressDialog mProgressDialog;

    protected abstract int setFragmentLayout();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(setFragmentLayout(), container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setContent(view);
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract void setContent(View rootView);

    public boolean isNetworkConnected() {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            showError(getString(R.string.error_internet));
            return false;
        } else {
            return true;
        }
    }

    public void showError(String errorMsg) {
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
    }

    public void showSuccess(String successMsg) {
        Toast.makeText(getActivity(), successMsg, Toast.LENGTH_LONG).show();
    }

    protected void setVisibleGone(int tv_emptyData) {
        rootView.findViewById(tv_emptyData).setVisibility(GONE);
    }

    protected void setVisible(int tv_emptyData) {
        rootView.findViewById(tv_emptyData).setVisibility(View.VISIBLE);
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    protected View getRootView() {
        return rootView;
    }

    protected RecyclerView setLinearRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        return recyclerView;
    }

    protected RecyclerView setRecyclerView(RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        return recyclerView;
    }

    public void showLoading(String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public void showVideoLoading(Context context, String message) {
        mProgressDialog = new ProgressDialog(context);
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
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    public void pushFragmentWithBackStack(Fragment DestinationFragment) {
        try {
            Fragment SourceFragment = this;
            int viewResourceID = ((ViewGroup) SourceFragment.getView().getParent()).getId();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(viewResourceID, DestinationFragment);
            ft.hide(SourceFragment);
            ft.addToBackStack(SourceFragment.getClass().getName());
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment DestinationFragment) {
        Fragment SourceFragment = this;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        int viewResourceID = ((ViewGroup) SourceFragment.getView().getParent()).getId();
        ft.replace(viewResourceID, DestinationFragment);
        ft.commit();
    }

    static public void replaceFragmentInContainer(Fragment DestinationFragment, int containerResourceID, FragmentManager fragmentManager) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        int viewResourceID = containerResourceID;
        ft.replace(viewResourceID, DestinationFragment);
        ft.commit();
    }

    public boolean popFragment(Fragment SourceFragment) {
        getFragmentManager().popBackStack();
        return true;
    }

//        int Count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
//        if (Count == 0) {
//            getActivity().finish();
//            return true;
//        } else {
//            return popFragment(this);
//        }
//    }
}
