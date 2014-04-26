package com.example.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nate on 4/3/14.
 */

interface TasksDialogListener {
  public void onItemsConfirmedSelected(ArrayList<Task> tasks);
}

public class TasksDialog extends DialogFragment {
  protected ListView choices;
  protected TextView title;
  protected TasksDialogListener listener;
  protected ArrayList<Task> tasks;

  public TasksDialog() {
    // Required empty public constructor
  }

  public TasksDialog(TasksDialogListener tasksDialogListener) {
    listener = tasksDialogListener;
    tasks = new ArrayList<Task>();
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View v = inflater.inflate(R.layout.fragment_multiple_choice_dialog, null);
    choices = (ListView) v.findViewById(R.id.multiple_choice_dialog_list);
    TasksAdapter adapter = new TasksAdapter(getActivity(), R.layout.task_list_item, tasks);
    choices.setAdapter(adapter);
    title = (TextView) v.findViewById(R.id.multiple_choice_dialog_title);
    title.setText("Select Tasks");
    builder.setView(v);
    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        listener.onItemsConfirmedSelected(tasks);
      }
    });
    return builder.create();
  }
}
