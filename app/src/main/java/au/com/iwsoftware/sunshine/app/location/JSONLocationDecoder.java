package au.com.iwsoftware.sunshine.app.location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class JSONLocationDecoder
{
  private JSONLocationCodec dataDecoder;

  // Location information
  final String LABEL_CITY = "city";

  public static final String LABEL_FORECAST_COUNT = "cnt";
  public static final String LABEL_FORECAST_LIST = "list";

  public JSONLocationDecoder()
  {
    this.dataDecoder = new JSONLocationCodec();
  }

  public Location parseLocation(String json, String locationSetting) throws JSONException
  {
    JSONObject root = new JSONObject(json);

    JSONObject city = root.getJSONObject(LABEL_CITY);

    Location location = dataDecoder.decode(city);
    location.setLocationSetting(locationSetting);

    return location;
  }
}
