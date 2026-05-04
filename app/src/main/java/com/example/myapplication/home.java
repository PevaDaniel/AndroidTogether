package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Music> musicList = new ArrayList<>();
        musicList.add(new Music("Song 1", "Artist 1"));
        musicList.add(new Music("Song 2", "Artist 2"));
        musicList.add(new Music("Song 3", "Artist 3"));

        MusicAdapter adapter = new MusicAdapter(musicList);
        recyclerView.setAdapter(adapter);
        ImageButton favButton  = view.findViewById(R.id.imageButton);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                navController.navigate(R.id.nav_to_fav);
            }
        });
        return view;
    }
}