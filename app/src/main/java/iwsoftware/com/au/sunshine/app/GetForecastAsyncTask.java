package iwsoftware.com.au.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import iwsoftware.com.au.sunshine.app.forecast.ForecastData;
import iwsoftware.com.au.sunshine.app.forecast.JSONForecastDecoder;

/**
 *
 */
public class GetForecastAsyncTask extends AsyncTask<String, Void, List<ForecastData>>
{
  @Override
  protected List<ForecastData> doInBackground(String... params)
  {
    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;

    try
    {
      // Construct the URL for the OpenWeatherMap query
      // Possible parameters are available at OWM's forecast API page, at
      // http://openweathermap.org/API#forecast
      Uri.Builder uriBuilder = new Uri.Builder();
      uriBuilder.scheme("http");
      uriBuilder.authority("api.openweathermap.org");
      uriBuilder.path("data/2.5/forecast/daily");
      uriBuilder.appendQueryParameter("q", TextUtils.join(",", params));
      uriBuilder.appendQueryParameter("mode", "json");
      uriBuilder.appendQueryParameter("units", "metric");
      uriBuilder.appendQueryParameter("cnt", "7");
      Uri uri = uriBuilder.build();

      URL url = new URL(uri.toString());

      // Create the request to OpenWeatherMap, and open the connection
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();

      // Read the input stream into a String
      InputStream inputStream = urlConnection.getInputStream();
      StringBuffer buffer = new StringBuffer();
      if (inputStream == null)
      {
        // Nothing to do.
        forecastJsonStr = null;
      }
      reader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = reader.readLine()) != null)
      {
        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
        // But it does make debugging a *lot* easier if you print out the completed
        // buffer for debugging.
        buffer.append(line + "\n");
      }

      if (buffer.length() == 0)
      {
        // Stream was empty.  No point in parsing.
        forecastJsonStr = null;
      }
      forecastJsonStr = buffer.toString();
    }
    catch (IOException e)
    {
      Log.e(GetForecastAsyncTask.class.getName(), "Error ", e);
      // If the code didn't successfully get the weather data, there's no point in attempting
      // to parse it.
      forecastJsonStr = null;
    }
    finally
    {
      if (urlConnection != null)
      {
        urlConnection.disconnect();
      }
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (final IOException e)
        {
          Log.e(GetForecastAsyncTask.class.getName(), "Error closing stream", e);
        }
      }
    }

    try
    {
      if (forecastJsonStr != null)
      {
        return new JSONForecastDecoder().parseForecast(forecastJsonStr);
      }
    }
    catch (JSONException e)
    {
      Log.e(GetForecastAsyncTask.class.getName(), "Error parsing JSON data", e);
    }
    return new ArrayList<ForecastData>();
  }
}
