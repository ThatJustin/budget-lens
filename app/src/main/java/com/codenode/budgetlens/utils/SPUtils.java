package com.codenode.budgetlens.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    public static final String CONFIG = "config";
    public static SharedPreferences sp;

    public static boolean contains(Context context, String keyName){
        if (sp == null)
            sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        return sp.contains(keyName);
    }

    public synchronized static void saveBoolean(Context context, String key, boolean value) {
        if (sp == null)
            sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);

        sp.edit().putBoolean(key, value).commit();
    }

    public synchronized static boolean getBoolean(Context context, String key, boolean defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(CONFIG, 0);
        return sp.getBoolean(key, defValue);
    }

    public synchronized static void saveStringData(Context context, String key, String value){
        if(sp == null){
            sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }

        sp.edit().putString(key, value).commit();
    }

    public synchronized static String getStringData(Context context, String key, String defValue){
        if(sp == null){
            sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    public synchronized static void saveIntData(Context context, String key, int value){
        if(sp == null){
            sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }

        sp.edit().putInt(key, value).commit();
    }

    public synchronized static int getIntData(Context context, String key, int defValue){
        if(sp == null){
            sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }


}
