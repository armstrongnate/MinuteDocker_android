package com.example.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by nate on 3/23/14.
 */

public class Entry {

    protected enum EntryApiAction {
        START_ACTION, PAUSE_ACTION, UPDATE_ACTION
    }

    public static String TAG = "Entry";
    public int duration; // in seconds
    public int accountId;
    public int contactId;
    public String description;
    public int externalId;
    public String loggedAt;
    public boolean isActive;
    public int [] taskIds;
    public int projectId;
    public String updatedAt;
    public int userId;

    public Entry() {
    }

    public static Entry fromJSONObject(JSONObject jsonObject) {
        Entry entry = new Entry();
        try {
            entry.accountId = -1;
            entry.contactId = -1;
            entry.projectId = -1;
            entry.taskIds = new int[0];
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
            if (!jsonObject.isNull("task_ids")) {
                JSONArray jsonTaskIds = jsonObject.getJSONArray("task_ids");
                entry.taskIds = new int[jsonTaskIds.length()];
                for (int i=0; i<jsonTaskIds.length(); i++) {
                    entry.taskIds[i] = jsonTaskIds.getInt(i);
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Exception caught: ", e);
        }

        return entry;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("account_id", accountId);
            jsonObject.accumulate("contact_id", contactId);
            jsonObject.accumulate("description", description);
            jsonObject.accumulate("duration", duration);
            jsonObject.accumulate("logged_at", loggedAt);
            jsonObject.accumulate("project_id", projectId);
            jsonObject.accumulate("user_id", userId);
            jsonObject.accumulate("timer_active", isActive);
            JSONArray jsonTaskIds = new JSONArray();
            for (int i=0; i<taskIds.length; i++) {
                jsonTaskIds.put(taskIds[i]);
            }
            jsonObject.accumulate("task_ids", jsonTaskIds);
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return jsonObject;
    }

    public JSONObject toParams() {
        JSONObject params = new JSONObject();
        try {
            params.accumulate("entry", toJSONObject());
        }
        catch (Exception e) {
            Log.d("InputString", e.getLocalizedMessage());
        }
        return params;
    }

    public void toggleActive(Context context, AsyncTaskCompleteListener<String> cb) {
        isActive = !isActive;
        MinuteDockr dockr = MinuteDockr.getInstance(context);
        if (isActive) {
            new HttpAsyncTask(context, cb, EntryApiAction.START_ACTION)
                    .execute(String.format("%sentries/current/start.json?api_key=%s", dockr.baseUrl, dockr.getCurrentApiKey()));
        }
        else {
            new HttpAsyncTask(context, cb, EntryApiAction.PAUSE_ACTION)
                    .execute(String.format("%sentries/current/pause.json?api_key=%s", dockr.baseUrl, dockr.getCurrentApiKey()));
        }
    }

    public void update(Context context, AsyncTaskCompleteListener<String> cb) {
        MinuteDockr dockr = MinuteDockr.getInstance(context);
        new HttpAsyncTask(context, cb, EntryApiAction.UPDATE_ACTION)
                .execute(String.format("%sentries/%s.json?api_key=%s", dockr.baseUrl, externalId, dockr.getCurrentApiKey()));
    }

    private class HttpAsyncTask extends ApiTask {
        protected EntryApiAction action;

        public HttpAsyncTask(Context context, AsyncTaskCompleteListener<String> cb, EntryApiAction action) {
            super(context, cb);
            this.action = action;
        }

        @Override
        protected String doInBackground(String... urls) {
            JSONObject params = null;
            if (action == EntryApiAction.UPDATE_ACTION) {
                params = toJSONObject();
                return ApiTask.put(urls[0], toParams());
            }
            else {
                return ApiTask.post(urls[0], params);
            }
        }
        @Override
        protected void onPostExecute(String result) {
            callback.onTaskComplete(result);
        }
    }
}
