package com.westernsydneyunipx.customer_support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.westernsydneyunipx.voqual.R;

public class ContactSupportFragment extends Fragment {


    public ContactSupportFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_support, container, false);

        view.findViewById(R.id.open_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:hazelkeedle@gmail.com"));
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Support");
                startActivity(Intent.createChooser(i, "Send feedback"));
            }
        });
        return view;
    }
}