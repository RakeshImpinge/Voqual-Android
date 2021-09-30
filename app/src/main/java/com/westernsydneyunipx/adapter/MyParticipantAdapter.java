package com.westernsydneyunipx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.westernsydneyunipx.model.Participant;
import com.westernsydneyunipx.util.OnParticipantClickListener;
import com.westernsydneyunipx.voqual.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author PA1810.
 */

public class MyParticipantAdapter extends RecyclerView.Adapter<MyParticipantAdapter.MyParticipantViewHolder> {

    private Context context;
    private ArrayList<Participant> participantArrayList;
    private OnParticipantClickListener onParticipantClickListener;

    public MyParticipantAdapter(Context context, ArrayList<Participant> participantArrayList, OnParticipantClickListener onParticipantClickListener) {
        this.context = context;
        this.participantArrayList = participantArrayList;
        this.onParticipantClickListener = onParticipantClickListener;
    }

    @Override
    public MyParticipantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant, parent, false);
        return new MyParticipantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyParticipantViewHolder holder, int position) {
        Participant participant = participantArrayList.get(position);

        holder.tvName.setText(participant.getFirst_name() + " " + participant.getLast_name());
        holder.tvUsername.setText(participant.getUsername());
    }

    @Override
    public int getItemCount() {
        return participantArrayList.size();
    }

    class MyParticipantViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvUsername)
        TextView tvUsername;

        MyParticipantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(itemView);
        }

        @OnClick(R.id.cardView)
        void openMyLogs() {
            onParticipantClickListener.onParticipantClick(participantArrayList.get(getAdapterPosition()));
        }
    }
}
