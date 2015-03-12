package iwsoftware.com.au.sunshine.app.forecast;

import java.text.SimpleDateFormat;

/**
 *
 */
public class ForecastData
{
  private long timestamp;
  private String weatherDescription;
  private double maxKelvin;
  private double minKelvin;
  private double pressure;
  private int humidity;
  private double windSpeed;
  private int windDir;

  private static final double ZERO_C_IN_K = 273.15;
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MMM dd");

  public long getTimestamp()
  {
    return timestamp;
  }

  public void setTimestamp(long timestamp)
  {
    this.timestamp = timestamp;
  }

  public String getWeatherDescription()
  {
    return weatherDescription;
  }

  public void setWeatherDescription(String weatherDescription)
  {
    this.weatherDescription = weatherDescription;
  }

  public double getMaxKelvin()
  {
    return maxKelvin;
  }

  public void setMaxKelvin(double maxKelvin)
  {
    this.maxKelvin = maxKelvin;
  }

  public double getMinKelvin()
  {
    return minKelvin;
  }

  public void setMinKelvin(double minKelvin)
  {
    this.minKelvin = minKelvin;
  }

  public void setMaxCelsius(double maxCelsius)
  {
    this.maxKelvin = celsiusToKelvin(maxCelsius);
  }

  public void setMinCelsius(double minCelsius)
  {
    this.minKelvin = celsiusToKelvin(minCelsius);
  }

  public double getMaxCelsius()
  {
    return kelvinToCelsius(maxKelvin);
  }

  public double getMinCelsius()
  {
    return kelvinToCelsius(minKelvin);
  }

  private double kelvinToCelsius(double kelvin)
  {
    return kelvin - ZERO_C_IN_K;
  }

  private double celsiusToKelvin(double kelvin)
  {
    return kelvin + ZERO_C_IN_K;
  }

  public String toString()
  {
    return String.format("%s - %s - %.2f°C / %.2f°C",
                         DATE_FORMAT.format(timestamp),
                         weatherDescription,
                         kelvinToCelsius(maxKelvin),
                         kelvinToCelsius(minKelvin));
  }

  public double getPressure()
  {
    return pressure;
  }

  public void setPressure(double pressure)
  {
    this.pressure = pressure;
  }

  public int getHumidity()
  {
    return humidity;
  }

  public void setHumidity(int humidity)
  {
    this.humidity = humidity;
  }

  public double getWindSpeed()
  {
    return windSpeed;
  }

  public void setWindSpeed(double windSpeed)
  {
    this.windSpeed = windSpeed;
  }

  public int getWindDir()
  {
    return windDir;
  }

  public void setWindDir(int windDir)
  {
    this.windDir = windDir;
  }
}
