package com.example.app;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


interface SingleChoiceDialogListener {
    public void onItemClick(Object choice);
}

public class SingleChoiceDialog extends DialogFragment {

    protected ListView choices;
    protected TextView title;
    public SingleChoiceDialogListener listener;


    public SingleChoiceDialog() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.fragment_single_choice_dialog, null);
        choices = (ListView) v.findViewById(R.id.single_choice_dialog_list);
        title = (TextView) v.findViewById(R.id.single_choice_dialog_title);
        builder.setView(v);
        return builder.create();
    }

}
