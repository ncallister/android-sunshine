package au.com.iwsoftware.sunshine.app.openweather;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
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

import au.com.iwsoftware.sunshine.app.data.WeatherContract;
import au.com.iwsoftware.sunshine.app.forecast.ForecastData;
import au.com.iwsoftware.sunshine.app.forecast.JSONForecastDecoder;
import au.com.iwsoftware.sunshine.app.forecast.WeatherDbForecastDataCodec;
import au.com.iwsoftware.sunshine.app.location.Location;
import au.com.iwsoftware.sunshine.app.location.LocationDbLocationCodec;

/**
 *
 */
public class GetForecastAsyncTask extends AsyncTask<String, Void, List<ForecastData>>
{
  private ContentResolver contentResolver;

  private JSONForecastDecoder forecastDecoder;

  private WeatherDbForecastDataCodec forecastCodec;
  private LocationDbLocationCodec locationCodec;

  private String APIKEY = null;

  public GetForecastAsyncTask(ContentResolver context, String APIKEY)
  {
    this.contentResolver = context;

    this.forecastDecoder = new JSONForecastDecoder();

    this.forecastCodec = new WeatherDbForecastDataCodec();
    this.locationCodec = new LocationDbLocationCodec();
    this.APIKEY = APIKEY;
  }

  @Override
  protected List<ForecastData> doInBackground(String... params)
  {
    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;

    String locationSetting = TextUtils.join(",", params);

    try
    {
      // Construct the URL for the OpenWeatherMap query
      // Possible parameters are available at OWM's forecast API page, at
      // http://openweathermap.org/API#forecast
      Uri.Builder uriBuilder = new Uri.Builder();
      uriBuilder.scheme("http");
      uriBuilder.authority("api.openweathermap.org");
      uriBuilder.path("data/2.5/forecast/daily");
      uriBuilder.appendQueryParameter("q", locationSetting);
      uriBuilder.appendQueryParameter("mode", "json");
      uriBuilder.appendQueryParameter("units", "metric");
      uriBuilder.appendQueryParameter("cnt", "7");
      uriBuilder.appendQueryParameter("APPID", APIKEY);
      Uri uri = uriBuilder.build();

      Log.d(GetForecastAsyncTask.class.getName(), "Loading forecasts: (" + locationSetting + ") -> " + uri.toString());
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
      Log.e(GetForecastAsyncTask.class.getSimpleName(), "Error ", e);
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
          Log.e(GetForecastAsyncTask.class.getSimpleName(), "Error closing stream", e);
        }
      }
    }

    try
    {
      if (forecastJsonStr != null)
      {
        Log.d(GetForecastAsyncTask.class.getName(), "Forecast data loaded for: " + locationSetting);
        return forecastDecoder.parseForecast(forecastJsonStr, locationSetting);
      }
    }
    catch (JSONException e)
    {
      Log.e(GetForecastAsyncTask.class.getSimpleName(), "Error parsing JSON data", e);
    }
    return new ArrayList<>();
  }

  void syncLocationToDatabase(Location location)
  {
    Cursor cursor = contentResolver.query(
        WeatherContract.LocationEntry.buildLocationWithSettingUri(location.getLocationSetting()),
        new String[]
            {
                WeatherContract.LocationEntry._ID,
            },
        null,
        null,
        null);
    try
    {
      if (cursor.moveToFirst())
      {
        location.setDatabaseID(cursor.getLong(cursor.getColumnIndex(
            WeatherContract.LocationEntry._ID)));
      }
      else
      {
        Uri locationUri = contentResolver.insert(
            WeatherContract.LocationEntry.CONTENT_URI, locationCodec.encode(location));

        long locationRowId = ContentUris.parseId(locationUri);
        location.setDatabaseID(locationRowId);
      }
    }
    finally
    {
      if (cursor != null)
      {
        cursor.close();
      }
    }
  }

  @Override
  protected void onPostExecute(List<ForecastData> forecasts)
  {
    for (ForecastData forecast : forecasts)
    {
      syncLocationToDatabase(forecast.getLocation());
    }

    // Delete all current forecasts
    contentResolver.delete(
        WeatherContract.WeatherEntry.CONTENT_URI,
        null,
        null
    );

    // Add in the new ones
    contentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,
                                            forecastCodec.encodeAll(forecasts));
  }
}
