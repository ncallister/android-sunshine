package iwsoftware.com.au.sunshine.app;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import iwsoftware.com.au.sunshine.app.forecast.BundleForecastDataCodec;
import iwsoftware.com.au.sunshine.app.forecast.ForecastData;
import iwsoftware.com.au.sunshine.app.forecast.ForecastRenderer;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment
{

  private Map<String, Drawable> art = null;

  public DetailFragment()
  {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

    initArt();

    BundleForecastDataCodec forecastCodec = new BundleForecastDataCodec();
    ForecastData data = forecastCodec.decode(getActivity().getIntent().getBundleExtra(
        BundleForecastDataCodec.INTENT_EXTRA_FORECAST_DATA));
    displayForecast(data, rootView);

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

  private String getWindDirection(int degrees)
  {
    String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
    return directions[(int) Math.round((((double) degrees % 360) / 45))];
  }
}
