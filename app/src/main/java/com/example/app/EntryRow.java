package com.example.app;

import android.util.Log;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by nate on 4/18/14.
 */
public class EntryRow {
  public String contact;
  public String project;
  public String[] tasks;
  public double duration;
  public boolean isHeader;
  public String headerText;
  public String description;
  public Date loggedAt;

  public EntryRow() {
    // empty constructor
  }

  public static EntryRow fromJSON(JSONObject jsonEntry, HashMap<Integer, Contact> contacts) {
    EntryRow entryRow = new EntryRow();
    try {
      if (!jsonEntry.isNull("description"))
        entryRow.description = jsonEntry.getString("description");
      if (!jsonEntry.isNull("contact_id"))
        entryRow.contact = contacts.get(jsonEntry.getInt("contact_id")).shortCode;
    }
    catch (Exception e) {
      // error occurred
    }
    return entryRow;
  }
}
