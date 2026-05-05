package com.example.myapplication;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class home extends Fragment {

    private RecyclerView recyclerView;
    private List<Music> musicList = new ArrayList<>();
    private MusicAdapter adapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MusicAdapter(musicList, music -> playAudio(music));
        recyclerView.setAdapter(adapter);

        loadAudioFiles();

        ImageButton favButton = view.findViewById(R.id.imageButton);
        favButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(
                    requireActivity(), R.id.fragmentContainerView);
            navController.navigate(R.id.nav_to_fav);
        });

        return view;
    }

    private void loadAudioFiles() {
        ContentResolver resolver = requireContext().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = resolver.query(uri, projection, selection, null, null);

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);

                musicList.add(new Music(id, title, artist));
            }

            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    private void playAudio(Music music) {
        try {
            Uri contentUri = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    String.valueOf(music.id)
            );

            mediaPlayer.reset();
            mediaPlayer.setDataSource(requireContext(), contentUri);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}