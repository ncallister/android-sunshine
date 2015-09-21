package au.com.iwsoftware.sunshine.app.forecast;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

/**
 *
 */
public class ForecastDataCursorLoader implements LoaderManager.LoaderCallbacks<Cursor>
{
  private static final int CURSOR_LOADER_ID = 1;

  private Context context;
  private LoaderManager manager;
  private Uri contentSource;
  private ForecastDataAdapter adapter;

  public ForecastDataCursorLoader(Context context, LoaderManager manager, Uri contentSource,
                                  ForecastDataAdapter adapter)
  {
    this.context = context;
    this.manager = manager;
    this.contentSource = contentSource;
    this.adapter = adapter;
  }

  public void initLoader()
  {
    manager.initLoader(CURSOR_LOADER_ID, null, this);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    return new CursorLoader(context, contentSource, WeatherDbForecastDataCodec.getFullProjection(),
                            null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data)
  {
    adapter.updateFromCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader)
  {
    adapter.clear();
  }
}
