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
import java.util.Arrays;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class EntryFormFragment extends Fragment {
    public static final String TAG = CurrentEntryActivity.class.getSimpleName();
    protected Entry currentEntry;
    protected Contact currentContact;
    protected Project currentProject;
    protected ArrayList<Task> currentTasks;
    protected TextView contact;
    protected TextView project;
    protected TextView task;
    protected TextView description;
    protected ArrayList<Contact> contacts;
    protected ArrayList<Project> projects;
    protected ArrayList<Task> tasks;

    public EntryFormFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry_form, container, false);

        contact = (TextView) rootView.findViewById(R.id.entry_form_contact);
        project = (TextView) rootView.findViewById(R.id.entry_form_project);
        task = (TextView) rootView.findViewById(R.id.entry_form_tasks);
        description = (TextView) rootView.findViewById(R.id.entry_form_description);

        return rootView;
    }

    public void setCurrentEntry(Entry entry) {
        currentEntry = entry;
        description.setText(entry.description);
        getCurrentContact();
        getCurrentProject();
        getCurrentTasks();
    }

    public void getCurrentContact() {
        if (currentEntry.contactId < 0) {
            contact.setText("");
            return;
        }
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
        if (currentEntry.projectId < 0) {
            project.setText("");
            return;
        }
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

    public void getCurrentTasks() {
        if (currentEntry.taskIds.length < 1) {
            task.setText("");
            return;
        }
        ApiTask apiTask = new ApiTask(getActivity(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    tasks = new ArrayList<Task>();
                    currentTasks = new ArrayList<Task>();
                    JSONArray jsonTasks = new JSONArray(result);
                    for (int i=0; i<jsonTasks.length(); i++) {
                        Task t = Task.fromJSONObject(jsonTasks.getJSONObject(i));
                        tasks.add(t);
                        if (entryHasTask(currentEntry, t)) {
                            currentTasks.add(t);
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    String delim = "";
                    for (int i=0; i<currentTasks.size(); i++) {
                        sb.append(delim).append(String.format("#%s", currentTasks.get(i).shortCode));
                        delim = ", ";
                    }
                    task.setText(sb.toString());
                }
                catch (JSONException e) {
                    Log.e(TAG, "JSONException caught: ", e);
                }
                catch (NullPointerException e) {
                    Log.e(TAG, "Null pointer exception caught: ", e);
                }
            }
        });
        apiTask.execute(MinuteDockr.getInstance(getActivity()).getTasksUrl());
    }

    private boolean entryHasTask(Entry entry, Task task) {
        for (int i=0; i<entry.taskIds.length; i++) {
            if (entry.taskIds[i] == task.externalId)
                return true;
        }
        return false;
    }
}
