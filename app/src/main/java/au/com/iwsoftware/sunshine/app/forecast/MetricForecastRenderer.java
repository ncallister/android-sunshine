package au.com.iwsoftware.sunshine.app.forecast;

import android.content.Context;

import au.com.iwsoftware.sunshine.app.R;

/**
 *
 */
public class MetricForecastRenderer extends ForecastRenderer
{
  @Override
  public String getMaximumTemperature(Context context, ForecastData data, int decimalPlaces)
  {
    return String.format(context.getString(R.string.format_temperature_metric, decimalPlaces),
                         data.getMaxCelsius());
  }

  @Override
  public String getMinimumTemperature(Context context, ForecastData data, int decimalPlaces)
  {
    return String.format(context.getString(R.string.format_temperature_metric, decimalPlaces),
                         data.getMinCelsius());
  }
}
