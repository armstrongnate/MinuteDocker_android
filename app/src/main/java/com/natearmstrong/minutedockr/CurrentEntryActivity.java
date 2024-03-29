package com.natearmstrong.minutedockr;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.natearmstrong.minutedockr.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrentEntryActivity extends ActionBarActivity implements RefreshActivity, DurationDialogListener {
  public static final String TAG = CurrentEntryActivity.class.getSimpleName();
  protected Entry currentEntry;
  protected CurrentTimesFragment currentTimesFragment;
  protected EntryFormFragment entryFormFragment;
  protected MinuteDockr app;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_current_entry);

    android.app.ActionBar ab = getActionBar();
    ab.setTitle("Dock");

    // handle to fragments
    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    currentTimesFragment = (CurrentTimesFragment) fragmentManager.findFragmentById(R.id.fragment_current_times);
    entryFormFragment = (EntryFormFragment) fragmentManager.findFragmentById(R.id.fragment_entry_form);

    // log button
    Button logButton = (Button) findViewById(R.id.log_button);
    logButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        currentEntry = entryFormFragment.getCurrentEntry();
        currentEntry.duration = currentTimesFragment.currentDurationSeconds;
        currentEntry.update(CurrentEntryActivity.this, new AsyncTaskCompleteListener<String>() {
          @Override
          public void onTaskComplete(String result) {
            logCurrentEntry();
          }
        });
      }
    });

    // current entry
    app = MinuteDockr.getInstance(CurrentEntryActivity.this);
    setCurrentEntry(MinuteDockr.getInstance(CurrentEntryActivity.this).currentEntry);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.current_entry, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      Intent intent = new Intent(CurrentEntryActivity.this, SettingsActivity.class);
      startActivity(intent);
      return true;
    }
    if (id == R.id.action_refresh) {
      getCurrentEntry();
      return true;
    }
    if (id == R.id.action_entries) {
      Intent intent = new Intent(CurrentEntryActivity.this, EntriesActivity.class);
      startActivity(intent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResume() {
    super.onResume();
    getCurrentEntry();
  }

  @Override
  public void onRefresh() {
    getCurrentEntry();
  }

  @Override
  public void onRefreshFinished() {
    View refresher = findViewById(R.id.action_refresh);
    refresher.clearAnimation();
  }

  public void getCurrentEntry() {
    ApiTask apiTask = new ApiTask(this, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        try {
          JSONObject jsonEntry = new JSONObject(result);
          setCurrentEntry(Entry.fromJSONObject(jsonEntry));
        }
        catch (JSONException e) {
          Log.e(TAG, "JSONException caught: ", e);
        }
        catch (NullPointerException e) {
          Log.e(TAG, "Null pointer exception caught: ", e);
        }
      }
    });
    apiTask.execute(MinuteDockr.getInstance(this).getCurrentEntryUrl());
  }

  private void setCurrentEntry(Entry entry) {
    currentEntry = entry;
    currentTimesFragment.setCurrentEntry(currentEntry);
    entryFormFragment.setCurrentEntry(currentEntry);
    MinuteDockr app = MinuteDockr.getInstance(CurrentEntryActivity.this);
    app.sharedPreferences.edit().putInt(app.CURRENT_USER_ID_PREFS_KEY, currentEntry.userId).commit();
    app.currentEntry = currentEntry;
  }

  @Override
  public void onDurationDialogPositiveClick(DurationDialogFragment dialogFragment) {
    currentTimesFragment.setDuration(dialogFragment.hours, dialogFragment.minutes, 0);
  }

  private void logCurrentEntry() {
    currentEntry.log(this, new AsyncTaskCompleteListener<String>() {
      @Override
      public void onTaskComplete(String result) {
        getCurrentEntry();
      }
    });
  }

}
