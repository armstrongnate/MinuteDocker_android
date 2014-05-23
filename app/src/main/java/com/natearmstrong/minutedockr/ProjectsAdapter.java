package com.natearmstrong.minutedockr;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.natearmstrong.minutedockr.R;

import java.util.ArrayList;

/**
 * Created by nate on 4/2/14.
 */
public class ProjectsAdapter extends ArrayAdapter<Project> {
  private ArrayList<Project> projects;

  public ProjectsAdapter(Context context, int layoutResourceId, ArrayList<Project> data) {
    super(context, layoutResourceId, new ArrayList<Project>(data));
    Project noneProject = new Project();
    noneProject.shortCode = "None";
    projects = new ArrayList<Project>(data);
    projects.add(noneProject);
    add(noneProject);
  }

  @Override
  public View getView(int i, View convertView, ViewGroup viewGroup) {
    View v = convertView;

    if (v == null) {
      LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = inflater.inflate(R.layout.contact_list_item, null);

      Project project = projects.get(i);
      if (project != null) {
        TextView shortCode = (TextView) v.findViewById(R.id.short_code);
        shortCode.setText(String.format("#%s", project.shortCode));

        if (i == projects.size() - 1) {
          shortCode.setTextColor(getContext().getResources().getColor(R.color.md_danger));
          shortCode.setTypeface(Typeface.DEFAULT_BOLD);
          shortCode.setText(project.shortCode);
        }
      }
    }

    return v;
  }

}
