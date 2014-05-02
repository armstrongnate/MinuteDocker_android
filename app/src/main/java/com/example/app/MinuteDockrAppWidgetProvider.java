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
  private static int durationSeconds;
  private static ComponentName widget;
  private static RemoteViews remoteViews;
  private static AppWidgetManager appWidgetManager;
  private static int[] appWidgetIds;

  private static Handler timerHandler;
  private static Runnable timerRunnable;

  @Override
  public void onUpdate(Context context, AppWidgetManager _appWidgetManager, int[] _appWidgetIds) {
    appWidgetIds = _appWidgetIds;
    remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    widget = new ComponentName(context, MinuteDockrAppWidgetProvider.class);
    appWidgetManager = _appWidgetManager;

    Intent intent = new Intent(context, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    remoteViews.setOnClickPendingIntent(R.id.widget_labels, pendingIntent);

    remoteViews.setOnClickPendingIntent(R.id.widget_action_button, getPendingSelfIntent(context, ACTION_CLICKED));
    getCurrentEntry(context, false);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    if (remoteViews == null) {
      remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    }

    if (ACTION_CLICKED.equals(intent.getAction())) {
      widget = new ComponentName(context, MinuteDockrAppWidgetProvider.class);
      if (appWidgetManager == null) {
        appWidgetManager = AppWidgetManager.getInstance(context);
      }
      getCurrentEntry(context, true);
    }
  }

  protected PendingIntent getPendingSelfIntent(Context context, String action) {
    Intent intent = new Intent(context, getClass());
    intent.setAction(action);
    return PendingIntent.getBroadcast(context, 0, intent, 0);
  }

  private void getCurrentEntry(final Context context, final boolean fromButton) {
    if (fromButton) {
      remoteViews.setImageViewResource(R.id.widget_action_button, 0);
    }

    if (timerHandler == null) {
      timerHandler = new Handler();
    }
    else {
      timerHandler.removeCallbacks(timerRunnable);
    }
    if (timerRunnable == null) {
      timerRunnable = new Runnable() {

        @Override
        public void run() {
          updateWidgetView();
          if (currentEntry.isActive) {
            durationSeconds += 10;
            timerHandler.postDelayed(this, 10000);
          }
        }
      };
    }

    updateAppWidgets();

    MinuteDockr.getInstance(context).getCurrentEntry(new AsyncTaskCompleteListener<Entry>() {
      @Override
      public void onTaskComplete(Entry entry) {
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

  private static void updateWidgetView() {
    int hours = (int)Math.floor(durationSeconds / 3600);
    int minutes = (int)Math.floor(durationSeconds / 60) % 60;
    remoteViews.setTextViewText(R.id.widget_duration, String.format("%02d:%02d", hours, minutes));
    remoteViews.setImageViewResource(R.id.widget_action_button, currentEntry.isActive ? R.drawable.widget_pause : R.drawable.widget_play);
    updateAppWidgets();
  }

  private static void updateAppWidgets() {
    if (appWidgetIds == null) {
      appWidgetManager.updateAppWidget(widget, remoteViews);
    }
    else {
      for (int i=0; i<appWidgetIds.length; i++) {
        appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
      }
    }
  }
}
