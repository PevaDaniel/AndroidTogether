package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavStorage {

    private static final String PREF_NAME = "favourites";
    private static final String KEY_FAVS = "fav_ids";

    public static void saveFavourite(Context context, long id) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favs = new HashSet<>(prefs.getStringSet(KEY_FAVS, new HashSet<>()));
        favs.add(String.valueOf(id));
        prefs.edit().putStringSet(KEY_FAVS, favs).apply();
    }

    public static void removeFavourite(Context context, long id) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favs = new HashSet<>(prefs.getStringSet(KEY_FAVS, new HashSet<>()));
        favs.remove(String.valueOf(id));
        prefs.edit().putStringSet(KEY_FAVS, favs).apply();
    }

    public static boolean isFavourite(Context context, long id) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favs = prefs.getStringSet(KEY_FAVS, new HashSet<>());
        return favs.contains(String.valueOf(id));
    }
}
