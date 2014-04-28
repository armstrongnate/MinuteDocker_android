package com.example.app;

import android.app.Fragment;
import android.os.AsyncTask;
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
  private ViewHolder viewHolder;
  private MinuteDockr app;
  private EntryAdapter adapter;

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
    app = MinuteDockr.getInstance(getActivity());
    page = getArguments().getInt("someInt", 0);
    viewHolder = new ViewHolder();
    entryRows = new ArrayList<EntryRow>();
    adapter = new EntryAdapter(getActivity(), R.layout.entry_row, entryRows);
    buildEntryRows();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_entries, container, false);
    viewHolder.entriesList = (ListView)view.findViewById(R.id.entries_list);
    viewHolder.total = (TextView)view.findViewById(R.id.entries_total_hours);
    viewHolder.entriesList.setAdapter(adapter);
    setDurationTotal();
    return view;
  }

  private void setDurationTotal() {
    double duration;
    switch (page) {
      case 0: {
        duration = app.todayTotal;
        break;
      }
      case 1: {
        duration = app.weekTotal;
        break;
      }
      default: {
        duration = 0;
      }
    }
    int hours = (int)Math.floor(duration / 3600);
    int minutes = (int)Math.floor(duration / 60) % 60;
    viewHolder.total.setText(String.format("%d hours %d minutes", hours, minutes));
  }

  private void buildEntryRows() {
    HashMap<Integer, Entry> entries;
    switch (page) {
      case 0: {
        entries = app.todayEntries;
        break;
      }
      case 1: {
        entries = app.weekEntries;
        break;
      }
      default: {
        entries = app.todayEntries;
      }
    }
    for (HashMap.Entry<Integer, Entry> e : entries.entrySet()) {
      EntryRow entryRow = new EntryRow();
      Entry entry = e.getValue();

      // contact
      Contact contact = app.contacts.get(entry.contactId);
      if (contact != null) {
        entryRow.contact = contact.shortCode;
      }

      // project
      Project project = app.projects.get(entry.projectId);
      if (project != null) {
        entryRow.project = project.shortCode;
      }

      // tasks
      entryRow.tasks = new String[entry.taskIds.length];
      for (int j = 0; j < entry.taskIds.length; j++) {
        Task task = app.tasks.get(entry.taskIds[j]);
        if (task != null) {
          entryRow.tasks[j] = task.shortCode;
        }
      }

      entryRow.duration = entry.duration;
      entryRow.description = entry.description;
      entryRows.add(entryRow);
      adapter.notifyDataSetChanged();
    }
  }
}
