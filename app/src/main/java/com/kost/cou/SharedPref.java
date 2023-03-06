package com.kost.cou;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.HashMap;

public class SharedPref
{
    private static SharedPref INSTANCE = null;
    private String morning;
    private String evening;

    private SharedPref() {

    }

    public static SharedPref getInstance(Context context) {
        synchronized (SharedPref.class) {
            if (INSTANCE == null) {
                synchronized (SharedPref.class) {
                    if (INSTANCE == null)
                        INSTANCE = new SharedPref();
                }
            }
        }
        return INSTANCE;
    }

    public String getMorning() {
        return morning;
    }

    public void setMorning(String morning) {
        this.morning = morning;
    }

    public String getEvening() {
        return evening;
    }

    public void setEvening(String evening) {
        this.evening = evening;
    }

    //    private static SharedPreferences mSharedPref;
//    public static final String TIME_MORNING = "timeMorning";
//    public static final String TIME_EVENING = "timeEvening";
//
//    private SharedPref()
//    {
//
//    }
//
//    public static void init(Context context)
//    {
//        if (mSharedPref == null)
//            mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//    }
//
//    public static String read(String key, String defValue) {
//        return mSharedPref.getString(key, defValue);
//    }
//
//    public static void write(String key, String value) {
//        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
//        prefsEditor.putString(key, value);
//        prefsEditor.commit();
//    }
}
