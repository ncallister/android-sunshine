package au.com.iwsoftware.sunshine.app;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.iwsoftware.sunshine.app.forecast.ForecastData;
import au.com.iwsoftware.sunshine.app.forecast.ForecastDataCursorLoader;
import au.com.iwsoftware.sunshine.app.forecast.ForecastDataLoaderListener;
import au.com.iwsoftware.sunshine.app.forecast.ForecastRenderer;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements ForecastDataLoaderListener
{
  private Map<String, Drawable> art = null;
  private ShareActionProvider shareActionProvider = null;
  private ForecastData forecast = null;
  private View rootView;
  private ForecastDataCursorLoader loader;

  public DetailFragment()
  {
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    super.setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    Log.v(DetailFragment.class.getSimpleName(), "onCreateView");
    rootView = inflater.inflate(R.layout.fragment_detail, container, false);

    initArt();
    clearForecast(rootView);

    Uri forecastUri = getActivity().getIntent().getData();
    Log.d(DetailFragment.class.getName(), "URI: " + forecast);

    loader = new ForecastDataCursorLoader(getContext(), getLoaderManager(), forecastUri, this);
    loader.initLoader();

    return rootView;
  }

  private void initArt()
  {
    if (art == null)
    {
      art = new HashMap<>();
      Resources res = getResources();
      art.put("clear", res.getDrawable(R.drawable.art_clear));
      art.put("clouds", res.getDrawable(R.drawable.art_clouds));
      art.put("fog", res.getDrawable(R.drawable.art_fog));
      art.put("light clouds", res.getDrawable(R.drawable.art_light_clouds));
      art.put("light rain", res.getDrawable(R.drawable.art_light_rain));
      art.put("snow", res.getDrawable(R.drawable.art_snow));
      art.put("storm", res.getDrawable(R.drawable.art_storm));
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    Log.v(DetailFragment.class.getSimpleName(), "onCreateOptionsMenu");
    // Inflate the menu; this adds items to the action bar if it is present.
    inflater.inflate(R.menu.menu_detail_fragment, menu);

    shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(
        menu.findItem(R.id.action_share));

    updateShare();
  }

  private void updateShare()
  {
    if (shareActionProvider != null && forecast != null)
    {
      ForecastRenderer renderer = ForecastRenderer.getRenderer(getActivity());

      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      shareIntent.setType("text/plain");
      shareIntent.putExtra(Intent.EXTRA_TEXT,
                           renderer.renderSummary(getActivity(), forecast, 1) + " #SunshineApp");
      shareActionProvider.setShareIntent(shareIntent);
    }
  }

  private void displayForecast(ForecastData data, View rootView)
  {
    ForecastRenderer renderer = ForecastRenderer.getRenderer(getActivity());

    SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE");
    SimpleDateFormat date = new SimpleDateFormat("MMMM dd");
    ((TextView) rootView.findViewById(R.id.textview_day_of_week)).setText(dayOfWeek.format(new Date(
        data.getTimestamp())));
    ((TextView) rootView.findViewById(R.id.textview_date)).setText(date.format(new Date(
        data.getTimestamp())));
    ((TextView) rootView.findViewById(R.id.textview_max)).setText(
        renderer.getMaximumTemperature(getActivity(), data, 0));
    ((TextView) rootView.findViewById(R.id.textview_min)).setText(
        renderer.getMinimumTemperature(getActivity(), data, 0));
    ((TextView) rootView.findViewById(R.id.textview_description)).setText(
        data.getWeatherDescription());
    ((TextView) rootView.findViewById(R.id.textview_humidity)).setText(getString(
        R.string.format_humidity, data.getHumidity()));
    ((TextView) rootView.findViewById(R.id.textview_pressure)).setText(getString(
        R.string.format_pressure, data.getPressure()));
    ((TextView) rootView.findViewById(R.id.textview_wind)).setText(getString(
        R.string.format_wind, data.getWindSpeed(), getWindDirection(data.getWindDir())));

    ((ImageView) rootView.findViewById(R.id.imageview_icon)).setImageDrawable(
        art.get(data.getWeatherDescription().toLowerCase()));
  }

  private void clearForecast(View rootView)
  {
    ((TextView) rootView.findViewById(R.id.textview_day_of_week)).setText("");
    ((TextView) rootView.findViewById(R.id.textview_date)).setText("");
    ((TextView) rootView.findViewById(R.id.textview_max)).setText("");
    ((TextView) rootView.findViewById(R.id.textview_min)).setText("");
    ((TextView) rootView.findViewById(R.id.textview_description)).setText("");
    ((TextView) rootView.findViewById(R.id.textview_humidity)).setText("");
    ((TextView) rootView.findViewById(R.id.textview_pressure)).setText("");
    ((TextView) rootView.findViewById(R.id.textview_wind)).setText("");

    ((ImageView) rootView.findViewById(R.id.imageview_icon)).setImageDrawable(null);
  }

  private String getWindDirection(int degrees)
  {
    String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
    return directions[(int) Math.round((((double) degrees % 360) / 45))];
  }

  @Override
  public void forecastDataLoaded(List<ForecastData> data)
  {
    forecast = data.get(0);
    displayForecast(forecast, rootView);

    updateShare();
  }

  @Override
  public void forecastDataReset()
  {
    clearForecast(rootView);
  }
}
