package com.example.app;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


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
  protected TasksDialog tasksDialog;
  private ViewHolder viewHolder;
  private MinuteDockr app;

  private class ViewHolder {
    public TextView contact;
    public TextView project;
    public TextView task;
    public EditText description;
  }

  public EntryFormFragment() {
    app = MinuteDockr.getInstance(getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_entry_form, container, false);
    contacts = new ArrayList<Contact>();
    projects = new ArrayList<Project>();
    tasks = new ArrayList<Task>();
    viewHolder = new ViewHolder();

    viewHolder.contact = (TextView) rootView.findViewById(R.id.entry_form_contact);
    viewHolder.project = (TextView) rootView.findViewById(R.id.entry_form_project);
    viewHolder.task = (TextView) rootView.findViewById(R.id.entry_form_tasks);
    viewHolder.description = (EditText) rootView.findViewById(R.id.entry_form_description);

    viewHolder.description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus)
          setCurrentDescription(viewHolder.description.getText().toString(), true);
      }
    });
    viewHolder.description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          setCurrentDescription(viewHolder.description.getText().toString(), true);
        }
        return false;
      }
    });

    contactsDialog = new ContactsDialog(new SingleChoiceDialogListener() {
      @Override
      public void onItemClick(Object choice) {
        if (choice != null) {
          Contact contact = (Contact) choice;
          setCurrentContact(contact, true);
        }
        else
          setCurrentContact(null, true);
        contactsDialog.dismiss();
      }
    });

    projectsDialog = new ProjectsDialog(new SingleChoiceDialogListener() {
      @Override
      public void onItemClick(Object choice) {
        if (choice != null) {
          Project project = (Project) choice;
          setCurrentProject(project, true);
        }
        else
          setCurrentProject(null, true);
        projectsDialog.dismiss();
      }
    });

    tasksDialog = new TasksDialog(new TasksDialogListener() {
      @Override
      public void onItemsConfirmedSelected(ArrayList<Task> tasks) {
        ArrayList<Task> selectedTasks = new ArrayList<Task>();
        for (Task task : tasks) {
          if (task.isChecked)
            selectedTasks.add(task);
        }
        setCurrentTasks(selectedTasks, true);
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
        buildTasksDialog();
        tasksDialog.show(getActivity().getFragmentManager(), "TasksDialog");
      }
    });

    return rootView;
  }

  public void setCurrentEntry(Entry entry) {
    currentEntry = entry;
    setCurrentDescription(entry.description, false);
    getCurrentContact();
    getCurrentProject();
    getCurrentTasks();
  }

  public void getCurrentContact() {
    if (currentEntry.contactId < 0) {
      setCurrentContact(null, false);
    }
    contacts = new ArrayList<Contact>();
    for (HashMap.Entry<Integer, Contact> entry : app.contacts.entrySet()) {
      Contact contact = entry.getValue();
      contacts.add(contact);
      if (contact.externalId == currentEntry.contactId) {
        setCurrentContact(contact, false);
      }
    }
    buildContactsDialog();
  }

  public void getCurrentProject() {
    if (currentEntry.projectId < 0) {
      setCurrentProject(null, false);
    }
    projects = new ArrayList<Project>();
    for (HashMap.Entry<Integer, Project> entry : app.projects.entrySet()) {
      Project project = entry.getValue();
      projects.add(project);
      if (project.externalId == currentEntry.projectId) {
        setCurrentProject(project, false);
      }
    }
  }

  public void getCurrentTasks() {
    if (currentEntry.taskIds.length < 1) {
      setCurrentTasks(null, false);
    }
    tasks = new ArrayList<Task>();
    currentTasks = new ArrayList<Task>();
    for (HashMap.Entry<Integer, Task> entry : app.tasks.entrySet()) {
      Task task = entry.getValue();
      tasks.add(task);
      if (entryHasTask(currentEntry, task)) {
        task.isChecked = true;
        currentTasks.add(task);
      }
    }
    setCurrentTasks(currentTasks, false);
  }

  private boolean entryHasTask(Entry entry, Task task) {
    for (int i=0; i<entry.taskIds.length; i++) {
      if (entry.taskIds[i] == task.externalId)
        return true;
    }
    return false;
  }

  private void buildContactsDialog() {
    Collections.sort(contacts, new Comparator<Contact>() {
      public int compare(Contact contact1, Contact contact2) {
        return contact1.shortCode.compareToIgnoreCase(contact2.shortCode);
      }
    });
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
    for (Task task : tasks) {
      task.isChecked = entryHasTask(currentEntry, task);
    }
    tasksDialog.tasks = tasks;
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

  public void setCurrentContact(Contact contact, boolean shouldUpdateCurrentEntry) {
    currentContact = contact;
    if (contact != null) {
      currentEntry.contactId = contact.externalId;
      viewHolder.contact.setText(String.format("@%s", contact.shortCode));
      if (currentEntry.projectId > 0 && currentProject == null)
        return;
      if (currentProject == null || currentProject.contactId != contact.externalId) {
        setCurrentProject(null, shouldUpdateCurrentEntry);
      }
      else
        if (shouldUpdateCurrentEntry) {
          updateCurrentEntry();
        }
    }
    else {
      currentEntry.contactId = -1;
      viewHolder.contact.setText("");
      setCurrentProject(null, shouldUpdateCurrentEntry);
    }
  }

  public void setCurrentProject(Project project, boolean shouldUpdateCurrentEntry) {
    currentProject = project;
    if (project != null) {
      currentEntry.projectId = project.externalId;
      viewHolder.project.setText(String.format("#%s", project.shortCode));
    }
    else {
      currentEntry.projectId = -1;
      viewHolder.project.setText("");
    }
    if (projects != null) {
      buildProjectsDialog();
    }
    if (shouldUpdateCurrentEntry) {
      updateCurrentEntry();
    }
  }

  public void setCurrentTasks(ArrayList<Task> tasks, boolean shouldUpdateCurrentEntry) {
    currentTasks = tasks;
    if (currentTasks != null)
      currentEntry.taskIds = new int[currentTasks.size()];
    if (tasks != null) {
      StringBuilder sb = new StringBuilder();
      String delim = "";
      for (int i=0; i<currentTasks.size(); i++) {
        currentEntry.taskIds[i] = currentTasks.get(i).externalId;
        sb.append(delim).append(String.format("#%s", currentTasks.get(i).shortCode));
        delim = ", ";
      }
      viewHolder.task.setText(sb.toString());
    }
    else
      viewHolder.task.setText("");
    if (shouldUpdateCurrentEntry) {
      updateCurrentEntry();
    }
  }

  public void setCurrentDescription(String description, boolean shouldUpdateCurrentEntry) {
    viewHolder.description.setText(description);
    currentEntry.description = description;
    if (shouldUpdateCurrentEntry) {
      updateCurrentEntry();
    }
  }

  public Entry getCurrentEntry() {
    currentEntry.description = viewHolder.description.getText().toString();
    return currentEntry;
  }
}
