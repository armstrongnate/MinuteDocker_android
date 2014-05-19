package com.example.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by nate on 4/27/14.
 */
public class SplashFragment extends Fragment {
  protected MinuteDockr app;
  protected int progress;
  protected static int NUM_API_CALLS = 5;
  protected ViewHolder viewHolder;

  private class ViewHolder {
    ProgressBar progressBar;
  }

  public SplashFragment() {
    // Required empty public constructor
    app = MinuteDockr.getInstance(getActivity());
    progress = 0;
    viewHolder = new ViewHolder();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_splash, null);
    viewHolder.progressBar = (ProgressBar)view.findViewById(R.id.main_progress_bar);
    viewHolder.progressBar.setMax(NUM_API_CALLS);
    viewHolder.progressBar.setProgress(progress);
    initApp();
    return view;
  }

  private void navigateToCurrentEntry() {
    Intent intent = new Intent(getActivity(), CurrentEntryActivity.class);
    startActivity(intent);
  }

  private void initApp() {
    if (apiCallsAreFinished()) {
      navigateToCurrentEntry();
    }
    app.getContactsAsync(new AsyncTaskCompleteListener<HashMap<Integer, Contact>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Contact> result) {
        onApiTaskComplete(result != null);
      }
    });
    app.getProjectsAsync(new AsyncTaskCompleteListener<HashMap<Integer, Project>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Project> result) {
        onApiTaskComplete(result != null);
      }
    });
    app.getTasksAsync(new AsyncTaskCompleteListener<HashMap<Integer, Task>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Task> result) {
        onApiTaskComplete(result != null);
      }
    });

    // get current entry and then entries for today and the week
    // since the we query the entries based on the user, we have to get the current entry first
    // and then we use the user_id on it to query for the entries
    final AsyncTaskCompleteListener<HashMap<Integer, Entry>> listener = new AsyncTaskCompleteListener<HashMap<Integer, Entry>>() {
      @Override
      public void onTaskComplete(HashMap<Integer, Entry> result) {
        onApiTaskComplete(result != null);
      }
    };
    app.getCurrentEntry(new AsyncTaskCompleteListener<Entry>() {
      @Override
      public void onTaskComplete(Entry result) {
        onApiTaskComplete(result != null);
        app.fetchEntries(0, listener);
      }
    });
  }

  private boolean apiCallsAreFinished() {
    return app.contacts != null && app.projects != null && app.tasks != null && app.todayEntries != null && app.currentEntry != null;
  }

  private void onApiTaskComplete(boolean success) {
    if (success) {
      if (viewHolder.progressBar != null) {
        progress += 1;
        viewHolder.progressBar.setProgress(progress);
      }
      if (apiCallsAreFinished() && progress == NUM_API_CALLS) {
        navigateToCurrentEntry();
      }
    }
    else {
      Toast.makeText(getActivity(), "Network Error!",
        Toast.LENGTH_LONG).show();
    }
  }
}
