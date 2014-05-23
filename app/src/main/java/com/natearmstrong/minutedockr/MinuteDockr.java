package com.natearmstrong.minutedockr;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.natearmstrong.minutedockr.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by nate on 3/20/14.
 */
public class MinuteDockr {
  private static final String PREFS_FILE = "minute_dockr";
  public static final String CURRENT_ACCOUNT_ID_PREFS_KEY = "current_account_id";
  public static final String API_KEY_PREFS_KEY = "api_key";
  public static final String CURRENT_USER_ID_PREFS_KEY = "user_id";
  public static final String USERNAME_PREFS_KEY = "username";
  public static final String PASSWORD_PREFS_KEY = "password";
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
  public HashMap<Integer, Contact> contacts;
  public HashMap<Integer, Project> projects;
  public HashMap<Integer, Task> tasks;
  public HashMap<Integer, Entry> todayEntries;
  public HashMap<Integer, Entry> weekEntries;
  public ApiTask contactsApiTask;
  public ApiTask projectsApiTask;
  public ApiTask tasksApiTask;
  public Entry currentEntry;
  public double todayTotal;
  public double weekTotal;

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
    return String.format("%s%s", baseUrl, currentEntryPath);
  }

  public String getCurrentAccountUrl() {
    return String.format("%s%s", baseUrl, currentAccountPath);
  }

  public String getContactsUrl() {
    return String.format("%s%s", baseUrl, contactsPath);
  }

  public String getProjectsUrl() {
    return String.format("%s%s", baseUrl, projectsPath);
  }

  public String getTasksUrl() {
    return String.format("%s%s", baseUrl, tasksPath);
  }

  public String getEntriesUrl(String from, String to) {
    if (from == null && to == null) {
      return String.format("%s%s?users=%d", baseUrl, entriesPath, getCurrentUserId());
    }
    else {
      return String.format("%s%s?users=%d&from=%s&to=%s", baseUrl, entriesPath, getCurrentUserId(), from, to);
    }
  }

  public String getCurrentApiKey() {
    return sharedPreferences.getString(API_KEY_PREFS_KEY, "");
  }

  public int getCurrentUserId() {
    return sharedPreferences.getInt(CURRENT_USER_ID_PREFS_KEY, 0);
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
    contactsApiTask = new ApiTask(context, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        try {
          Log.i(TAG, "result is: " + result);
          contacts = new HashMap<Integer, Contact>();
          JSONArray jsonContacts = new JSONArray(result);
          for (int i = 0; i < jsonContacts.length(); i++) {
            Contact contact = Contact.fromJSONObject(jsonContacts.getJSONObject(i));
            contacts.put(contact.externalId, contact);
          }
          listener.onTaskComplete(contacts);
        } catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        } catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
      }
    });
    contactsApiTask.execute(getContactsUrl());
  }

  public void getProjectsAsync(final AsyncTaskCompleteListener<HashMap<Integer, Project>> listener) {
    if (projects != null) {
      listener.onTaskComplete(projects);
      return;
    }
    projectsApiTask = new ApiTask(context, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        try {
          projects = new HashMap<Integer, Project>();
          JSONArray jsonProjects = new JSONArray(result);
          for (int i = 0; i < jsonProjects.length(); i++) {
            Project project = Project.fromJSONObject(jsonProjects.getJSONObject(i));
            projects.put(project.externalId, project);
          }
          listener.onTaskComplete(projects);
        } catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        } catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
      }
    });
    if (projectsApiTask.getStatus() != AsyncTask.Status.RUNNING) {
      projectsApiTask.execute(getProjectsUrl());
    }
  }

  public void getTasksAsync(final AsyncTaskCompleteListener<HashMap<Integer, Task>> listener) {
    if (tasks != null) {
      listener.onTaskComplete(tasks);
      return;
    }
    tasksApiTask = new ApiTask(context, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        try {
          tasks = new HashMap<Integer, Task>();
          JSONArray jsonTasks = new JSONArray(result);
          for (int i = 0; i < jsonTasks.length(); i++) {
            Task task = Task.fromJSONObject(jsonTasks.getJSONObject(i));
            tasks.put(task.externalId, task);
          }
          listener.onTaskComplete(tasks);
        } catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        } catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
      }
    });
    if (tasksApiTask.getStatus() != AsyncTask.Status.RUNNING) {
      tasksApiTask.execute(getTasksUrl());
    }
  }

  public void getCurrentEntry(final AsyncTaskCompleteListener<Entry> listener) {
    ApiTask apiTask = new ApiTask(context, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        try {
          JSONObject jsonEntry = new JSONObject(result);
          currentEntry = Entry.fromJSONObject(jsonEntry);
          sharedPreferences.edit().putInt(CURRENT_USER_ID_PREFS_KEY, currentEntry.userId).commit();
          listener.onTaskComplete(currentEntry);
        }
        catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        }
        catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
      }
    });
    apiTask.execute(getCurrentEntryUrl());
  }

  public void fetchEntries(final int page, final AsyncTaskCompleteListener<HashMap<Integer, Entry>> listener) {
    switch (page) {
      case 0: {
        todayEntries = new HashMap<Integer, Entry>();
        break;
      }
      case 1: {
        weekEntries = new HashMap<Integer, Entry>();
        break;
      }
    }
    ApiTask entriesApiTask = new ApiTask(context, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        double durationTotal = 0;
        HashMap<Integer, Entry> entries = new HashMap<Integer, Entry>();
        try {
          JSONArray jsonEntries = new JSONArray(result);
          for (int i = 0; i < jsonEntries.length(); i++) {
            durationTotal += jsonEntries.getJSONObject(i).getInt("duration");
            if (page != 1) {
              Entry entry = Entry.fromJSONObject(jsonEntries.getJSONObject(i));
              entries.put(entry.externalId, entry);
            }
          }
          switch (page) {
            case 0: {
              todayEntries = new HashMap<Integer, Entry>(entries);
              todayTotal = durationTotal;
              listener.onTaskComplete(todayEntries);
              break;
            }
            case 1: {
              weekTotal = durationTotal;
              if (listener != null) {
                listener.onTaskComplete(weekEntries);
              }
              break;
            }
          }
        }
        catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        }
        catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
      }
    });
    String from = null;
    String to = null;
    SimpleDateFormat sdf = new SimpleDateFormat("dd%20MMM%20yyy");
    switch (page) {
      case 0: { // today
        Calendar today = Calendar.getInstance();
        from = sdf.format(today.getTime());
        to = from;
        break;
      }
      case 1: { // this week - api default
        break;
      }
      case 2: { // this month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        from = sdf.format(calendar.getTime());
        to = sdf.format(Calendar.getInstance().getTime());
        break;
      }
    }
    entriesApiTask.execute(getEntriesUrl(from, to));
  }
}
