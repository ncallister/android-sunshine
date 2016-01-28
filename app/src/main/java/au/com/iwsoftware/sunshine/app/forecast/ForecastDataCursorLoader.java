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
 * The ForecastDataCursorLoader acts as both a link and an interpretive layer between a view
 * and a database provider which provides forecast information.
 *
 * Each loader loads only a single URI and reports to only a single listener. If multiple views
 * wish to display the same information then each should use its own loader although the
 * LoaderManager will ensure that only a single CursorLoader is instantiated.
 *
 * The ForecastDataCursorLoader then interprets the result by parsing the returned cursor into
 * a collection of {@link ForecastData} objects that can be provided to the view via the
 * {@link ForecastDataLoaderListener} interface.
 */
public class ForecastDataCursorLoader implements LoaderManager.LoaderCallbacks<Cursor>
{
  private Context context;
  private LoaderManager manager;
  private Uri contentSourceURI;
  private ForecastDataLoaderListener listener;
  private WeatherDbForecastDataCodec codec = new WeatherDbForecastDataCodec();

  public ForecastDataCursorLoader(Context context, LoaderManager manager, Uri contentSourceURI,
                                  ForecastDataLoaderListener listener)
  {
    this.context = context;
    this.manager = manager;
    this.contentSourceURI = contentSourceURI;
    this.listener = listener;
  }

  /**
   * Initialise the loader to begin performing the data loading.
   */
  public void initLoader()
  {
    Log.d(ForecastDataCursorLoader.class.getName(), "initLoader for " + contentSourceURI);
    manager.initLoader(contentSourceURI.hashCode(), null, this);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    Log.d(ForecastDataCursorLoader.class.getName(), "onCreateLoader for " + contentSourceURI);
    return new CursorLoader(context, contentSourceURI, WeatherDbForecastDataCodec.getFullProjection(),
                            null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data)
  {
    Log.d(ForecastDataCursorLoader.class.getName(), "onLoadFinished for " + contentSourceURI);
    listener.forecastDataLoaded(codec.decodeAll(data, context.getContentResolver()));
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader)
  {
    Log.d(ForecastDataCursorLoader.class.getName(), "onLoaderReset for " + contentSourceURI);
    listener.forecastDataReset();
  }
}
