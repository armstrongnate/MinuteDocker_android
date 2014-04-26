package com.example.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by nate on 4/18/14.
 */
public class EntriesFragment extends android.support.v4.app.Fragment {
  private static String TAG = "ENTRIES FRAGMENT";
  private int page;
  private ArrayList<EntryRow> entryRows;
  private HashMap<Integer, Contact> contacts;
  private HashMap<Integer, Project> projects;
  private HashMap<Integer, Task> tasks;
  private ViewHolder viewHolder;
  private double durationTotal;

  private class ViewHolder {
    ListView entriesList;
    TextView total;
  }

  public static EntriesFragment newInstance(int page) {
    EntriesFragment fragmentEntries = new EntriesFragment();
    Bundle args = new Bundle();
    args.putInt("someInt", page);
    fragmentEntries.setArguments(args);
    return fragmentEntries;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    page = getArguments().getInt("someInt", 0);
    durationTotal = 0;
    viewHolder = new ViewHolder();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_entries, container, false);
    fetchAssets();
    fetchEntries(view);
    return view;
  }

  private void fetchEntries(final View view) {
    viewHolder.entriesList = (ListView)view.findViewById(R.id.entries_list);
    viewHolder.total = (TextView)view.findViewById(R.id.entries_total_hours);
    setDurationTotal();
    if (entryRows != null) {
      viewHolder.entriesList.setAdapter(new EntryAdapter(getActivity(), R.layout.entry_row, entryRows));
    }
    else {
      ApiTask apiTask = new ApiTask(getActivity(), new AsyncTaskCompleteListener<String>() {
        @Override
        public void onTaskComplete(String result) {
          if (getActivity() != null) {
            entryRows = new ArrayList<EntryRow>();
            durationTotal = 0;
            durationTotal = buildEntryRowsFromJSON(result);
            setDurationTotal();
            viewHolder.entriesList.setAdapter(new EntryAdapter(getActivity(), R.layout.entry_row, entryRows));
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
      apiTask.execute(MinuteDockr.getInstance(getActivity()).getEntriesUrl(from, to));
    }
  }

  private void setDurationTotal() {
    int hours = (int)Math.floor(durationTotal / 3600);
    int minutes = (int)Math.floor(durationTotal / 60) % 60;
    viewHolder.total.setText(String.format("%d hours %d minutes", hours, minutes));
  }

  private double buildEntryRowsFromJSON(String jsonString) {
    try {
      JSONArray jsonEntries = new JSONArray(jsonString);
      for (int i = 0; i < jsonEntries.length(); i++) {
        Entry entry = Entry.fromJSONObject(jsonEntries.getJSONObject(i));
        EntryRow entryRow = new EntryRow();
        durationTotal += entry.duration;

        // contact
        Contact contact = contacts.get(entry.contactId);
        if (contact != null) {
          entryRow.contact = contact.shortCode;
        }

        // project
        Project project = projects.get(entry.projectId);
        if (project != null) {
          entryRow.project = project.shortCode;
        }

        // tasks
        entryRow.tasks = new String[entry.taskIds.length];
        for (int j = 0; j < entry.taskIds.length; j++) {
          Task task = tasks.get(entry.taskIds[j]);
          if (task != null) {
            entryRow.tasks[j] = task.shortCode;
          }
        }

        entryRow.duration = entry.duration;
        entryRow.description = entry.description;
        entryRows.add(entryRow);
      }
    }
    catch (JSONException e) {
      Log.e(TAG, "JSONException caught: ", e);
    }
    catch (NullPointerException e) {
      Log.e(TAG, "Null pointer exception caught: ", e);
    }

    return durationTotal;
  }

  private void fetchAssets() {
    // get contacts
    MinuteDockr.getInstance(getActivity()).getContactsAsync(new AsyncTaskCompleteListener<HashMap<Integer, Contact>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Contact> result) {
        contacts = result;
      }
    });

    // get projects
    MinuteDockr.getInstance(getActivity()).getProjectsAsync(new AsyncTaskCompleteListener<HashMap<Integer, Project>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Project> result) {
        projects = result;
      }
    });

    // get tasks
    MinuteDockr.getInstance(getActivity()).getTasksAsync(new AsyncTaskCompleteListener<HashMap<Integer, Task>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Task> result) {
        tasks = result;
      }
    });
  }
}
