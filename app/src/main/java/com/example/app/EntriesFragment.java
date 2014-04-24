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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nate on 4/18/14.
 */
public class EntriesFragment extends android.support.v4.app.Fragment {
  private static String TAG = "ENTRIES FRAGMENT";
  private int page;
  private ArrayList<EntryRow> entryRows;
  private HashMap<Integer, Contact> contacts;

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
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_entries, container, false);
    fetchEntries(view);
    return view;
  }

  private void fetchEntries(View view) {
    final ListView entriesList = (ListView)view.findViewById(R.id.entries_list);

    ApiTask apiTask = new ApiTask(getActivity(), new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        try {
          // get contacts
          MinuteDockr.getInstance(getActivity()).getContactsAsync(new AsyncTaskCompleteListener<HashMap<Integer, Contact>>() {
            @Override
            public void onTaskComplete(HashMap<Integer, Contact> result) {
              contacts = result;
            }
          });

          // build list of entry rows
          ArrayList<EntryRow> entryRows = new ArrayList<EntryRow>();
          JSONArray jsonEntries = new JSONArray(result);
          for (int i=0; i<jsonEntries.length(); i++) {
            EntryRow entryRow = EntryRow.fromJSON(jsonEntries.getJSONObject(i), contacts);
            entryRows.add(entryRow);
          }
          entriesList.setAdapter(new EntryAdapter(getActivity(), R.layout.entry_row, entryRows));
        }
        catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        }
        catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
      }
    });
    apiTask.execute(MinuteDockr.getInstance(getActivity()).getEntriesUrl(18545));
  }
}
