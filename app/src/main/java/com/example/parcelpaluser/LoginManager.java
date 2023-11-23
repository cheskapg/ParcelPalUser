package com.example.parcelpaluser;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginManager {
    private static final String LOGIN_PREFS = "LoginPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String USER_EMAIL = "userEmail";
    private static final String FCM_TOKEN = "fcmToken";
    private static final String USER_ID = "userId";

    public static void saveLoginState(Context context, boolean isLoggedIn) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public static void saveUserEmail(Context context, String userEmail) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_EMAIL, userEmail);
        editor.apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_EMAIL, "");
    }

    public static void saveFCMToken(Context context, String fcmToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FCM_TOKEN, fcmToken);
        editor.apply();
    }

    public static String getFCMToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FCM_TOKEN, "");
    }
    public static void clearFCMToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(FCM_TOKEN);
        editor.apply();
    }
    public static void saveUserId(Context context, String userid) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userid);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_ID, "");
    }

}