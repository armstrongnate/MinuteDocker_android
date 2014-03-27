package com.example.app;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by nate on 3/26/14.
 */
public class Project {
    public static String TAG = "Project";
    public int externalId;
    public int contactId;
    public Boolean hidden;
    public String description;
    public String name;
    public String shortCode;
    public String defaultRateInDollars;

    public Project() {
    }

    public static Project fromJSONObject(JSONObject json) {
        Project project = new Project();
        try {
            if (!json.isNull("id"))
                project.externalId = json.getInt("id");
            if (!json.isNull("contact_id"))
                project.contactId = json.getInt("contact_id");
            if (!json.isNull("hidden"))
                project.hidden = json.getBoolean("hidden");
            if (!json.isNull("description"))
                project.shortCode = json.getString("description");
            if (!json.isNull("name"))
                project.name = json.getString("name");
            if (!json.isNull("short_code"))
                project.shortCode = json.getString("short_code");
            if (!json.isNull("default_rate_dollars"))
                project.defaultRateInDollars = json.getString("default_rate_dollars");
        }
        catch (Exception e) {
            Log.e(TAG, "Exception caught: ", e);
        }

        return project;
    }
}
