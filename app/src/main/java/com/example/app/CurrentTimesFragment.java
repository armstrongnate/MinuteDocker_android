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
        findViews(rootView);
        setTodayTime();
        setWeekTime();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCurrentEntry();
//        Toast.makeText(getActivity().getApplicationContext(), "in onResume",
//                Toast.LENGTH_LONG).show();
    }

    private void setCurrentEntry() {
        ApiTask apiTask = new ApiTask(getActivity(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject jsonEntry = new JSONObject(result);
                    currentEntry = Entry.fromJSONObject(jsonEntry);
                    currentDurationSeconds = currentEntry.duration;
                    timerHandler.postDelayed(timerRunnable, 0);
                }
                catch (JSONException e) {
                    Log.e(TAG, "JSONException caught: ", e);
                }
                catch (NullPointerException e) {
                    Log.e(TAG, "Null pointer exception caught: ", e);
                }
            }
        });
        apiTask.execute(MinuteDockr.getInstance(getActivity()).getCurrentEntryUrl());
    }

    private void setTodayTime() {

    }

    private void setWeekTime() {

    }

    private void findViews(View rootView) {
        Typeface semiBold = Typeface.createFromAsset(getActivity().getAssets(), "Proxima_Nova_Semibold.ttf");
        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "Proxima_Nova_Regular.ttf");
        currentDuration = (TextView) rootView.findViewById(R.id.current_duration);
        currentDuration.setTypeface(semiBold);
        TextView currentDurationLabel = (TextView) rootView.findViewById(R.id.current_duration_label);
        currentDurationLabel.setTypeface(semiBold);
        TextView dayLabel = (TextView) rootView.findViewById(R.id.current_time_day_label);
        dayLabel.setTypeface(semiBold);
        TextView weekLabel = (TextView) rootView.findViewById(R.id.current_time_week_label);
        weekLabel.setTypeface(semiBold);
        TextView dayDuration = (TextView) rootView.findViewById(R.id.current_time_day);
        dayDuration.setTypeface(regular);
        TextView weekDuration = (TextView) rootView.findViewById(R.id.current_time_week);
        weekDuration.setTypeface(regular);
    }
}
