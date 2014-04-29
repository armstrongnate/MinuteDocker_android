package com.example.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nate on 4/18/14.
 */
public class EntryAdapter extends ArrayAdapter<EntryRow> {
  protected ArrayList<EntryRow> rows;
  private ViewHolder viewHolder;

  private class ViewHolder {
    TextView contact;
    TextView project;
    TextView tasks;
    TextView duration;
    TextView description;
  }

  public EntryAdapter(Context context, int layoutResourceId, ArrayList<EntryRow> data) {
    super(context, layoutResourceId, data);
    rows = data;
    viewHolder = new ViewHolder();
  }

  @Override
  public View getView(int i, View convertView, ViewGroup viewGroup) {
    View v = convertView;
    EntryRow entryRow = rows.get(i);
    if (v == null) {
      if (entryRow != null) {
        if (!entryRow.isHeader) {
          LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          v = inflater.inflate(R.layout.entry_row, null);
          buildEntryRowView(v, entryRow);
        }
      }
    }
    else {
      buildEntryRowView(v, entryRow);
    }
    return v;
  }

  private void buildEntryRowView(View v, EntryRow entryRow) {
    if (v != null) {
      // contact
      viewHolder.contact = (TextView) v.findViewById(R.id.entry_row_contact);
      if (entryRow.contact != null) {
        viewHolder.contact.setText(String.format("@%s", entryRow.contact));
        viewHolder.contact.setVisibility(View.VISIBLE);
      } else {
        viewHolder.contact.setVisibility(View.GONE);
      }

      // project
      viewHolder.project = (TextView) v.findViewById(R.id.entry_row_project);
      if (entryRow.project != null) {
        viewHolder.project.setText(String.format("#%s", entryRow.project));
        viewHolder.project.setVisibility(View.VISIBLE);
      } else {
        viewHolder.project.setVisibility(View.GONE);
      }

      // tasks
      viewHolder.tasks = (TextView) v.findViewById(R.id.entry_row_tasks);
      if (entryRow.tasks.length > 0) {
        viewHolder.tasks.setText(String.format("Tasks: %s", buildTasksString(entryRow.tasks)));
        viewHolder.tasks.setVisibility(View.VISIBLE);
      } else {
        viewHolder.tasks.setVisibility(View.GONE);
      }

      // description
      viewHolder.description = (TextView) v.findViewById(R.id.entry_row_description);
      if (entryRow.description != null) {
        viewHolder.description.setText(entryRow.description);
        viewHolder.description.setVisibility(View.VISIBLE);
      } else {
        viewHolder.description.setVisibility(View.GONE);
      }

      // duration
      viewHolder.duration = (TextView) v.findViewById(R.id.entry_row_duration);
      int hours = (int) Math.floor(entryRow.duration / 3600);
      int minutes = (int) Math.floor(entryRow.duration / 60) % 60;
      viewHolder.duration.setText(String.format("%02d:%02d", hours, minutes));
    }
  }

  private String buildTasksString(String[] tasks) {
    StringBuilder sb = new StringBuilder();
    String delim = "";
    for (int j=0; j<tasks.length; j++) {
      sb.append(delim).append(String.format("#%s", tasks[j]));
      delim = ", ";
    }
    return sb.toString();
  }
}
