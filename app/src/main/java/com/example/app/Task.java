package com.example.app;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by nate on 3/26/14.
 */
public class Task {
    public static String TAG = "Task";
    public int externalId;
    public String description;
    public String name;
    public String shortCode;
    public String defaultRateInDollars;
    public Boolean hidden;

    public Task() {
    }

    public static Task fromJSONObject(JSONObject json) {
        Task task = new Task();
        try {
            if (!json.isNull("id"))
                task.externalId = json.getInt("id");
            if (!json.isNull("hidden"))
                task.hidden = json.getBoolean("hidden");
            if (!json.isNull("description"))
                task.shortCode = json.getString("description");
            if (!json.isNull("name"))
                task.name = json.getString("name");
            if (!json.isNull("short_code"))
                task.shortCode = json.getString("short_code");
            if (!json.isNull("default_rate_dollars"))
                task.defaultRateInDollars = json.getString("default_rate_dollars");
        }
        catch (Exception e) {
            Log.e(TAG, "Exception caught: ", e);
        }

        return task;
    }
}
