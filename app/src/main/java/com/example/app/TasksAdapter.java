package com.example.app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nate on 4/3/14.
 */
public class TasksAdapter extends ArrayAdapter<Task> {
    private ArrayList<Task> tasks;
    protected Task task;

    public TasksAdapter(Context context, int layoutResourceId, ArrayList<Task> data) {
        super(context, layoutResourceId, new ArrayList<Task>(data));
        tasks = new ArrayList<Task>(data);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View v = convertView;
        final int position = i;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.task_list_item, null);
            if (v != null) {
                task = tasks.get(i);
                if (task != null) {
                    TextView shortCode = (TextView) v.findViewById(R.id.short_code);
                    shortCode.setText(String.format("#%s", task.shortCode));
                    final CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);
                    checkBox.setChecked(task.isChecked);
                    final View cell = v.findViewById(R.id.task_cell);

                    v.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View arg0, MotionEvent arg1) {
                            switch (arg1.getAction()) {
                                case MotionEvent.ACTION_DOWN: {
                                    cell.setBackgroundColor(Color.parseColor("#dddddd"));
                                    break;
                                }
                                case MotionEvent.ACTION_UP: {
                                    task = tasks.get(position);
                                    cell.setBackgroundColor(Color.TRANSPARENT);
                                    task.isChecked = !task.isChecked;
                                    checkBox.setChecked(task.isChecked);
                                    break;
                                }
                                case MotionEvent.ACTION_CANCEL: {
                                    cell.setBackgroundColor(Color.TRANSPARENT);
                                }
                            }
                            return true;
                        }
                    });
                }
            }
        }

        return v;
    }

}
