package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Music music);
    }

    private final List<Music> musicList;
    private final OnItemClickListener listener;

    public MusicAdapter(List<Music> musicList, OnItemClickListener listener) {
        this.musicList = musicList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.musicTitle);
            artist = itemView.findViewById(R.id.musicArtist);
        }

        public void bind(Music music, OnItemClickListener listener) {
            title.setText(music.title != null ? music.title : "Unknown Title");
            artist.setText(music.artist != null ? music.artist : "Unknown Artist");

            itemView.setOnClickListener(v -> listener.onItemClick(music));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(musicList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }
}