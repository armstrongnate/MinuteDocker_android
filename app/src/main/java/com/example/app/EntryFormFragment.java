package com.example.app;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

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
    protected ArrayList<Contact> contacts;
    protected ArrayList<Project> projects;
    protected ArrayList<Task> tasks;
    protected ContactsDialog contactsDialog;
    protected ProjectsDialog projectsDialog;
    protected Dialog tasksDialog;
    private ViewHolder viewHolder;

    private class ViewHolder {
        public TextView contact;
        public TextView project;
        public TextView task;
        public TextView description;
    }

    public EntryFormFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry_form, container, false);
        viewHolder = new ViewHolder();

        viewHolder.contact = (TextView) rootView.findViewById(R.id.entry_form_contact);
        viewHolder.project = (TextView) rootView.findViewById(R.id.entry_form_project);
        viewHolder.task = (TextView) rootView.findViewById(R.id.entry_form_tasks);
        viewHolder.description = (TextView) rootView.findViewById(R.id.entry_form_description);
        contactsDialog = new ContactsDialog(new SingleChoiceDialogListener() {
            @Override
            public void onItemClick(Object choice) {
                if (choice != null) {
                    Contact contact = (Contact) choice;
                    setCurrentContact(contact);
                }
                else
                    setCurrentContact(null);
                contactsDialog.dismiss();
            }
        });

        projectsDialog = new ProjectsDialog(new SingleChoiceDialogListener() {
            @Override
            public void onItemClick(Object choice) {
                if (choice != null) {
                    Project project = (Project) choice;
                    setCurrentProject(project);
                }
                else
                    setCurrentProject(null);
                projectsDialog.dismiss();
            }
        });

        viewHolder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactsDialog.show(getActivity().getFragmentManager(), "ContactsDialog");
            }
        });

        viewHolder.project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                projectsDialog.show(getActivity().getFragmentManager(), "ProjectsDialog");
            }
        });

        viewHolder.task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tasksDialog.show();
            }
        });

        return rootView;
    }

    public void setCurrentEntry(Entry entry) {
        currentEntry = entry;
        viewHolder.description.setText(entry.description);
        getCurrentContact();
        getCurrentProject();
        getCurrentTasks();
    }

    public void getCurrentContact() {
        if (currentEntry.contactId < 0) {
            setCurrentContact(null);
            return;
        }
        ApiTask apiTask = new ApiTask(getActivity(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    contacts = new ArrayList<Contact>();
                    JSONArray jsonContacts = new JSONArray(result);
                    for (int i=0; i<jsonContacts.length(); i++) {
                        Contact contact = Contact.fromJSONObject(jsonContacts.getJSONObject(i));
                        contacts.add(contact);
                        if (contact.externalId == currentEntry.contactId) {
                            setCurrentContact(contact);
                        }
                    }
                }
                catch (JSONException e) {
                    Log.e(TAG, "JSONException caught: ", e);
                }
                catch (NullPointerException e) {
                    Log.e(TAG, "Null pointer exception caught: ", e);
                }
                buildContactsDialog();
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
                    boolean found = false;
                    for (int i=0; i<jsonProjects.length(); i++) {
                        Project project = Project.fromJSONObject(jsonProjects.getJSONObject(i));
                        projects.add(project);
                        if (project.externalId == currentEntry.projectId) {
                            setCurrentProject(project);
                            found = true;
                        }
                    }
                    if (!found)
                        setCurrentProject(null);
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
            viewHolder.task.setText("");
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
                    setCurrentTasks(currentTasks);
                }
                catch (JSONException e) {
                    Log.e(TAG, "JSONException caught: ", e);
                }
                catch (NullPointerException e) {
                    Log.e(TAG, "Null pointer exception caught: ", e);
                }
                buildTasksDialog();
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

    private void buildContactsDialog() {
        contactsDialog.contacts = contacts;
    }

    private void buildProjectsDialog() {
        ArrayList<Project> currentContactProjects = new ArrayList<Project>();
        for (Project project : projects) {
            if (project.contactId == currentEntry.contactId)
                currentContactProjects.add(project);
        }
        projectsDialog.projects = currentContactProjects;
    }

    private void buildTasksDialog() {
        String shortCodes[] = new String[tasks.size()];
        for (int i=0; i<tasks.size(); i++) {
            if (tasks.get(i).shortCode != null)
                shortCodes[i] = tasks.get(i).shortCode;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Tasks")
                .setItems(shortCodes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        tasksDialog = builder.create();
    }

    private void updateCurrentEntry() {
        currentEntry.update(getActivity(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                Toast.makeText(getActivity(), "Updated!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setCurrentContact(Contact contact) {
        currentContact = contact;
        if (contact != null) {
            currentEntry.contactId = contact.externalId;
            viewHolder.contact.setText(String.format("@%s", contact.shortCode));
            if (currentProject == null || currentProject.contactId != contact.externalId) {
                setCurrentProject(null);
            }
        }
        else {
            currentEntry.contactId = -1;
            viewHolder.contact.setText("");
            setCurrentProject(null);
        }
        updateCurrentEntry();
    }

    public void setCurrentProject(Project project) {
        currentProject = project;
        if (project != null) {
            currentEntry.projectId = project.externalId;
            viewHolder.project.setText(String.format("#%s", project.shortCode));
        }
        else {
            currentEntry.projectId = -1;
            viewHolder.project.setText("");
        }
        if (projects != null)
            buildProjectsDialog();
        updateCurrentEntry();
    }

    public void setCurrentTasks(ArrayList<Task> tasks) {
        currentTasks = tasks;
        if (tasks != null) {
            StringBuilder sb = new StringBuilder();
            String delim = "";
            for (int i=0; i<currentTasks.size(); i++) {
                sb.append(delim).append(String.format("#%s", currentTasks.get(i).shortCode));
                delim = ", ";
            }
            viewHolder.task.setText(sb.toString());
        }
        else
            viewHolder.task.setText("");
        updateCurrentEntry();
    }
}
