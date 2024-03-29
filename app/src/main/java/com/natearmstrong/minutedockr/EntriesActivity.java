package com.natearmstrong.minutedockr;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.natearmstrong.minutedockr.R;


public class EntriesActivity extends ActionBarActivity {
  protected FragmentPagerAdapter adapterViewPager;

  public enum entryPages {
    TODAY, WEEK, MONTH
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_entries);

    android.app.ActionBar ab = getActionBar();
    ab.setTitle("Today");

    ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
    adapterViewPager = new EntriesPagerAdapter(getSupportFragmentManager());
    vpPager.setAdapter(adapterViewPager);
    vpPager.setOnPageChangeListener(new EntriesOnPageChangeListener());
  }

  @Override
  protected void onPause() {
    super.onPause();
    MinuteDockr app = MinuteDockr.getInstance(EntriesActivity.this);
    if (app.contactsApiTask != null && app.contactsApiTask.getStatus() == AsyncTask.Status.RUNNING) {
      app.contactsApiTask.cancel(true);
    }
    if (app.projectsApiTask != null && app.projectsApiTask.getStatus() == AsyncTask.Status.RUNNING) {
      app.projectsApiTask.cancel(true);
    }
    if (app.tasksApiTask != null && app.tasksApiTask.getStatus() == AsyncTask.Status.RUNNING) {
      app.tasksApiTask.cancel(true);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.entries, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      Intent intent = new Intent(EntriesActivity.this, SettingsActivity.class);
      startActivity(intent);
      return true;
    }
    if (id == R.id.action_current_entry) {
      Intent intent = new Intent(EntriesActivity.this, CurrentEntryActivity.class);
      startActivity(intent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public class EntriesOnPageChangeListener implements ViewPager.OnPageChangeListener {

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
      android.app.ActionBar ab = EntriesActivity.this.getActionBar();
      switch (position) {
        case 0: {
          ab.setTitle("Today");
          break;
        }
        case 1: {
          ab.setTitle("This Week");
          break;
        }
        case 2: {
          ab.setTitle("This Month");
          break;
        }
      }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
  }

  public static class EntriesPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_PAGES = 1;

    public EntriesPagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
      super(fragmentManager);
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
      return EntriesFragment.newInstance(position);
    }
  }

}
