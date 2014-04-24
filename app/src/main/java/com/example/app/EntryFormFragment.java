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

  private class ViewHolder {
    public TextView contact;
    public TextView project;
    public TextView task;
    public EditText description;
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
    viewHolder.description = (EditText) rootView.findViewById(R.id.entry_form_description);

    viewHolder.description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus)
          setCurrentDescription(viewHolder.description.getText().toString());
      }
    });
    viewHolder.description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          setCurrentDescription(viewHolder.description.getText().toString());
        }
        return false;
      }
    });

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

    tasksDialog = new TasksDialog(new TasksDialogListener() {
      @Override
      public void onItemsConfirmedSelected(ArrayList<Task> tasks) {
        ArrayList<Task> selectedTasks = new ArrayList<Task>();
        for (Task task : tasks) {
          if (task.isChecked)
            selectedTasks.add(task);
        }
        setCurrentTasks(selectedTasks);
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
    setCurrentDescription(entry.description);
    getCurrentContact();
    getCurrentProject();
    getCurrentTasks();
  }

  public void getCurrentContact() {
    if (currentEntry.contactId < 0) {
      setCurrentContact(null);
    }
    MinuteDockr.getInstance(getActivity()).getContactsAsync(new AsyncTaskCompleteListener<HashMap<Integer, Contact>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Contact> results) {
        contacts = new ArrayList<Contact>();
        for (HashMap.Entry<Integer, Contact> entry : results.entrySet()) {
          Contact contact = entry.getValue();
          contacts.add(contact);
          if (contact.externalId == currentEntry.contactId) {
            setCurrentContact(contact);
          }
        }
        buildContactsDialog();
      }
    });
  }

  public void getCurrentProject() {
    if (currentEntry.projectId < 0) {
      setCurrentProject(null);
    }
    MinuteDockr.getInstance(getActivity()).getProjectsAsync(new AsyncTaskCompleteListener<HashMap<Integer, Project>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Project> results) {
        projects = new ArrayList<Project>();
        for (HashMap.Entry<Integer, Project> entry : results.entrySet()) {
          Project project = entry.getValue();
          projects.add(project);
          if (project.externalId == currentEntry.projectId) {
            setCurrentProject(project);
          }
        }
      }
    });
  }

  public void getCurrentTasks() {
    if (currentEntry.taskIds.length < 1) {
      setCurrentTasks(null);
    }
    MinuteDockr.getInstance(getActivity()).getTasksAsync(new AsyncTaskCompleteListener<HashMap<Integer, Task>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Task> results) {
        tasks = new ArrayList<Task>();
        currentTasks = new ArrayList<Task>();
        for (HashMap.Entry<Integer, Task> entry : results.entrySet()) {
          Task task = entry.getValue();
          tasks.add(task);
          if (entryHasTask(currentEntry, task)) {
            task.isChecked = true;
            currentTasks.add(task);
          }
        }
        setCurrentTasks(currentTasks);
      }
    });
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

  public void setCurrentContact(Contact contact) {
    currentContact = contact;
    if (contact != null) {
      currentEntry.contactId = contact.externalId;
      viewHolder.contact.setText(String.format("@%s", contact.shortCode));
      if (currentEntry.projectId > 0 && currentProject == null)
        return;
      if (currentProject == null || currentProject.contactId != contact.externalId) {
        setCurrentProject(null);
      }
      else
        updateCurrentEntry();
    }
    else {
      currentEntry.contactId = -1;
      viewHolder.contact.setText("");
      setCurrentProject(null);
    }
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
    updateCurrentEntry();
  }

  public void setCurrentDescription(String description) {
    viewHolder.description.setText(description);
    currentEntry.description = description;
    updateCurrentEntry();
  }

  public Entry getCurrentEntry() {
    currentEntry.description = viewHolder.description.getText().toString();
    return currentEntry;
  }
}
