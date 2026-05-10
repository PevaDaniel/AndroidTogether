package com.example.myapplication;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class home extends Fragment {

    private RecyclerView recyclerView;
    private List<Music> musicList = new ArrayList<>();
    private MusicAdapter adapter;

    private MediaPlayer mediaPlayer;
    private int currentIndex = -1;

    private ProgressBar progressBar;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mediaPlayer = new MediaPlayer();

        recyclerView = view.findViewById(R.id.RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MusicAdapter(musicList, music -> playAudio(music));
        recyclerView.setAdapter(adapter);

        loadAudioFiles();

        ImageButton playBtn = view.findViewById(R.id.PlayButton);
        ImageButton pauseBtn = view.findViewById(R.id.PauseButton);
        ImageButton skipForward = view.findViewById(R.id.SkipForward);
        ImageButton skipBackward = view.findViewById(R.id.SkipBackward);
        progressBar = view.findViewById(R.id.MusicProgress);

        playBtn.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });

        pauseBtn.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        skipForward.setOnClickListener(v -> {
            if (currentIndex < musicList.size() - 1) {
                currentIndex++;
                playAudio(musicList.get(currentIndex));
            }
        });

        skipBackward.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                playAudio(musicList.get(currentIndex));
            }
        });

        ImageButton favButton = view.findViewById(R.id.imageButton);
        favButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(
                    requireActivity(), R.id.fragmentContainerView);
            navController.navigate(R.id.nav_to_fav);
        });

        handler.post(updateProgress);

        return view;
    }

    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                progressBar.setMax(mediaPlayer.getDuration());
                progressBar.setProgress(mediaPlayer.getCurrentPosition());
            }
            handler.postDelayed(this, 300);
        }
    };

    private void loadAudioFiles() {
        ContentResolver resolver = requireContext().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID
        };

        Cursor cursor = resolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);
                long albumId = cursor.getLong(albumColumn);

                Uri albumUri = Uri.parse("content://media/external/audio/albumart");
                Uri coverUri = Uri.withAppendedPath(albumUri, String.valueOf(albumId));

                Music m = new Music(id, title, artist, coverUri.toString());
                m.isFavourite = FavStorage.isFavourite(getContext(), id);

                musicList.add(m);
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

            currentIndex = musicList.indexOf(music);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
