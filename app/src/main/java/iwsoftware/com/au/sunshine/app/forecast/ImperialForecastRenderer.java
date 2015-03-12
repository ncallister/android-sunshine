package iwsoftware.com.au.sunshine.app.forecast;

import android.content.Context;

import iwsoftware.com.au.sunshine.app.R;

/**
 *
 */
public class ImperialForecastRenderer extends ForecastRenderer
{
  public static final double FAHRENHEIT_FACTOR = 1.8;
  public static final double FAHRENHEIT_OFFSET = 32.0;

  @Override
  public String getMaximumTemperature(Context context, ForecastData data, int decimalPlaces)
  {
    return String.format(context.getString(R.string.format_temperature_imperial, decimalPlaces),
                         celsiusToFahrenheit(data.getMaxCelsius()));
  }

  @Override
  public String getMinimumTemperature(Context context, ForecastData data, int decimalPlaces)
  {
    return String.format(context.getString(R.string.format_temperature_imperial, decimalPlaces),
                         celsiusToFahrenheit(data.getMinCelsius()));
  }

  private double celsiusToFahrenheit(double celsius)
  {
    return (celsius * FAHRENHEIT_FACTOR) + FAHRENHEIT_OFFSET;
  }
}
