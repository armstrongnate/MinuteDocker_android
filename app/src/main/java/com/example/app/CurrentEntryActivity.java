package com.example.app;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public class CurrentEntryActivity extends ActionBarActivity implements RefreshActivity, DurationDialogListener {
  public static final String TAG = CurrentEntryActivity.class.getSimpleName();
  protected Entry currentEntry;
  protected CurrentTimesFragment currentTimesFragment;
  protected EntryFormFragment entryFormFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_current_entry);

    android.app.ActionBar ab = getActionBar();
    ab.setTitle(R.string.app_name);

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
        currentEntry.log(CurrentEntryActivity.this, new AsyncTaskCompleteListener<String>() {
          @Override
          public void onTaskComplete(String result) {
            getCurrentEntry();
          }
        });
      }
    });
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
    if (id == R.id.action_refresh) {
      View refresher = findViewById(R.id.action_refresh);
      Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotation);
      rotation.setRepeatCount(10);
      refresher.startAnimation(rotation);
      getCurrentEntry();
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
          currentEntry = Entry.fromJSONObject(jsonEntry);
          currentTimesFragment.setCurrentEntry(currentEntry);
          entryFormFragment.setCurrentEntry(currentEntry);
          Toast.makeText(getApplicationContext(), "Refreshed!",
            Toast.LENGTH_LONG).show();
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

  /**
   * A placeholder fragment containing a simple view.
   */
  public static class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_current_entry, container, false);
      return rootView;
    }
  }

  @Override
  public void onDurationDialogPositiveClick(DurationDialogFragment dialogFragment) {
    currentTimesFragment.setDuration(dialogFragment.hours, dialogFragment.minutes, 0);
  }

}
