package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingsActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    android.app.ActionBar ab = getActionBar();
    ab.setTitle("Settings");

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
        .add(R.id.settings, new PlaceholderFragment())
        .commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.settings, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_current_entry) {
      Intent intent = new Intent(SettingsActivity.this, CurrentEntryActivity.class);
      startActivity(intent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public static class PlaceholderFragment extends Fragment {
    private ViewHolder viewHolder;

    private class ViewHolder {
      Button logOutButton;
    }

    public PlaceholderFragment() {
      viewHolder = new ViewHolder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
      viewHolder.logOutButton = (Button)rootView.findViewById(R.id.settings_log_out_button);
      viewHolder.logOutButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MinuteDockr app = MinuteDockr.getInstance(getActivity());
          app.contacts = null;
          SharedPreferences.Editor editor = app.sharedPreferences.edit();
          editor.remove(MinuteDockr.USERNAME_PREFS_KEY);
          editor.remove(MinuteDockr.PASSWORD_PREFS_KEY);
          editor.remove(app.CURRENT_ACCOUNT_ID_PREFS_KEY);
          editor.commit();
          Intent intent = new Intent(getActivity(), MainActivity.class);
          startActivity(intent);
        }
      });

      return rootView;
    }
  }
}
