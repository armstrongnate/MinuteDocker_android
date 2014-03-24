package com.example.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        private AlertDialog apiKeyHelpDialog;
        protected EditText apiKey;
        protected SharedPreferences prefs;
        public static final String TAG = LoginActivity.class.getSimpleName();

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);

            MinuteDockr app = MinuteDockr.getInstance(getActivity());
            String apiKeyString = app.currentApiKey;

            TextView logoText = (TextView) rootView.findViewById(R.id.logo_text);
            TextView helpText = (TextView) rootView.findViewById(R.id.help_text);
            apiKey = (EditText) rootView.findViewById(R.id.api_key);
            apiKey.setText(apiKeyString);
            Button button = (Button) rootView.findViewById(R.id.button);
            button.setOnClickListener(this);
            ImageView apiKeyHelp = (ImageView) rootView.findViewById(R.id.api_key_help);

            Typeface extraBold = Typeface.createFromAsset(getActivity().getAssets(), "Proxima_Nova_Extrabold.ttf");
            Typeface semiBold = Typeface.createFromAsset(getActivity().getAssets(), "Proxima_Nova_Semibold.ttf");
            logoText.setTypeface(extraBold);
            helpText.setTypeface(extraBold);
            apiKey.setTypeface(semiBold);
            button.setTypeface(extraBold);

            createApiHelpDialog();

            apiKeyHelp.setOnClickListener(this);
            return rootView;
        }

        private void createApiHelpDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View helpDialog = MinuteDockr.getInstance(getActivity())
                    .customDialogView(R.string.api_help_dialog_title, R.string.api_help_dialog_message);
            builder.setView(helpDialog)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
            apiKeyHelpDialog = builder.create();
        }

        // onclick
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.api_key_help:
                    apiKeyHelpDialog.show();

                case R.id.button:
                    if (isNetworkAvailable()) {
                        GetCurrentAccountTask getAccountTask = new GetCurrentAccountTask();
                        getAccountTask.execute();
                    }
                    else {
                        // show network error alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        View helpDialog = MinuteDockr.getInstance(getActivity())
                                .customDialogView(R.string.no_internet_dialog_title, R.string.no_internet_dialog_message);
                        builder.setView(helpDialog)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User clicked OK button
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
            }
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            return networkInfo != null && networkInfo.isConnected();
        }

        public class GetCurrentAccountTask extends AsyncTask<Object, Void, String> {

            @Override
            protected String doInBackground(Object... arg0) {
                int responseCode = -1;
                try {
                    URL currentAccountUrl = new URL("https://minutedock.com/api/v1/accounts/current.json?api_key=" + apiKey.getText().toString());
                    HttpURLConnection connection = (HttpURLConnection) currentAccountUrl.openConnection();
                    connection.connect();

                    responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        MinuteDockr app = MinuteDockr.getInstance(getActivity());
                        prefs.edit().putString(app.API_KEY_PREFS_KEY, apiKey.getText().toString()).commit();

                        InputStream inputStream = connection.getInputStream();
                        Reader reader = new InputStreamReader(inputStream);
                        int contentLength = connection.getContentLength();
                        char[] charArray = new char[contentLength];
                        reader.read(charArray);
                        String responseData = new String(charArray);

                        JSONObject jsonResponse = new JSONObject(responseData);
                        prefs.edit().putInt(app.CURRENT_ACCOUNT_ID_PREFS_KEY, jsonResponse.getInt("id")).commit();
                    }
                }
                catch (MalformedURLException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
                catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
                catch (Exception e) {
                    Log.e(TAG, "Exception caught: ", e);
                }

                return "Code: " + responseCode;
            }
        }
    }

}
