package com.natearmstrong.minutedockr;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.natearmstrong.minutedockr.R;

import java.util.ArrayList;

/**
 * Created by nate on 4/1/14.
 */
public class ContactsDialog extends SingleChoiceDialog {
  protected ArrayList<Contact> contacts;

  public ContactsDialog(SingleChoiceDialogListener singleChoiceDialogListener) {
    listener = singleChoiceDialogListener;
    contacts = new ArrayList<Contact>();
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    ContactsAdapter adapter = new ContactsAdapter(getActivity(), R.layout.contact_list_item, contacts);
    choices.setAdapter(adapter);
    choices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Contact contact = i == contacts.size() ? null : contacts.get(i);
        listener.onItemClick(contact);
      }
    });
    title.setText("Select Contact");
    return dialog;
  }
}
