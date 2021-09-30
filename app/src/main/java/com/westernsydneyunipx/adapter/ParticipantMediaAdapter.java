package com.westernsydneyunipx.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.participant.ParticipantActivity;
import com.westernsydneyunipx.util.OnPlayClickListener;
import com.westernsydneyunipx.util.SessionManager;
import com.westernsydneyunipx.voqual.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author PA1810.
 */
public class ParticipantMediaAdapter extends RecyclerView.Adapter<ParticipantMediaAdapter.PaymentViewHolder> {

    private Context context;
    private ArrayList<MediaData> mediaDataList;
    private OnPlayClickListener onMyLogsClickListener;
    public clickInterface clickInterface;
    public VideoDeleteInterface videoDeleteInterface;

    public ParticipantMediaAdapter(Context context, ArrayList<MediaData> mediaDataList, OnPlayClickListener onMyLogsClickListener) {
        this.context = context;
        this.mediaDataList = mediaDataList;
        this.onMyLogsClickListener = onMyLogsClickListener;
    }


    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_logs, parent, false);
        return new PaymentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PaymentViewHolder holder, final int position) {
        final MediaData mediaData = mediaDataList.get(position);
        if (mediaData.getMedia_type() == 1) {
            holder.ivAudio.setVisibility(View.VISIBLE);
            holder.ivVideo.setVisibility(View.GONE);
        } else {
            holder.ivAudio.setVisibility(View.GONE);
            holder.ivVideo.setVisibility(View.VISIBLE);
        }

        holder.tvTitle.setText(mediaData.getTitle());
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaData.getMedia_type()==1)
                {

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to Delete this audio?")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    clickInterface.onDelete(String.valueOf(mediaDataList.get(position).getId()),position);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();

                }else if (mediaData.getMedia_type()==2)
                    {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to Delete this video?")
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        videoDeleteInterface.onVideoDelete(String.valueOf(mediaDataList.get(position).getId()),position);
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaDataList.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.ivAudio)
        ImageView ivAudio;
        @BindView(R.id.ivVideo)
        ImageView ivVideo;
        @BindView(R.id.iv_delete)
        ImageView iv_delete;


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


    public  interface clickInterface{

        void onDelete(String s, int i);

    }

    public void onClik(clickInterface onClick)
    {
        this.clickInterface=onClick;
    }


    public interface VideoDeleteInterface{

       void onVideoDelete(String id , int pos);

    }


    public void onVideodeleteClick(VideoDeleteInterface videoDeleteInterface)
    {
           this.videoDeleteInterface=videoDeleteInterface;
    }
}
