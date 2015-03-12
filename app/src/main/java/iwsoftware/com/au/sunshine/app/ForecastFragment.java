package iwsoftware.com.au.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import java.util.List;

import iwsoftware.com.au.sunshine.app.forecast.BundleForecastDataCodec;
import iwsoftware.com.au.sunshine.app.forecast.ForecastData;
import iwsoftware.com.au.sunshine.app.forecast.ForecastDataAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment
{
  private View rootView;
  private ForecastDataAdapter forecastDataAdapter;

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
    Log.i(ForecastFragment.class.getName(), "update forecast");
    ForecastFetcher fetcher = new ForecastFetcher();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    fetcher.execute(preferences.getString(getString(R.string.pref_key_location),
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

  private class ForecastFetcher extends GetForecastAsyncTask
  {
    @Override
    protected void onPostExecute(List<ForecastData> forecasts)
    {
      forecastDataAdapter.clear();
      for (ForecastData data : forecasts)
      {
        forecastDataAdapter.add(data);
      }
    }
  }
}
