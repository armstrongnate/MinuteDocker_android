package com.example.app;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by nate on 3/26/14.
 */
public class Contact {
  public static String TAG = "Contact";
  public int externalId;
  public String name;
  public String shortCode;

  public Contact() {
  }

  public static Contact fromJSONObject(JSONObject json) {
    Contact contact = new Contact();
    try {
      if (!json.isNull("id"))
        contact.externalId = json.getInt("id");
      if (!json.isNull("name"))
        contact.name = json.getString("name");
      if (!json.isNull("short_code"))
        contact.shortCode = json.getString("short_code");
    }
    catch (Exception e) {
      Log.e(TAG, "Exception caught: ", e);
    }

    return contact;
  }
}
