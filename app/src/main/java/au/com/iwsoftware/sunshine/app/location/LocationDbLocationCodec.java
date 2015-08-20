package au.com.iwsoftware.sunshine.app.location;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.iwsoftware.sunshine.app.data.WeatherContract;

/**
 *
 */
public class LocationDbLocationCodec
{
  private static final Map<String, FieldCodec> fields = new HashMap<>();

  static
  {
    fields.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
               new FieldCodec()
               {
                 @Override
                 public void encode(Location location, ContentValues values)
                 {
                   values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                              location.getLocationSetting());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, Location location)
                 {
                   location.setLocationSetting(dbCursor.getString(columnIndex));
                 }
               });
    fields.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,
               new FieldCodec()
               {
                 @Override
                 public void encode(Location location, ContentValues values)
                 {
                   values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                              location.getCityName());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, Location location)
                 {
                   location.setCityName(dbCursor.getString(columnIndex));
                 }
               });
    fields.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,
               new FieldCodec()
               {
                 @Override
                 public void encode(Location location, ContentValues values)
                 {
                   values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                              location.getLatitude());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, Location location)
                 {
                   location.setLatitude(dbCursor.getDouble(columnIndex));
                 }
               });
    fields.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,
               new FieldCodec()
               {
                 @Override
                 public void encode(Location location, ContentValues values)
                 {
                   values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,
                              location.getLongitude());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, Location location)
                 {
                   location.setLongitude(dbCursor.getDouble(columnIndex));
                 }
               });
    fields.put(WeatherContract.LocationEntry._ID,
               new FieldCodec()
               {
                 @Override
                 public void encode(Location location, ContentValues values)
                 {
                   // If we don't know the ID yet (probably not yet in database) then don't try to
                   // push the ID.
                   if (location.getDatabaseID() != Location.DATABASE_ID_UNKNOWN)
                   {
                     values.put(WeatherContract.LocationEntry._ID,
                                location.getDatabaseID());
                   }
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, Location location)
                 {
                   location.setDatabaseID(dbCursor.getLong(columnIndex));
                 }
               });
  }

  public ContentValues encode(Location location)
  {
    ContentValues values = new ContentValues();
    for (String key : fields.keySet())
    {
      fields.get(key).encode(location, values);
    }
    return values;
  }

  public ContentValues[] encodeAll(List<Location> allLocations)
  {
    ContentValues[] values = new ContentValues[allLocations.size()];

    int i = 0;
    for (Location location : allLocations)
    {
      values[i] = encode(location);
      i++;
    }

    return values;
  }

  public Location decode(Cursor dbCursor)
  {
    Location location = new Location();
    for (int i = 0 ; i < dbCursor.getColumnCount() ; ++i)
    {
      if (fields.containsKey(dbCursor.getColumnName(i)))
      {
        fields.get(dbCursor.getColumnName(i)).decode(dbCursor, i, location);
      }
    }
    return location;
  }

  public List<Location> decodeAll(Cursor dbCursor)
  {
    List<Location> allLocations = new ArrayList<>();

    dbCursor.moveToPosition(-1);
    while (dbCursor.moveToNext())
    {
      allLocations.add(decode(dbCursor));
    }

    return allLocations;
  }

  private static abstract class FieldCodec
  {
    public abstract void encode(Location location, ContentValues values);
    public abstract void decode(Cursor dbCursor, int columnIndex, Location location);
  }
}
