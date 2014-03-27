package com.example.app;

import android.app.Fragment;
import android.graphics.Typeface;
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
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by nate on 3/23/14.
 */
public class CurrentTimesFragment extends android.support.v4.app.Fragment {
    public static final String TAG = CurrentTimesFragment.class.getSimpleName();
    protected Entry currentEntry;
    protected TextView currentDuration;
    int currentDurationSeconds;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int hours = (int)Math.floor(currentDurationSeconds / 3600);
            int minutes = (int)Math.floor(currentDurationSeconds / 60) % 60;
            int seconds = currentDurationSeconds % 60;
            currentDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            if (currentEntry.isActive)
                currentDurationSeconds += 1;
                timerHandler.postDelayed(this, 1000);
        }
    };

    public CurrentTimesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_times, null);
        currentDuration = (TextView) rootView.findViewById(R.id.current_duration);
        setTodayTime();
        setWeekTime();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void setCurrentEntry(Entry entry) {
        timerHandler.removeCallbacks(timerRunnable);
        currentEntry = entry;
        currentDurationSeconds = currentEntry.duration;
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void setTodayTime() {

    }

    private void setWeekTime() {

    }
}
