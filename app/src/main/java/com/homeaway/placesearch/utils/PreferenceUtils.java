package com.homeaway.placesearch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;
import java.util.Set;

public class PreferenceUtils {
    public static final String FAVORITE_LIST = "favorite_list";
    private static final String TAG = LogUtils.makeLogTag(PreferenceUtils.class);
    private static final String SHARED_PREFS_FILE = "placesearch_prefs";
    private static PreferenceUtils sInstance;
    private SharedPreferences mSharedPreferences;

    /**
     * Constructor of the singleton class
     */
    private PreferenceUtils(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        }
    }

    /**
     * This method returns the single instance of PreferenceUtils
     *
     * @return single instance of the class
     */
    public static synchronized PreferenceUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtils(context);
        }
        return sInstance;
    }

    /**
     * Retrieve all values from the preferences.
     *
     * @return Returns a map containing a list of pairs key/value representing
     * the preferences.
     */
    public Map<String, ?> getAllSharedPreferences() {
        return mSharedPreferences.getAll();
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return Returns the preference value if it exists, or false.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a boolean.
     */
    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * Set a boolean value in the preferences editor, to be written back
     * once commit or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public void putBoolean(String key, boolean value) {
        Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Retrieve a float value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return returns the preference value if it exists, or 0.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a float.
     */
    public float getFloat(String key) {
        return mSharedPreferences.getFloat(key, 0);
    }

    /**
     * Set a float value in the preferences editor, to be written back once
     * commit or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public void putFloat(String key, float value) {
        LogUtils.info(TAG, "Value: " + value);
        Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return Returns the preference value if it exists, or 0.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     */
    public int getInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    /**
     * Set an int value in the preferences editor, to be written back once
     * commit or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public void putInt(String key, int value) {
        LogUtils.info(TAG, "Value: " + value);
        Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Retrieve a long value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return Returns the preference value if it exists, or 0.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a long.
     */
    public long getLong(String key) {
        return mSharedPreferences.getLong(key, 0);
    }

    /**
     * Set a long value in the preferences editor, to be written back once
     * commit or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     *              to persistent storage.
     */
    public void putLong(String key, long value) {
        LogUtils.info(TAG, "Value: " + value);
        Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Retrieve a String value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return Returns the preference value if it exists, or null.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a String.
     */
    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     * commit or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference. Supplying null
     *              as the value is equivalent to calling remove String with
     *              this key.
     */
    public void putString(String key, String value) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Retrieve a set of String values from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return Returns the preference values if they exist, or null.
     * Throws ClassCastException if there is a preference with this name
     * that is not a Set.
     */
    public Set<String> getStringSet(String key) {
        return mSharedPreferences.getStringSet(key, null);
    }

    /**
     * Set a set of String values in the preferences editor, to be written
     * back once commit or apply is called.
     *
     * @param key   The name of the preference to modify.
     * @param value The set of new values for the preference.  Passing null
     *              for this argument is equivalent to calling remove Sting with
     *              this key.
     */
    public void putStringSet(String key, Set<String> value) {
        LogUtils.info(TAG, "Value: " + value);
        Editor editor = mSharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public void removePreference(String key) {
        LogUtils.info(TAG, "Remove : " + key);
        if (mSharedPreferences.contains(key)) {
            Editor editor = mSharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    public boolean containsKey(String key) {
        return mSharedPreferences.contains(key);
    }
}
