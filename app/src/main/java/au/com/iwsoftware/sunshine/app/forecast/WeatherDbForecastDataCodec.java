package au.com.iwsoftware.sunshine.app.forecast;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.iwsoftware.sunshine.app.data.WeatherContract;
import au.com.iwsoftware.sunshine.app.location.LocationDbLocationCodec;
import au.com.iwsoftware.sunshine.app.openweather.OpenWeatherIcon;

/**
 *
 */
public class WeatherDbForecastDataCodec
{
  private static final Map<String, FieldCodec> fields = new HashMap<>();

  public static final List<String> PROJECTION = new ArrayList<String>();

  static
  {
    PROJECTION.add(WeatherContract.WeatherEntry.TABLE_NAME + "." +
                       WeatherContract.WeatherEntry._ID + " AS " +
                       WeatherContract.WeatherEntry._ID_EXPLICIT);
    fields.put(WeatherContract.WeatherEntry._ID_EXPLICIT,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   if (data.getDatabaseId() != ForecastData.DATABASE_ID_UNKNOWN)
                   {
                     values.put(WeatherContract.WeatherEntry._ID, data.getDatabaseId());
                   }
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setDatabaseId(dbCursor.getInt(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_DATE);
    fields.put(WeatherContract.WeatherEntry.COLUMN_DATE,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_DATE, data.getTimestamp());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setTimestamp(dbCursor.getLong(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
    fields.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                              data.getWeatherDescription());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setWeatherDescription(dbCursor.getString(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
    fields.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, data.getMaxKelvin());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setMaxKelvin(dbCursor.getDouble(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
    fields.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, data.getMinKelvin());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setMinKelvin(dbCursor.getDouble(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
    fields.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, data.getPressure());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setPressure(dbCursor.getDouble(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
    fields.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                              (double) data.getHumidity());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setHumidity((int) dbCursor.getDouble(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
    fields.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, data.getWindSpeed());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setWindSpeed((int) dbCursor.getDouble(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_DEGREES);
    fields.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, data.getWindDir());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setWindDir(dbCursor.getInt(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_LOC_KEY);
    fields.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
                              data.getLocation().getDatabaseID());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   long locationId = dbCursor.getLong(columnIndex);
                   Cursor locationCursor = contentResolver.query(
                       WeatherContract.LocationEntry.buildLocationUri(locationId),
                       null, null, null, null);
                   try
                   {
                     if (locationCursor.moveToFirst())
                     {
                       data.setLocation(locationCodec.decode(locationCursor));
                     }
                   }
                   finally
                   {
                     if (locationCursor != null)
                     {
                       locationCursor.close();
                     }
                   }
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
    fields.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   values.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, data.getWeatherId());
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   data.setWeatherId(dbCursor.getInt(columnIndex));
                 }
               });

    PROJECTION.add(WeatherContract.WeatherEntry.COLUMN_WEATHER_ICON);
    fields.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ICON,
               new FieldCodec()
               {
                 @Override
                 public void encode(ForecastData data, ContentValues values)
                 {
                   if (data.getWeatherIcon() != null)
                   {
                     values.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ICON,
                                data.getWeatherIcon().name());
                   }
                 }

                 @Override
                 public void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                    ContentResolver contentResolver)
                 {
                   if (!dbCursor.isNull(columnIndex))
                   {
                     data.setWeatherIcon(OpenWeatherIcon.valueOf(dbCursor.getString(columnIndex)));
                   }
                 }
               });
  }

  private static LocationDbLocationCodec locationCodec = new LocationDbLocationCodec();

  public ContentValues encode(ForecastData data)
  {
    ContentValues values = new ContentValues();
    for (String key : fields.keySet())
    {
      fields.get(key).encode(data, values);
    }
    return values;
  }

  public ContentValues[] encodeAll(List<ForecastData> allData)
  {
    ContentValues[] values = new ContentValues[allData.size()];

    int i = 0;
    for (ForecastData data : allData)
    {
      values[i] = encode(data);
      i++;
    }

    return values;
  }

  public ForecastData decode(Cursor dbCursor, ContentResolver contentResolver)
  {
    ForecastData data = new ForecastData();
    for (int i = 0; i < dbCursor.getColumnCount(); ++i)
    {
      String column = dbCursor.getColumnName(i);
      if (fields.containsKey(column))
      {
        fields.get(column).decode(dbCursor, i, data, contentResolver);
      }
    }
    return data;
  }

  public List<ForecastData> decodeAll(Cursor dbCursor, ContentResolver contentResolver)
  {
    List<ForecastData> allData = new ArrayList<>();

    dbCursor.moveToPosition(-1);
    while (dbCursor.moveToNext())
    {
      allData.add(decode(dbCursor, contentResolver));
    }

    return allData;
  }

  public static String[] getFullProjection()
  {
    List<String> projection = new ArrayList<String>();
    projection.addAll(WeatherDbForecastDataCodec.PROJECTION);
    projection.addAll(LocationDbLocationCodec.PROJECTION);
    String[] fullProjection = new String[projection.size()];
    return projection.toArray(fullProjection);
  }

  private static abstract class FieldCodec
  {
    public abstract void encode(ForecastData data, ContentValues values);

    public abstract void decode(Cursor dbCursor, int columnIndex, ForecastData data,
                                ContentResolver contentResolver);
  }
}
