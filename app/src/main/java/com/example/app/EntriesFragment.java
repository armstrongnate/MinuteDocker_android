package com.example.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nate on 4/18/14.
 */
public class EntriesFragment extends android.support.v4.app.Fragment {
  private int page;

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
    ListView entriesList = (ListView)view.findViewById(R.id.entries_list);
    ArrayList<EntryRow> entryRows = new ArrayList<EntryRow>();
    entryRows.add(new EntryRow());
    entriesList.setAdapter(new EntryAdapter(getActivity(), R.layout.entry_row, entryRows));
    return view;
  }
}
