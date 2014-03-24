package com.example.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nate on 3/23/14.
 */
interface AsyncTaskCompleteListener<T> {
    public void onTaskComplete(T result);
}

public class ApiTask extends AsyncTask<String, Void, String> {
    private AsyncTaskCompleteListener<String> callback;
    private Context context;
    private static final String TAG = "ApiTask";

    public ApiTask(Context context, AsyncTaskCompleteListener<String> cb) {
        this.context = context;
        this.callback = cb;
    }

    @Override
    protected String doInBackground(String... strings) {
        int responseCode = -1;
        String responseData = null;
        try {
            URL currentAccountUrl = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) currentAccountUrl.openConnection();
            connection.connect();

            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                Reader reader = new InputStreamReader(inputStream);
                int contentLength = connection.getContentLength();
                char[] charArray = new char[contentLength];
                reader.read(charArray);
                responseData = new String(charArray);
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

        return responseData;
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onTaskComplete(result);
    }
}
