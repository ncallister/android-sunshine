package au.com.iwsoftware.sunshine.app.openweather;

/**
 * The icons used by OpenWeatherMap.
 * <p>
 * See http://openweathermap.org/weather-conditions for more details.
 */
public enum OpenWeatherIcon
{
  CLEAR("01d", "Clear Sky"),
  FEW_CLOUDS("02d", "Few Clouds"),
  SCATTERED_CLOUDS("03d", "Scattered Clouds"),
  BROKEN_CLOUDS("04d", "Broken Clouds"),
  DRIZZLE("09d", "Shower Rain"),
  RAIN("10d", "Rain"),
  THUNDERSTORM("11d", "Thunderstorm"),
  SNOW("13d", "Snow"),
  MIST("50d", "Mist"),
  ;


  private String code;
  private String description;

  OpenWeatherIcon(String code, String description)
  {
    this.code = code;
    this.description = description;
  }

  public String getCode()
  {
    return code;
  }

  public String getDescription()
  {
    return description;
  }

  public static OpenWeatherIcon getIconByCode(String code)
  {
    for (OpenWeatherIcon icon : OpenWeatherIcon.values())
    {
      if (icon.getCode().equals(code))
      {
        return icon;
      }
    }

    // I know, I should make a new exception type. Couldn't find an existing one that matched.
    throw new RuntimeException("There is no listed icon with code " + code);
  }
}
