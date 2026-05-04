package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();

        checkPermissions();
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        return navHostFragment.getNavController().navigateUp() || super.onSupportNavigateUp();
    }

    private void checkPermissions() {

        String permissionNeeded;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionNeeded = Manifest.permission.READ_MEDIA_AUDIO;
        } else {
            permissionNeeded = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permissionNeeded) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permissionNeeded}, REQUEST_PERMISSION_CODE);
        } else {
            loadAudioFiles();
        }
    }

    private void loadAudioFiles() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

            do {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);

                // Ez a konkrét fájl hivatkozása, amit a MediaPlayer-nek majd átadhatsz
                Uri contentUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));

                Log.d("AudioApp", "Zene található: " + title + " előadó: " + artist + " URI: " + contentUri);

                // Itt hozzáadhatod a fájlokat a RecyclerView számára)

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    MediaPlayer mediaPlayer = new MediaPlayer();

    private void playAudio(Uri contentUri) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), contentUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}