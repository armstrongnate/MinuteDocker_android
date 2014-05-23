package com.natearmstrong.minutedockr;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.natearmstrong.minutedockr.R;

import java.util.ArrayList;

/**
 * Created by nate on 4/2/14.
 */
public class ProjectsDialog extends SingleChoiceDialog {
  protected ArrayList<Project> projects;

  public ProjectsDialog(SingleChoiceDialogListener singleChoiceDialogListener) {
    listener = singleChoiceDialogListener;
    projects = new ArrayList<Project>();
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    ProjectsAdapter adapter = new ProjectsAdapter(getActivity(), R.layout.contact_list_item, projects);
    choices.setAdapter(adapter);
    choices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Project project = i == projects.size() ? null : projects.get(i);
        listener.onItemClick(project);
      }
    });
    title.setText("Select Project");
    return dialog;
  }
}
