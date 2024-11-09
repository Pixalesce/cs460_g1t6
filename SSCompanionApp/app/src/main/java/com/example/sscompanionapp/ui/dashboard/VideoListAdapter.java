// [app/src/main/java/com/example/sscompanionapp/ui/dashboard/VideoListAdapter.java](app/src/main/java/com/example/sscompanionapp/ui/dashboard/VideoListAdapter.java)
package com.example.sscompanionapp.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sscompanionapp.R;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private final List<String> videoUrls;
    private final Context context;

    public VideoListAdapter(Context context, List<String> videoUrls) {
        this.context = context;
        this.videoUrls = videoUrls;
    }

    @NonNull
    @Override
    public VideoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListAdapter.ViewHolder holder, int position) {
        String videoUrl = videoUrls.get(position);
        // Load video thumbnail using Glide
        Glide.with(context)
                .asBitmap()
                .load(videoUrl)
                .into(holder.thumbnailView);

        // Set up click listener to play video
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            intent.setDataAndType(Uri.parse(videoUrl), "video/*");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoUrls.size();
    }

    public void updateData(List<String> newUrls) {
        videoUrls.clear();
        videoUrls.addAll(newUrls);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.video_thumbnail);
        }
    }
}