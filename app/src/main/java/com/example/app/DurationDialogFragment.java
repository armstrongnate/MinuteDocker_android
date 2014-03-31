package com.example.app;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;


interface DurationDialogListener {
    public void onDurationDialogPositiveClick(DurationDialogFragment dialogFragment);
}

public class DurationDialogFragment extends DialogFragment {
    protected DurationDialogListener listener;
    protected NumberPicker hoursPicker;
    protected NumberPicker minutesPicker;
    public int hours;
    public int minutes;

    public DurationDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View durationDialogFragment = inflater.inflate(R.layout.fragment_duration_dialog, null);
        hoursPicker = (NumberPicker) durationDialogFragment.findViewById(R.id.hours_picker);
        minutesPicker = (NumberPicker) durationDialogFragment.findViewById(R.id.minutes_picker);
        hoursPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hoursPicker.setMaxValue(100);
        hoursPicker.setMinValue(0);
        hoursPicker.setValue(hours);
        minutesPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutesPicker.setMaxValue(60);
        minutesPicker.setMinValue(0);
        minutesPicker.setValue(minutes);
        builder.setView(durationDialogFragment);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                hours = hoursPicker.getValue();
                minutes = minutesPicker.getValue();
                listener.onDurationDialogPositiveClick(DurationDialogFragment.this);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // just close
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (DurationDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DurationDialogListener");
        }
    }

    public void setDuration(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }
}
