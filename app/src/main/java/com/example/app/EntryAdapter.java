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
  public EntryAdapter(Context context, int layoutResourceId, ArrayList<EntryRow> data) {
    super(context, layoutResourceId, new ArrayList<EntryRow>(data));
    rows = new ArrayList<EntryRow>(data);
  }

  @Override
  public View getView(int i, View convertView, ViewGroup viewGroup) {
    View v = convertView;
    if (v == null) {
      EntryRow entryRow = rows.get(i);
      if (entryRow != null) {
        if (!entryRow.isHeader) {
          LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          v = inflater.inflate(R.layout.entry_row, null);
          if (v != null) {
            TextView contactTV = (TextView)v.findViewById(R.id.entry_row_contact);
            contactTV.setText(entryRow.contact);
          }
        }
      }
    }
    return v;
  }
}
