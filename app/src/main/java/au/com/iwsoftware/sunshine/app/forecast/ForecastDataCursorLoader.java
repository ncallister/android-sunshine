package au.com.iwsoftware.sunshine.app.forecast;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 *
 */
public class ForecastDataCursorLoader implements LoaderManager.LoaderCallbacks<Cursor>
{
  private static final int CURSOR_LOADER_ID = 1;

  private Context context;
  private LoaderManager manager;
  private Uri contentSource;
  private ForecastDataLoaderListener listener;
  private WeatherDbForecastDataCodec codec = new WeatherDbForecastDataCodec();

  public ForecastDataCursorLoader(Context context, LoaderManager manager, Uri contentSource,
                                  ForecastDataLoaderListener listener)
  {
    this.context = context;
    this.manager = manager;
    this.contentSource = contentSource;
    this.listener = listener;
  }

  public void initLoader()
  {
    Log.d(ForecastDataCursorLoader.class.getName(), "initLoader for " + contentSource);
    manager.restartLoader(CURSOR_LOADER_ID, null, this);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    Log.d(ForecastDataCursorLoader.class.getName(), "onCreateLoader for " + contentSource);
    return new CursorLoader(context, contentSource, WeatherDbForecastDataCodec.getFullProjection(),
                            null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data)
  {
    Log.d(ForecastDataCursorLoader.class.getName(), "onLoadFinished for " + contentSource);
    listener.forecastDataLoaded(codec.decodeAll(data, context.getContentResolver()));
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader)
  {
    Log.d(ForecastDataCursorLoader.class.getName(), "onLoaderReset for " + contentSource);
    listener.forecastDataReset();
  }
}
