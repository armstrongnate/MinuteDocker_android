package com.example.app;

import org.json.JSONObject;

/**
 * Created by nate on 4/18/14.
 */
public class EntryRow {
  public String contact;
  public String project;
  public double duration;
  public boolean isHeader;
  public String headerText;

  public EntryRow() {
    // empty constructor
    this.contact = "Foo Bar";
  }

  public EntryRow fromJSON(JSONObject jsonEntry) {
    return new EntryRow();
  }
}
