package com.example.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import java.util.HashMap;

/**
 * Created by nate on 4/29/14.
 */
public class MinuteDockrAppWidgetProvider extends AppWidgetProvider {
  private static final String ACTION_CLICKED = "minuteDockrAppWidgetActionClicked";
  private static Entry currentEntry;
  private int durationSeconds;
  private ComponentName widget;
  private RemoteViews remoteViews;
  private AppWidgetManager appWidgetManager;
  private int numAppWidgets = 0;
  private int[] appWidgetIds;

  private Handler timerHandler = new Handler();
  private Runnable timerRunnable = new Runnable() {

    @Override
    public void run() {
      updateWidgetView();
      if (currentEntry.isActive) {
        durationSeconds += 10;
        timerHandler.postDelayed(this, 10000);
      }
    }
  };

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    numAppWidgets = appWidgetIds.length;
    this.appWidgetIds = appWidgetIds;
    remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    widget = new ComponentName(context, MinuteDockrAppWidgetProvider.class);
    this.appWidgetManager = appWidgetManager;

    Intent intent = new Intent(context, CurrentEntryActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    remoteViews.setOnClickPendingIntent(R.id.widget_labels, pendingIntent);

    remoteViews.setOnClickPendingIntent(R.id.widget_action_button, getPendingSelfIntent(context, ACTION_CLICKED));
    getCurrentEntry(context, false);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    widget = new ComponentName(context, MinuteDockrAppWidgetProvider.class);
    appWidgetManager = AppWidgetManager.getInstance(context);

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
    remoteViews.setImageViewResource(R.id.widget_action_button, 0);
    updateAppWidgets();

    MinuteDockr.getInstance(context).getCurrentEntry(new AsyncTaskCompleteListener<Entry>() {
      @Override
      public void onTaskComplete(Entry entry) {
        timerHandler.removeCallbacks(timerRunnable);
        currentEntry = entry;
        durationSeconds = currentEntry.duration;
        timerHandler.postDelayed(timerRunnable, 0);
        if (fromButton) {
          currentEntry.toggleActive(context, new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
              updateWidgetView();
            }
          });
        }
        else {
          updateWidgetView();
        }
      }
    });
  }

  private void updateWidgetView() {
    int hours = (int)Math.floor(durationSeconds / 3600);
    int minutes = (int)Math.floor(durationSeconds / 60) % 60;
    remoteViews.setTextViewText(R.id.widget_duration, String.format("%02d:%02d", hours, minutes));
    remoteViews.setImageViewResource(R.id.widget_action_button, currentEntry.isActive ? R.drawable.widget_pause : R.drawable.widget_play);
    updateAppWidgets();
  }

  private void updateAppWidgets() {
    for (int i=0; i<numAppWidgets; i++) {
      appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
    }
  }
}
