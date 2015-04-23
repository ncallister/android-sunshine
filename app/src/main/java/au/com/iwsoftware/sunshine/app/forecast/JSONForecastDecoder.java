package au.com.iwsoftware.sunshine.app.forecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class JSONForecastDecoder
{
  private JSONForecastDataCodec dataDecoder;

  public static final String LABEL_FORECAST_COUNT = "cnt";
  public static final String LABEL_FORECAST_LIST = "list";

  public JSONForecastDecoder()
  {
    this.dataDecoder = new JSONForecastDataCodec();
  }

  public List<ForecastData> parseForecast(String json) throws JSONException
  {
    JSONObject root = new JSONObject(json);
    int forecastCount = root.getInt(LABEL_FORECAST_COUNT);
    List<ForecastData> data = new ArrayList<ForecastData>(forecastCount);
    JSONArray list = root.getJSONArray(LABEL_FORECAST_LIST);
    for (int i = 0 ; i < list.length() ; ++i)
    {
      data.add(dataDecoder.parseForecast(list.getJSONObject(i)));
    }
    return data;
  }
}
