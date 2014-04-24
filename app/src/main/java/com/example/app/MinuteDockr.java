package com.example.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nate on 3/20/14.
 */
public class MinuteDockr {
  private static final String PREFS_FILE = "minute_dockr";
  public static final String CURRENT_ACCOUNT_ID_PREFS_KEY = "current_account_id";
  public static final String API_KEY_PREFS_KEY = "api_key";
  private static final String MY_API_KEY = "0e3ec0f390e9b7aff763d64d8cea6c50";
  private static final String TAG = "MinuteDockr";

  public static String baseUrl = "https://minutedock.com/api/v1/";
  public static String currentEntryPath = "entries/current.json";
  public static String currentAccountPath = "accounts/current.json";
  public static String contactsPath = "contacts.json";
  public static String projectsPath = "projects.json";
  public static String tasksPath = "tasks.json";
  public static String entriesPath = "entries.json";

  private static MinuteDockr instance = null;
  public SharedPreferences sharedPreferences;
  public Context context;
  private HashMap<Integer, Contact> contacts;
  private HashMap<Integer, Project> projects;
  private HashMap<Integer, Task> tasks;

  private MinuteDockr(Context appContext) {
    context = appContext;
    sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
  }

  public static MinuteDockr getInstance(Context context) {
    if (instance == null) {
      instance = new MinuteDockr(context);
    }
    return instance;
  }

  public String getCurrentEntryUrl() {
    return String.format("%s%s?api_key=%s", baseUrl, currentEntryPath, getCurrentApiKey());
  }

  public String getCurrentAccountUrl() {
    return String.format("%s%s?api_key=%s", baseUrl, currentAccountPath, getCurrentApiKey());
  }

  public String getCurrentAccountUrl(String apiKey) {
    return String.format("%s%s?api_key=%s", baseUrl, currentAccountPath, apiKey);
  }

  public String getContactsUrl() {
    return String.format("%s%s?api_key=%s", baseUrl, contactsPath, getCurrentApiKey());
  }

  public String getProjectsUrl() {
    return String.format("%s%s?api_key=%s", baseUrl, projectsPath, getCurrentApiKey());
  }

  public String getTasksUrl() {
    return String.format("%s%s?api_key=%s", baseUrl, tasksPath, getCurrentApiKey());
  }

  public String getEntriesUrl(int userId) {
    return String.format("%s%s?users=%d&api_key=%s", baseUrl, entriesPath, userId, getCurrentApiKey());
  }

  public String getCurrentApiKey() {
    return sharedPreferences.getString(API_KEY_PREFS_KEY, "");
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

  public void getContactsAsync(final AsyncTaskCompleteListener<HashMap<Integer, Contact>> listener) {
    if (contacts != null) {
      listener.onTaskComplete(contacts);
      return;
    }
    ApiTask apiTask = new ApiTask(context, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        try {
          contacts = new HashMap<Integer, Contact>();
          JSONArray jsonContacts = new JSONArray(result);
          for (int i=0; i<jsonContacts.length(); i++) {
            Contact contact = Contact.fromJSONObject(jsonContacts.getJSONObject(i));
            contacts.put(contact.externalId, contact);
          }
        }
        catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        }
        catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
        listener.onTaskComplete(contacts);
      }
    });
    apiTask.execute(getContactsUrl());
  }

  public void getProjectsAsync(final AsyncTaskCompleteListener<HashMap<Integer, Project>> listener) {
    if (projects != null) {
      listener.onTaskComplete(projects);
      return;
    }
    ApiTask apiTask = new ApiTask(context, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        try {
          projects = new HashMap<Integer, Project>();
          JSONArray jsonProjects = new JSONArray(result);
          for (int i=0; i<jsonProjects.length(); i++) {
            Project project = Project.fromJSONObject(jsonProjects.getJSONObject(i));
            projects.put(project.externalId, project);
          }
        }
        catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        }
        catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
        listener.onTaskComplete(projects);
      }
    });
    apiTask.execute(getProjectsUrl());
  }

  public void getTasksAsync(final AsyncTaskCompleteListener<HashMap<Integer, Task>> listener) {
    if (tasks != null) {
      listener.onTaskComplete(tasks);
      return;
    }
    ApiTask apiTask = new ApiTask(context, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        try {
          tasks = new HashMap<Integer, Task>();
          JSONArray jsonTasks = new JSONArray(result);
          for (int i=0; i<jsonTasks.length(); i++) {
            Task task = Task.fromJSONObject(jsonTasks.getJSONObject(i));
            tasks.put(task.externalId, task);
          }
        }
        catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        }
        catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
        listener.onTaskComplete(tasks);
      }
    });
    apiTask.execute(getTasksUrl());
  }
}
