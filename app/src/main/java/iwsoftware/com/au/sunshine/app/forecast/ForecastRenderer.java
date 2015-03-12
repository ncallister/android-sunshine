package iwsoftware.com.au.sunshine.app.forecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;

import iwsoftware.com.au.sunshine.app.R;

/**
 *
 */
public abstract class ForecastRenderer
{
  public static ForecastRenderer getRenderer(Context context)
  {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String units = prefs.getString(context.getString(R.string.pref_key_units),
                                   context.getString(R.string.pref_default_units));

    if (units.equals(context.getString(R.string.pref_entry_units_imperial)))
    {
      return new ImperialForecastRenderer();
    }
    else if (units.equals(context.getString(R.string.pref_entry_units_metric)))
    {
      return new MetricForecastRenderer();
    }

    throw new IllegalArgumentException("Unable to create renderer for units: " + units);
  }

  public String renderSummary(Context context, ForecastData data, int decimalPlaces)
  {
    return context.getString(R.string.format_summary,
                             new SimpleDateFormat(
                                 context.getString(R.string.format_summary_date)).format(
                                 data.getTimestamp()),
                             data.getWeatherDescription(),
                             getMaximumTemperature(context, data, decimalPlaces),
                             getMinimumTemperature(context, data, decimalPlaces));
  }

  public abstract String getMaximumTemperature(Context context, ForecastData data, int decimalPlaces);

  public abstract String getMinimumTemperature(Context context, ForecastData data, int decimalPlaces);
}
