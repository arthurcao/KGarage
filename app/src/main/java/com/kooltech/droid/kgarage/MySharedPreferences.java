package com.kooltech.droid.kgarage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by NgocTien on 5/18/2015.
 */
public class MySharedPreferences {

    public final static String CONFIG_SETTING	= "CONFIG_SETTING";

    public final static String CONFIGURE_USERNAME  = "CONFIGURE_USERNAME";
    public static void saveUser(Context context, String user) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor 	  = mySharedPreferences.edit();
        editor.putString(CONFIGURE_USERNAME, user);
        editor.commit();
    }
    public static String loadUser(Context context)  {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(CONFIGURE_USERNAME, "");
    }


    public final static String CONFIGURE_PASSWORD  = "CONFIGURE_PASSWORD";
    public static void savePassword(Context context, String user) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor 	  = mySharedPreferences.edit();
        editor.putString(CONFIGURE_PASSWORD, user);
        editor.commit();
    }
    public static String loadPassword(Context context)  {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(CONFIGURE_PASSWORD, "");
    }



    public final static String CONFIGURE_LOGIN  = "CONFIGURE_LOGIN";
    public static void saveLoginStatus(Context context, boolean status) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor 	  = mySharedPreferences.edit();
        editor.putBoolean(CONFIGURE_LOGIN, status);
        editor.commit();
    }
    public static boolean loadLoginStatus(Context context)  {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        return mySharedPreferences.getBoolean(CONFIGURE_LOGIN, false);
    }

    public final static String CONFIGURE_GCM_REGISTERID  = "CONFIGURE_GCM_REGISTERID";
    public static void saveRegisterID(Context context, String value) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor 	  = mySharedPreferences.edit();
        editor.putString(CONFIGURE_GCM_REGISTERID, value);
        editor.commit();
    }
    public static String loadRegisterID(Context context)  {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(CONFIGURE_GCM_REGISTERID, "");
    }


    public final static String CONFIGURE_FACEBOOK_TOKEN = "CONFIGURE_FACEBOOK_TOKEN";
    public static void saveFacebookAccessToken(Context context, String value) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor 	  = mySharedPreferences.edit();
        editor.putString(CONFIGURE_FACEBOOK_TOKEN, value);
        editor.commit();
    }
    public static String loadFacebookAccessToken(Context context)  {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(CONFIGURE_FACEBOOK_TOKEN, "");
    }

    public final static String CONFIGURE_LOGIN_TYPE = "CONFIGURE_LOGIN_TYPE";
    //1: account
    //2: facebok
    public static void saveLoginType(Context context, int value) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor 	  = mySharedPreferences.edit();
        editor.putInt(CONFIGURE_LOGIN_TYPE, value);
        editor.commit();
    }
    public static int loadLoginType(Context context)  {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt(CONFIGURE_LOGIN_TYPE, 1);
    }

    public final static String CONFIGURE_AUTO_LOGIN = "CONFIGURE_AUTO_LOGIN";
    //1: account
    //2: facebok
    public static void saveAutoLogin(Context context, boolean value) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor 	  = mySharedPreferences.edit();
        editor.putBoolean(CONFIGURE_AUTO_LOGIN, value);
        editor.commit();
    }
    public static boolean loadAutoLogin(Context context)  {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(CONFIG_SETTING, Activity.MODE_PRIVATE);
        return mySharedPreferences.getBoolean(CONFIGURE_AUTO_LOGIN, false);
    }

}
