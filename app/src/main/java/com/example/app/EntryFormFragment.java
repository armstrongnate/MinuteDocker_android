package com.example.app;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class EntryFormFragment extends Fragment {
    protected Entry currentEntry;
    protected TextView contact;
    protected TextView project;
    protected TextView tasks;
    protected TextView description;

    public EntryFormFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry_form, container, false);

        contact = (TextView) rootView.findViewById(R.id.entry_form_contact);
        project = (TextView) rootView.findViewById(R.id.entry_form_project);
        tasks = (TextView) rootView.findViewById(R.id.entry_form_tasks);
        description = (TextView) rootView.findViewById(R.id.entry_form_description);

        return rootView;
    }

    public void setCurrentEntry(Entry entry) {
        currentEntry = entry;
        description.setText(entry.description);
    }


}
