package com.example.app;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
  protected MinuteDockr app;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ActionBar ab = getActionBar();
    ab.hide();
    app = MinuteDockr.getInstance(this);
    int currentAccountId = app.sharedPreferences.getInt(MinuteDockr.CURRENT_ACCOUNT_ID_PREFS_KEY, -1);
    String username = app.sharedPreferences.getString(MinuteDockr.USERNAME_PREFS_KEY, "");
    String password = app.sharedPreferences.getString(MinuteDockr.PASSWORD_PREFS_KEY, "");
    if (currentAccountId == -1 || username.length() < 1 || password.length() < 1) {
      navigateToLogin();
    }
    else {
      if (savedInstanceState == null) {
        getSupportFragmentManager().beginTransaction()
          .add(R.id.container, new SplashFragment())
          .commit();
      }
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void navigateToLogin() {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

}
