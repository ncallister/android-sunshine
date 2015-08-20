package au.com.iwsoftware.sunshine.app.location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class JSONLocationCodec
{
  final String CITY_LABEL_NAME = "name";
  final String CITY_LABEL_COORD = "coord";

  // Location coordinate
  final String COORD_LABEL_LATITUDE = "lat";
  final String COORD_LABEL_LONGITUDE = "lon";

  public JSONObject encode(Location location) throws JSONException
  {
    JSONObject city = new JSONObject();

    city.put(CITY_LABEL_NAME, location.getCityName());

    JSONObject coord = new JSONObject();
    city.put(CITY_LABEL_COORD, coord);

    coord.put(COORD_LABEL_LONGITUDE, location.getLongitude());
    coord.put(COORD_LABEL_LATITUDE, location.getLatitude());

    return city;
  }

  public Location decode(JSONObject city) throws JSONException
  {
    Location location = new Location();

    location.setCityName(city.getString(CITY_LABEL_NAME));

    JSONObject coord = city.getJSONObject(CITY_LABEL_COORD);
    location.setLatitude(coord.getDouble(COORD_LABEL_LATITUDE));
    location.setLongitude(coord.getDouble(COORD_LABEL_LONGITUDE));

    return location;
  }
}
