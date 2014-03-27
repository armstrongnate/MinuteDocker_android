package com.example.app;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class EntryFormFragment extends Fragment {
    public static final String TAG = CurrentEntryActivity.class.getSimpleName();
    protected Entry currentEntry;
    protected Contact currentContact;
    protected Project currentProject;
    protected TextView contact;
    protected TextView project;
    protected TextView tasks;
    protected TextView description;
    protected ArrayList<Contact> contacts;
    protected ArrayList<Project> projects;

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
        getCurrentContact();
        getCurrentProject();
    }

    public void getCurrentContact() {
        ApiTask apiTask = new ApiTask(getActivity(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    contacts = new ArrayList<Contact>();
                    JSONArray jsonContacts = new JSONArray(result);
                    for (int i=0; i<jsonContacts.length(); i++) {
                        Contact c = Contact.fromJSONObject(jsonContacts.getJSONObject(i));
                        contacts.add(c);
                        if (c.externalId == currentEntry.contactId) {
                            currentContact = c;
                            contact.setText(String.format("@%s", c.shortCode));
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
        apiTask.execute(MinuteDockr.getInstance(getActivity()).getContactsUrl());
    }

    public void getCurrentProject() {
        ApiTask apiTask = new ApiTask(getActivity(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    projects = new ArrayList<Project>();
                    JSONArray jsonProjects = new JSONArray(result);
                    for (int i=0; i<jsonProjects.length(); i++) {
                        Project p = Project.fromJSONObject(jsonProjects.getJSONObject(i));
                        projects.add(p);
                        if (p.externalId == currentEntry.projectId) {
                            currentProject = p;
                            project.setText(String.format("#%s", p.shortCode));
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
        apiTask.execute(MinuteDockr.getInstance(getActivity()).getProjectsUrl());
    }


}
