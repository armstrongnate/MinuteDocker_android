package com.example.app;

import android.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrentEntryActivity extends ActionBarActivity implements RefreshActivity {
    public static final String TAG = CurrentEntryActivity.class.getSimpleName();
    protected Entry currentEntry;
    protected CurrentTimesFragment currentTimesFragment;
    protected EntryFormFragment entryFormFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_current_entry);

        getCurrentEntry();

        // handle to fragments
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        currentTimesFragment = (CurrentTimesFragment) fragmentManager.findFragmentById(R.id.fragment_current_times);
        entryFormFragment = (EntryFormFragment) fragmentManager.findFragmentById(R.id.fragment_entry_form);
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
        Toast.makeText(getApplicationContext(), "Refreshed!",
                Toast.LENGTH_LONG).show();
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

}
