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
    public int accountId;
    public int contactId;
    public String description;
    public int externalId;
    public String loggedAt;
    public boolean isActive;
    public JSONArray taskIds;
    public int projectId;
    public String updatedAt;
    public int userId;

    public Entry() {
    }

    public static Entry fromJSONObject(JSONObject jsonObject) {
        Entry entry = new Entry();
        try {
            if (!jsonObject.isNull("account_id"))
                entry.accountId = jsonObject.getInt("account_id");
            if (!jsonObject.isNull("contact_id"))
                entry.contactId = jsonObject.getInt("contact_id");
            if (!jsonObject.isNull("description"))
                entry.description = jsonObject.getString("description");
            if (!jsonObject.isNull("duration"))
                entry.duration = jsonObject.getInt("duration");
            if (!jsonObject.isNull("id"))
                entry.externalId = jsonObject.getInt("id");
            if (!jsonObject.isNull("logged_at"))
                entry.loggedAt = jsonObject.getString("logged_at");
            if (!jsonObject.isNull("project_id"))
                entry.projectId = jsonObject.getInt("project_id");
            if (!jsonObject.isNull("user_id"))
                entry.userId = jsonObject.getInt("user_id");
            if (!jsonObject.isNull("timer_active"))
                entry.isActive = jsonObject.getBoolean("timer_active");
            if (!jsonObject.isNull("updated_at"))
                entry.updatedAt = jsonObject.getString("updated_at");
            if (!jsonObject.isNull("task_ids"))
                entry.taskIds = jsonObject.getJSONArray("task_ids");
        }
        catch (Exception e) {
            Log.e(TAG, "Exception caught: ", e);
        }

        return entry;
    }
}
