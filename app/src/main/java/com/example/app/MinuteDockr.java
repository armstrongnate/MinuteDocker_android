package com.example.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nate on 3/20/14.
 */
public class MinuteDockr {
    private static final String PREFS_FILE = "minute_dockr";
    public static final String CURRENT_ACCOUNT_ID_PREFS_KEY = "current_account_id";
    public static final String API_KEY_PREFS_KEY = "api_key";
    private static final String MY_API_KEY = "0e3ec0f390e9b7aff763d64d8cea6c50";
    private static final String TAG = "MinuteDockr";
    private static MinuteDockr instance = null;
    public SharedPreferences sharedPreferences;
    public Context context;
    public String currentApiKey;

    private MinuteDockr(Context appContext) {
        context = appContext;
        sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        currentApiKey = sharedPreferences.getString(API_KEY_PREFS_KEY, "");
    }

    public static MinuteDockr getInstance(Context context) {
        if (instance == null) {
            instance = new MinuteDockr(context);
        }
        return instance;
    }

    public View customDialogView(int titleTextId, int messageTextId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customDialog = inflater.inflate(R.layout.md_custom_dialog, null);
        TextView title = (TextView) customDialog.findViewById(R.id.title);
        title.setText(titleTextId);
        TextView message = (TextView) customDialog.findViewById(R.id.message);
        message.setText(messageTextId);
        Typeface extraBold = Typeface.createFromAsset(context.getAssets(), "Proxima_Nova_Extrabold.ttf");
        Typeface semiBold = Typeface.createFromAsset(context.getAssets(), "Proxima_Nova_Semibold.ttf");
        title.setTypeface(extraBold);
        message.setTypeface(semiBold);

        return customDialog;
    }
}
