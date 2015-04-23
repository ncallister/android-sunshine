package au.com.iwsoftware.sunshine.app.forecast;

import android.os.Bundle;

/**
 *
 */
public class BundleForecastDataCodec
{
  public static final String INTENT_EXTRA_FORECAST_DATA = "forecast-data";


//  private long timestamp;
//  private String weatherDescription;
//  private double maxKelvin;
//  private double minKelvin;
//  private double pressure;
//  private int humidity;
//  private double windSpeed;
//  private int windDir;


  public static final String LABEL_TIMESTAMP = "timestamp";
  public static final String LABEL_DESCRIPTION = "description";
  public static final String LABEL_MAX_KELVIN = "max-kelvin";
  public static final String LABEL_MIN_KELVIN = "min-kelvin";
  public static final String LABEL_PRESSURE = "pressure";
  public static final String LABEL_HUMIDITY = "humidity";
  public static final String LABEL_WIND_SPEED = "wind-speed";
  public static final String LABEL_WIND_DIRECTION = "wind-direction";

  public Bundle encode(ForecastData data)
  {
    Bundle bundle = new Bundle();

    bundle.putLong(LABEL_TIMESTAMP, data.getTimestamp());
    bundle.putString(LABEL_DESCRIPTION, data.getWeatherDescription());
    bundle.putDouble(LABEL_MAX_KELVIN, data.getMaxKelvin());
    bundle.putDouble(LABEL_MIN_KELVIN, data.getMinKelvin());
    bundle.putDouble(LABEL_PRESSURE, data.getPressure());
    bundle.putInt(LABEL_HUMIDITY, data.getHumidity());
    bundle.putDouble(LABEL_WIND_SPEED, data.getWindSpeed());
    bundle.putInt(LABEL_WIND_DIRECTION, data.getWindDir());

    return bundle;
  }

  public ForecastData decode(Bundle bundle)
  {
    ForecastData data = new ForecastData();

    data.setTimestamp(bundle.getLong(LABEL_TIMESTAMP));
    data.setWeatherDescription(bundle.getString(LABEL_DESCRIPTION));
    data.setMaxKelvin(bundle.getDouble(LABEL_MAX_KELVIN));
    data.setMinKelvin(bundle.getDouble(LABEL_MIN_KELVIN));
    data.setPressure(bundle.getDouble(LABEL_PRESSURE));
    data.setHumidity(bundle.getInt(LABEL_HUMIDITY));
    data.setWindSpeed(bundle.getDouble(LABEL_WIND_SPEED));
    data.setWindDir(bundle.getInt(LABEL_WIND_DIRECTION));

    return data;
  }
}
