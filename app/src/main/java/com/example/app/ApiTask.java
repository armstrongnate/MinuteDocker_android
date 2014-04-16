package com.example.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
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
  protected AsyncTaskCompleteListener<String> callback;
  protected Context context;
  protected static final String TAG = "ApiTask";

  public ApiTask() {
  }

  public ApiTask(Context context, AsyncTaskCompleteListener<String> cb) {
    this.context = context;
    this.callback = cb;
  }

  public static String post(String url, JSONObject jsonObject){
    InputStream inputStream = null;
    String result = "";
    try {
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(url);

      if (jsonObject != null) {
        String json = "";
        json = jsonObject.toString();
        StringEntity se = new StringEntity(json);

        httpPost.setEntity(se);
      }

      httpPost.setHeader("Accept", "application/json");
      httpPost.setHeader("Content-type", "application/json");

      HttpResponse httpResponse = httpclient.execute(httpPost);

      inputStream = httpResponse.getEntity().getContent();

      if (inputStream != null) {
        result = convertInputStreamToString(inputStream);
      }
      else
        result = "Did not work!";

    } catch (Exception e) {
      Log.d("InputStream", e.getLocalizedMessage());
    }

    return result;
  }

  public static String put(String url, JSONObject jsonObject){
    InputStream inputStream = null;
    String result = "";
    try {
      HttpClient httpclient = new DefaultHttpClient();
      HttpPut httpPut = new HttpPut(url);

      if (jsonObject != null) {
        StringEntity se = new StringEntity(jsonObject.toString());
        httpPut.setEntity(se);
      }

      httpPut.setHeader("Accept", "application/json");
      httpPut.setHeader("Content-type", "application/json");

      HttpResponse httpResponse = httpclient.execute(httpPut);

      inputStream = httpResponse.getEntity().getContent();

      if (inputStream != null) {
        result = convertInputStreamToString(inputStream);
      }
      else
        result = "Did not work!";

    }
    catch (Exception e) {
      Log.d("InputStream", e.getLocalizedMessage());
    }

    return result;
  }

  @Override
  protected String doInBackground(String... strings) {
    int responseCode;
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

  private static String convertInputStreamToString(InputStream inputStream) throws IOException{
    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
    String line = "";
    String result = "";
    while((line = bufferedReader.readLine()) != null)
      result += line;

    inputStream.close();
    return result;

  }
}
