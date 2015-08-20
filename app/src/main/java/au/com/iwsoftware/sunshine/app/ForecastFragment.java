package au.com.iwsoftware.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import au.com.iwsoftware.sunshine.app.data.WeatherContract;
import au.com.iwsoftware.sunshine.app.forecast.BundleForecastDataCodec;
import au.com.iwsoftware.sunshine.app.forecast.ForecastData;
import au.com.iwsoftware.sunshine.app.forecast.ForecastDataAdapter;
import au.com.iwsoftware.sunshine.app.forecast.ForecastDataAdapterUriObserver;
import au.com.iwsoftware.sunshine.app.openweather.GetForecastAsyncTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment
{
  private View rootView;
  private ForecastDataAdapter forecastDataAdapter;
  private ForecastDataAdapterUriObserver forecastObserver;

  public ForecastFragment()
  {
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    super.setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    rootView = inflater.inflate(R.layout.fragment_forecast, container, false);

    forecastDataAdapter = new ForecastDataAdapter(getActivity(),
                                             R.layout.list_item_forecast,
                                             R.id.list_item_forecast_textview,
                                             new ArrayList<ForecastData>());
    ListView list = ((ListView) rootView.findViewById(R.id.listview_forecast));
    list.setAdapter(forecastDataAdapter);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id)
      {
        Intent displayDetailIntent = new Intent(getActivity(), DetailActivity.class);
        BundleForecastDataCodec codec = new BundleForecastDataCodec();
        ForecastData data = forecastDataAdapter.getItem(position);
        displayDetailIntent.putExtra(BundleForecastDataCodec.INTENT_EXTRA_FORECAST_DATA,
                                     codec.encode(data));
        getActivity().startActivity(displayDetailIntent);
      }
    });

    forecastObserver = new ForecastDataAdapterUriObserver(getContext().getContentResolver(),
                                                          new Handler(),
                                                          forecastDataAdapter);
    // Register to receive change events
    forecastObserver.registerUri(WeatherContract.WeatherEntry.CONTENT_URI);
    // Perform an initial load of the data from the DB
    forecastObserver.updateAdapter(WeatherContract.WeatherEntry.CONTENT_URI);

    return rootView;
  }

  @Override
  public void onStart()
  {
    super.onStart();
    updateForecast();
  }

  private void updateForecast()
  {
    Log.i(ForecastFragment.class.getSimpleName(), "update forecast");

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    new GetForecastAsyncTask(getContext().getContentResolver()).execute(
        preferences.getString(getString(R.string.pref_key_location),
                              getString(R.string.pref_default_location)));
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    inflater.inflate(R.menu.forecast_fragment_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_refresh)
    {
      updateForecast();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
