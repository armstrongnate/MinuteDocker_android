package com.example.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by nate on 3/23/14.
 */
public class CurrentTimesFragment extends android.support.v4.app.Fragment {
    public static final String TAG = CurrentTimesFragment.class.getSimpleName();
    protected Entry currentEntry;
    protected TextView currentDuration;

    public CurrentTimesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_times, container, false);
        currentDuration = (TextView) rootView.findViewById(R.id.current_duration);
        setCurrentEntry();
        setTodayTime();
        setWeekTime();
        return rootView;
    }

    private void setCurrentEntry() {
        ApiTask apiTask = new ApiTask(getActivity(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject jsonEntry = new JSONObject(result);
                    currentEntry = Entry.fromJSONObject(jsonEntry);
                    int hours = (int)Math.floor(currentEntry.duration / 3600);
                    int minutes = (int)Math.floor(currentEntry.duration / 60) % 60;
                    int seconds = currentEntry.duration % 60;
                    currentDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                    Log.i(TAG, "Entry duration: " + currentEntry.duration);
                }
                catch (JSONException e) {
                    Log.e(TAG, "JSONException caught: ", e);
                }
            }
        });
        apiTask.execute("https://minutedock.com/api/v1/entries/current.json?api_key=0e3ec0f390e9b7aff763d64d8cea6c50");
    }

    private void setTodayTime() {

    }

    private void setWeekTime() {

    }
}
