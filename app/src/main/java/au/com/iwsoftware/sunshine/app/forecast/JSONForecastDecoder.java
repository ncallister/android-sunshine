package au.com.iwsoftware.sunshine.app.forecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import au.com.iwsoftware.sunshine.app.location.JSONLocationDecoder;
import au.com.iwsoftware.sunshine.app.location.Location;

/**
 *
 */
public class JSONForecastDecoder
{
  private JSONForecastDataCodec dataDecoder;
  private JSONLocationDecoder locationDecoder;

  public static final String LABEL_FORECAST_COUNT = "cnt";
  public static final String LABEL_FORECAST_LIST = "list";

  public JSONForecastDecoder()
  {
    this.dataDecoder = new JSONForecastDataCodec();
    this.locationDecoder = new JSONLocationDecoder();
  }

  public List<ForecastData> parseForecast(String json, String locationSetting) throws JSONException
  {
    Location location = locationDecoder.parseLocation(json, locationSetting);

    JSONObject root = new JSONObject(json);
    int forecastCount = root.getInt(LABEL_FORECAST_COUNT);
    List<ForecastData> data = new ArrayList<ForecastData>(forecastCount);
    JSONArray list = root.getJSONArray(LABEL_FORECAST_LIST);
    for (int i = 0 ; i < list.length() ; ++i)
    {
      ForecastData forecast = dataDecoder.parseForecast(list.getJSONObject(i));
      forecast.setLocation(location);
      data.add(forecast);
    }
    return data;
  }
}
