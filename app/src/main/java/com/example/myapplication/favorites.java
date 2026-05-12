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

public class favorites extends Fragment {

    private RecyclerView recyclerView;
    private MusicAdapter adapter;
    private List<Music> favoriteMusicList = new ArrayList<>();

    //változók
    private MediaPlayer mediaPlayer;
    private int currentIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        //MediaPlayer példányosítása
        mediaPlayer = new MediaPlayer();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Gombok bekötése
        ImageButton btnPrev = view.findViewById(R.id.imageButton);     // Vissza
        ImageButton btnPlay = view.findViewById(R.id.imageButton2);    // Play
        ImageButton btnPause = view.findViewById(R.id.imageButton3);   // Pause
        ImageButton btnNext = view.findViewById(R.id.imageButton4);    // Következő
        ImageButton homeButton = view.findViewById(R.id.imageButton6); // Home

        //play
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });

        //pause
        btnPause.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        //kövi
        btnNext.setOnClickListener(v -> {
            if (favoriteMusicList != null && !favoriteMusicList.isEmpty()) {
                if (currentIndex < favoriteMusicList.size() - 1) {
                    currentIndex++;
                    playAudio(favoriteMusicList.get(currentIndex));
                }
            }
        });

        //előző
        btnPrev.setOnClickListener(v -> {
            if (favoriteMusicList != null && !favoriteMusicList.isEmpty()) {
                if (currentIndex > 0) {
                    currentIndex--;
                    playAudio(favoriteMusicList.get(currentIndex));
                }
            }
        });

        //home
        homeButton.setOnClickListener(v -> {
            // Elnavigáláskor a zene kikapcsol
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            navController.navigate(R.id.nav_to_home);
        });

        return view;
    }

    //Biztosítjuk, hogy a mediaPlayer létezzen, ha visszajövünk a Home-ról
    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        loadFavoriteAudioFiles();
    }

    //Ha kilépünk az alkalmazásból, a zene leáll
    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    //kedvencek betöltése
    private void loadFavoriteAudioFiles() {
        favoriteMusicList.clear();

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

                //Szűrés a kedvencekre
                if (FavStorage.isFavourite(requireContext(), id)) {
                    String title = cursor.getString(titleColumn);
                    String artist = cursor.getString(artistColumn);
                    long albumId = cursor.getLong(albumColumn);

                    Uri albumUri = Uri.parse("content://media/external/audio/albumart");
                    Uri coverUri = Uri.withAppendedPath(albumUri, String.valueOf(albumId));

                    Music m = new Music(id, title, artist, coverUri.toString());
                    m.isFavourite = true;

                    favoriteMusicList.add(m);
                }
            }
            cursor.close();
        }

        //Adapter bekötése lejátszás eseménnyel
        if (adapter == null) {
            adapter = new MusicAdapter(favoriteMusicList, music -> playAudio(music));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    //zene lejátszása
    private void playAudio(Music music) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }

            Uri contentUri = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    String.valueOf(music.id)
            );

            mediaPlayer.reset();
            mediaPlayer.setDataSource(requireContext(), contentUri);
            mediaPlayer.prepare();
            mediaPlayer.start();

            //Aktuális pozíció elmentése a léptetéshez
            currentIndex = favoriteMusicList.indexOf(music);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}