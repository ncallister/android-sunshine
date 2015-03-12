package iwsoftware.com.au.sunshine.app.forecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class JSONForecastDataCodec
{
  public static final String LABEL_DATE_TIME = "dt";
  public static final String LABEL_TEMPERATURE_OBJECT = "temp";
  public static final String LABEL_TEMPERATURE_MAX = "max";
  public static final String LABEL_TEMPERATURE_MIN = "min";
  public static final String LABEL_PRESSURE = "pressure";
  public static final String LABEL_HUMIDITY = "humidity";
  public static final String LABEL_WEATHER_OBJECT = "weather";
  public static final String LABEL_WEATHER_DESCRIPTION = "main";
  public static final String LABEL_WIND_SPEED = "speed";
  public static final String LABEL_WIND_DIRECTION = "deg";

  public ForecastData parseForecast(JSONObject forecast) throws JSONException
  {
    ForecastData data = new ForecastData();
    data.setTimestamp(TimeUnit.MILLISECONDS.convert(forecast.getLong(LABEL_DATE_TIME),
                                                    TimeUnit.SECONDS));
    data.setPressure(forecast.getDouble(LABEL_PRESSURE));
    data.setHumidity(forecast.getInt(LABEL_HUMIDITY));
    data.setWindSpeed(forecast.getDouble(LABEL_WIND_SPEED));
    data.setWindDir(forecast.getInt(LABEL_WIND_DIRECTION));

    parseTemperatureData(data, forecast.getJSONObject(LABEL_TEMPERATURE_OBJECT));
    parseWeatherData(data, forecast.getJSONArray(LABEL_WEATHER_OBJECT).getJSONObject(0));

    return data;
  }

  private void parseTemperatureData(ForecastData data, JSONObject temperature) throws JSONException
  {
    data.setMaxCelsius(temperature.getDouble(LABEL_TEMPERATURE_MAX));
    data.setMinCelsius(temperature.getDouble(LABEL_TEMPERATURE_MIN));
  }

  private void parseWeatherData(ForecastData data, JSONObject weather) throws JSONException
  {
    data.setWeatherDescription(weather.getString(LABEL_WEATHER_DESCRIPTION));
  }

  public JSONObject encodeForecast(ForecastData data) throws JSONException
  {
    JSONObject forecast = new JSONObject();

    forecast.put(LABEL_DATE_TIME, TimeUnit.SECONDS.convert(data.getTimestamp(),
                                                           TimeUnit.MILLISECONDS));
    forecast.put(LABEL_PRESSURE, data.getPressure());
    forecast.put(LABEL_HUMIDITY, data.getHumidity());
    forecast.put(LABEL_WIND_SPEED, data.getWindSpeed());
    forecast.put(LABEL_WIND_DIRECTION, data.getWindDir());

    JSONObject temperature = new JSONObject();
    encodeTemperatureData(data, temperature);
    forecast.put(LABEL_TEMPERATURE_OBJECT, temperature);

    JSONObject weather = new JSONObject();
    encodeWeatherData(data, weather);
    JSONArray array = new JSONArray();
    array.put(weather);
    forecast.put(LABEL_WEATHER_OBJECT, array);

    return forecast;
  }

  private void encodeTemperatureData(ForecastData data, JSONObject temperature) throws JSONException
  {
    temperature.put(LABEL_TEMPERATURE_MAX, data.getMaxCelsius());
    temperature.put(LABEL_TEMPERATURE_MIN, data.getMinCelsius());
  }

  private void encodeWeatherData(ForecastData data, JSONObject weather) throws JSONException
  {
    weather.put(LABEL_WEATHER_DESCRIPTION, data.getWeatherDescription());
  }
}
