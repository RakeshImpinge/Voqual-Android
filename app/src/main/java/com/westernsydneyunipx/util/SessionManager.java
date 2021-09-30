package com.westernsydneyunipx.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.participant.ParticipantActivity;
import com.westernsydneyunipx.reseracher.ResearcherActivity;
import com.westernsydneyunipx.voqual.LoginActivity;

import java.lang.reflect.Type;

/**
 * @author PA1810.
 */

public class SessionManager {

    private static Editor editor;
    private SharedPreferences pref;

    // New code -----------------------------------------------
    private static final String PREF_NAME = "VoQual";
    private static final String IS_LOGIN = "is_login";
    private static final String USER = "user";

    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLogin(User user, boolean keepLogin) {
        if (keepLogin) {
            editor.putBoolean(IS_LOGIN, true);
        }

        Gson gson = new Gson();
        String personString = gson.toJson(user);
        editor.putString(USER, personString);

        editor.commit();
    }

    public User getUser() {
        Gson gson = new Gson();
        String json = pref.getString(USER, null);
        Type type = new TypeToken<User>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent notLogin = new Intent(context, LoginActivity.class);
            notLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(notLogin);
        } else {
            Intent login;

            login = new Intent(context, ParticipantActivity.class);
           /* if (getUser().getResearcher_id() != 0 ) {
                login = new Intent(context, ParticipantActivity.class);
            } else {
                login = new Intent(context, ResearcherActivity.class);
            }*/
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(login);
        }
    }

    public void logout() {
        editor.clear();
        editor.commit();

        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
