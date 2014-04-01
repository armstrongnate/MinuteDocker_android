package com.example.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GestureDetectorCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Calendar;

import android.os.Handler;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by nate on 3/23/14.
 */
public class CurrentTimesFragment extends android.support.v4.app.Fragment {
    public static final String TAG = CurrentTimesFragment.class.getSimpleName();
    protected Entry currentEntry;
    protected TextView currentDuration;
    protected TimePickerDialog currentTimePickerDialog;
    private GestureDetectorCompat gDetect;
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
        gDetect = new GestureDetectorCompat(getActivity(), new GestureListener());
        currentDuration = (TextView) rootView.findViewById(R.id.current_duration);
        currentDuration.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gDetect.onTouchEvent(motionEvent);
                return true;
            }
        });

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

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            showDurationDialog();
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            currentEntry.toggleActive(getActivity(), new AsyncTaskCompleteListener<String>() {
                @Override
                public void onTaskComplete(String result) {
                    String flag = currentEntry.isActive ? "Resuming" : "Paused!";
                    Toast.makeText(getActivity(), flag,
                            Toast.LENGTH_LONG).show();
                }
            });
            return true;
        }
    }

    private void showDurationDialog() {
        DurationDialogFragment dialog = new DurationDialogFragment();
        int hours = (int)Math.floor(currentDurationSeconds / 3600);
        int minutes = (int)Math.floor(currentDurationSeconds / 60) % 60;
        dialog.setDuration(hours, minutes);
        dialog.show(getActivity().getSupportFragmentManager(), "DurationDialogFragment");
    }

    public void setDuration(int hours, int minutes, int seconds) {
        currentDurationSeconds = (hours * 3600) + (minutes * 60) + seconds;
        currentEntry.duration = currentDurationSeconds;
        updateCurrentEntry();
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void updateCurrentEntry() {
        currentEntry.update(getActivity(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                Toast.makeText(getActivity(), "Updated!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
