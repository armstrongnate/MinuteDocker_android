package com.example.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);

            TextView logoText = (TextView) rootView.findViewById(R.id.logo_text);
            TextView helpText = (TextView) rootView.findViewById(R.id.help_text);
            EditText apiKey = (EditText) rootView.findViewById(R.id.api_key);
            Button button = (Button) rootView.findViewById(R.id.button);
            ImageView apiKeyHelp = (ImageView) rootView.findViewById(R.id.api_key_help);

            Typeface extraBold = Typeface.createFromAsset(getActivity().getAssets(), "Proxima_Nova_Extrabold.ttf");
            logoText.setTypeface(extraBold);
            helpText.setTypeface(extraBold);
            apiKey.setTypeface(extraBold);
            button.setTypeface(extraBold);

            createApiHelpDialog();

            apiKeyHelp.setOnClickListener(this);
            return rootView;
        }

        private void createApiHelpDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.api_help_dialog_message)
                    .setTitle(R.string.api_help_dialog_title)
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
            }
        }
    }

}
