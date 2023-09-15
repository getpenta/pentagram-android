package org.telegram.pentagram.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

@SuppressLint("ApplySharedPref")
public abstract class Preferences{

    private final SharedPreferences preferences;

    protected Preferences(String label) {
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences(label, Context.MODE_PRIVATE);
    }

    protected void set(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    protected void set(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    protected void set(String key, float value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    protected void set(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    protected void set(String key, Boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    protected int get(String key, int def) {
        return preferences.getInt(key, def);
    }

    protected long get(String key, long def) {
        return preferences.getLong(key, def);
    }

    protected float get(String key, float def) {
        return preferences.getFloat(key, def);
    }

    protected String get(String key, String def) {
        return preferences.getString(key, def);
    }

    protected Boolean get(String key, Boolean def) {
        return preferences.getBoolean(key, def);
    }

    void clear() {
        preferences.edit().clear().commit();
    }

}
