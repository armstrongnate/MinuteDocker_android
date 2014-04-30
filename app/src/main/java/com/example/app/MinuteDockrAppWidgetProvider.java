package com.example.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import java.util.HashMap;

/**
 * Created by nate on 4/29/14.
 */
public class MinuteDockrAppWidgetProvider extends AppWidgetProvider {
  private static final String ACTION_CLICKED = "minuteDockrAppWidgetActionClicked";
  private Entry currentEntry;

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    RemoteViews remoteViews;
    ComponentName widget;

    remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    widget = new ComponentName(context, MinuteDockrAppWidgetProvider.class);

    Intent intent = new Intent(context, CurrentEntryActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    remoteViews.setOnClickPendingIntent(R.id.widget_labels, pendingIntent);

    remoteViews.setOnClickPendingIntent(R.id.widget_action_button, getPendingSelfIntent(context, ACTION_CLICKED));
    appWidgetManager.updateAppWidget(widget, remoteViews);
    getCurrentEntry(context, false);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);

    if (ACTION_CLICKED.equals(intent.getAction())) {
      getCurrentEntry(context, true);
    }
    else if ("android.appwidget.action.APPWIDGET_UPDATE".equals(intent.getAction())) {
      getCurrentEntry(context, false);
    }
  }

  protected PendingIntent getPendingSelfIntent(Context context, String action) {
    Intent intent = new Intent(context, getClass());
    intent.setAction(action);
    return PendingIntent.getBroadcast(context, 0, intent, 0);
  }

  private void getCurrentEntry(final Context context, final boolean fromButton) {
    final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

    final RemoteViews remoteViews;
    final ComponentName widget;

    remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    widget = new ComponentName(context, MinuteDockrAppWidgetProvider.class);

    remoteViews.setImageViewResource(R.id.widget_action_button, 0);
    appWidgetManager.updateAppWidget(widget, remoteViews);

    MinuteDockr.getInstance(context).getCurrentEntry(new AsyncTaskCompleteListener<Entry>() {
      @Override
      public void onTaskComplete(final Entry entry) {
        final String description;
        if (entry.description != null && entry.description.length() > 0) {
          description = entry.description;
        }
        else {
          description = "No description.";
        }
        MinuteDockr.getInstance(context).getContactsAsync(new AsyncTaskCompleteListener<HashMap<Integer, Contact>>() {
          @Override
          public void onTaskComplete(HashMap<Integer, Contact> result) {
            Contact contact = result.get(entry.contactId);
            final String contactShortCode;
            if (contact != null && contact.shortCode != null && contact.shortCode.length() > 0) {
              contactShortCode = String.format("@%s", contact.shortCode);
            }
            else {
              contactShortCode = "No contact selected.";
            }
            if (fromButton) {
              currentEntry = entry;
              currentEntry.toggleActive(context, new AsyncTaskCompleteListener<String>() {
                @Override
                public void onTaskComplete(String result) {
                  updateWidgetView(context, description, contactShortCode, currentEntry.isActive);
                }
              });
            }
            else {
              updateWidgetView(context, description, contactShortCode, entry.isActive);
            }
          }
        });
      }
    });
  }

  private void updateWidgetView(Context context, String description, String contact, boolean active) {
    final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

    final RemoteViews remoteViews;
    final ComponentName widget;

    remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    widget = new ComponentName(context, MinuteDockrAppWidgetProvider.class);
    remoteViews.setTextViewText(R.id.widget_description, description);
    remoteViews.setTextViewText(R.id.widget_contact, contact);
    remoteViews.setImageViewResource(R.id.widget_action_button, active ? R.drawable.widget_pause : R.drawable.widget_play);
    appWidgetManager.updateAppWidget(widget, remoteViews);
  }
}
