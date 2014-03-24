package com.example.app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by nate on 3/23/14.
 */
public class Entry {
    public static String TAG = "Entry";
    public int duration; // in seconds
    public int account_id;
    public int contact_id;
    public String description;
    public int external_id;
    public String logged_at;
    public boolean active;
    public JSONArray taskIds;
    public int project_id;
    public String updated_at;
    public int user_id;

    public Entry() {
    }

    public static Entry fromJSONObject(JSONObject jsonObject) {
        Entry entry = new Entry();
        try {
            entry.account_id = jsonObject.getInt("account_id");
            entry.contact_id = jsonObject.getInt("contact_id");
            entry.description = jsonObject.getString("description");
            entry.duration = jsonObject.getInt("duration");
            entry.external_id = jsonObject.getInt("id");
            entry.logged_at = jsonObject.getString("logged_at");
            entry.project_id = jsonObject.getInt("project_id");
            entry.user_id = jsonObject.getInt("user_id");
            entry.active = jsonObject.getBoolean("timer_active");
            entry.updated_at = jsonObject.getString("updated_at");
            entry.taskIds = jsonObject.getJSONArray("task_ids");
        }
        catch (Exception e) {
            Log.e(TAG, "Exception caught: ", e);
        }

        return entry;
    }
}
