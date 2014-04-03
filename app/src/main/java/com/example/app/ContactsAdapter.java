package com.example.app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nate on 4/1/14.
 */
public class ContactsAdapter extends ArrayAdapter<Contact> {
    private ArrayList<Contact> contacts;

    public ContactsAdapter(Context context, int layoutResourceId, ArrayList<Contact> data) {
        super(context, layoutResourceId, new ArrayList<Contact>(data));
        Contact noneContact = new Contact();
        noneContact.shortCode = "None";
        contacts = new ArrayList<Contact>(data);
        contacts.add(noneContact);
        add(noneContact);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.contact_list_item, null);

            Contact contact = contacts.get(i);
            if (contact != null) {
                TextView shortCode = (TextView) v.findViewById(R.id.short_code);
                shortCode.setText(String.format("@%s", contact.shortCode));

                if (i == contacts.size() - 1) {
                    shortCode.setTextColor(getContext().getResources().getColor(R.color.md_danger));
                    shortCode.setTypeface(Typeface.DEFAULT_BOLD);
                    shortCode.setText(contact.shortCode);
                }
            }
        }

        return v;
    }

}
