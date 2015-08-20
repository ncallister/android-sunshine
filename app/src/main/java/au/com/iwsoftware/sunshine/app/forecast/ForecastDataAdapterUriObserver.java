package au.com.iwsoftware.sunshine.app.forecast;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.util.List;

/**
 *
 */
public class ForecastDataAdapterUriObserver extends ContentObserver
{
  private ContentResolver contentResolver;
  private ForecastDataAdapter adapter;
  private WeatherDbForecastDataCodec codec;

  public ForecastDataAdapterUriObserver(ContentResolver contentResolver, Handler handler,
                                        ForecastDataAdapter adapter)
  {
    super(handler);
    this.contentResolver = contentResolver;
    this.adapter = adapter;
    this.codec = new WeatherDbForecastDataCodec();
  }

  public void registerUri(Uri forecastUri)
  {
    contentResolver.registerContentObserver(forecastUri, true, this);
  }

  public void unregister()
  {
    contentResolver.unregisterContentObserver(this);
  }

  @Override
  public void onChange(boolean selfChange, Uri uri)
  {
    updateAdapter(uri);
  }

  public void updateAdapter(Uri forecastUri)
  {
    Cursor cursor = contentResolver.query(forecastUri, null, null, null, null);
    try
    {
      List<ForecastData> allData = codec.decodeAll(cursor, contentResolver);
      adapter.clear();
      for (ForecastData data : allData)
      {
        adapter.add(data);
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
}
