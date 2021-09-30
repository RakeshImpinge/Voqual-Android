package com.westernsydneyunipx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.util.OnPlayClickListener;
import com.westernsydneyunipx.voqual.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author PA1810.
 */

public class ResearcherMediaAdapter extends RecyclerView.Adapter<ResearcherMediaAdapter.PaymentViewHolder> {

    private Context context;
    private ArrayList<MediaData> mediaDataList;
    private OnPlayClickListener onMyLogsClickListener;

    public ResearcherMediaAdapter(Context context, ArrayList<MediaData> mediaDataList, OnPlayClickListener onMyLogsClickListener) {
        this.context = context;
        this.mediaDataList = mediaDataList;
        this.onMyLogsClickListener = onMyLogsClickListener;
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_logs, parent, false);
        return new PaymentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PaymentViewHolder holder, int position) {
        MediaData mediaData = mediaDataList.get(position);

        if (mediaData.getMedia_type() == 1) {
            holder.ivAudio.setVisibility(View.VISIBLE);
            holder.ivVideo.setVisibility(View.GONE);
        } else {
            holder.ivAudio.setVisibility(View.GONE);
            holder.ivVideo.setVisibility(View.VISIBLE);
        }

        holder.tvTitle.setText(mediaData.getTitle());
        holder.tvParticipant.setText(mediaData.getFirst_name() + " " + mediaData.getLast_name());
    }

    @Override
    public int getItemCount() {
        return mediaDataList.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvParticipant)
        TextView tvParticipant;
        @BindView(R.id.ivAudio)
        ImageView ivAudio;
        @BindView(R.id.ivVideo)
        ImageView ivVideo;

        PaymentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(itemView);
        }

        @OnClick(R.id.cardView)
        void openMyLogs() {
            MediaData mediaData = mediaDataList.get(getAdapterPosition());
            if (mediaData.getMedia_type() == 1) {
                onMyLogsClickListener.onAudioPlay(mediaData);
            } else {
                onMyLogsClickListener.onVideoPlay(mediaData);
            }
        }
    }
}
