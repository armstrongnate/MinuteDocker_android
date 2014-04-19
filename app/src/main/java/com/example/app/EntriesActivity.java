package com.example.app;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class EntriesActivity extends ActionBarActivity {
  protected FragmentPagerAdapter adapterViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_entries);
    ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
    adapterViewPager = new EntriesPagerAdapter(getSupportFragmentManager());
    vpPager.setAdapter(adapterViewPager);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.entries, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public static class EntriesPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_PAGES = 3;

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

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0: {
          return "Today";
        }
        case 1: {
          return "Week";
        }
        case 2: {
          return "Month";
        }
        default:
          return null;
      }
    }
  }


}
