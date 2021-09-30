package com.westernsydneyunipx.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.voqual.R;

import java.util.ArrayList;

/**
 * @author PA1810.
 */

public class SpinnerAdapter extends BaseAdapter {

    private final LayoutInflater inflter;
    private Context context;
    private ArrayList<User> userList;

    public SpinnerAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;

        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflter.inflate(R.layout.item_spinner, null);
        TextView tvUsername = view.findViewById(R.id.tvName);
        tvUsername.setText(userList.get(position).getUsername());
        return view;
    }
}
