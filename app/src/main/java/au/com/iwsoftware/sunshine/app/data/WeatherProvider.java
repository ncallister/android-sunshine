/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.com.iwsoftware.sunshine.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class WeatherProvider extends ContentProvider
{
  // The URI Matcher used by this content provider.
  private static final UriMatcher sUriMatcher = buildUriMatcher();
  private WeatherDbHelper mOpenHelper;

  static final int WEATHER = 100;
  static final int WEATHER_WITH_ID = 103;
  static final int WEATHER_WITH_LOCATION = 101;
  static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
  static final int LOCATION = 300;
  static final int LOCATION_WITH_ID = 301;

  private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
  private static final SQLiteQueryBuilder sLocationQueryBuilder;

  static
  {
    sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

    //This is an inner join which looks like
    //weather INNER JOIN location ON weather.location_id = location._id
    sWeatherByLocationSettingQueryBuilder.setTables(
        WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
            WeatherContract.LocationEntry.TABLE_NAME +
            " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
            "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
            " = " + WeatherContract.LocationEntry.TABLE_NAME +
            "." + WeatherContract.LocationEntry._ID);

    sLocationQueryBuilder = new SQLiteQueryBuilder();
    sLocationQueryBuilder.setTables(WeatherContract.LocationEntry.TABLE_NAME);
  }

  // No limitations on selection
  private static final String sSelectAll = null;

  //date >= ?
  private static final String sStartDateSelection =
      WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";

  //weather._id >= ?
  private static final String sWeatherIDSelection =
      WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID + " = ? ";

  //location.location_setting = ?
  private static final String sLocationSettingSelection =
      WeatherContract.LocationEntry.TABLE_NAME +
          "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

  //location._id = ?
  private static final String sLocationIDSelection =
      WeatherContract.LocationEntry.TABLE_NAME +
          "." + WeatherContract.LocationEntry._ID + " = ? ";

  //location.location_setting = ? AND date >= ?
  private static final String sLocationSettingWithStartDateSelection =
      WeatherContract.LocationEntry.TABLE_NAME +
          "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
          WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";

  //location.location_setting = ? AND date = ?
  private static final String sLocationSettingAndDaySelection =
      WeatherContract.LocationEntry.TABLE_NAME +
          "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
          WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";

  private Cursor getAllLocations(Uri uri, String[] projection, String sortOrder)
  {
    return sLocationQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                                       projection,
                                       sSelectAll,
                                       null,
                                       null,
                                       null,
                                       sortOrder
    );
  }

  private Cursor getLocationById(Uri uri, String[] projection, String sortOrder)
  {
    long locationId = WeatherContract.LocationEntry.getLocationIdFromUri(uri);
    return sLocationQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                                       projection,
                                       sLocationIDSelection,
                                       new String[]{String.valueOf(locationId)},
                                       null,
                                       null,
                                       sortOrder);
  }

  private Cursor getAllWeather(Uri uri, String[] projection, String sortOrder)
  {
    long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

    String[] selectionArgs;
    String selection;

    if (startDate == 0)
    {
      selection = sSelectAll;
      selectionArgs = null;
    }
    else
    {
      selection = sStartDateSelection;
      selectionArgs = new String[]{Long.toString(startDate)};
    }

    return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                                                       projection,
                                                       selection,
                                                       selectionArgs,
                                                       null,
                                                       null,
                                                       sortOrder
    );
  }

  private Cursor getWeatherById(Uri uri, String[] projection, String sortOrder)
  {
    long weatherId = WeatherContract.WeatherEntry.getWeatherIdFromUri(uri);
    return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                                                       projection,
                                                       sWeatherIDSelection,
                                                       new String[]{String.valueOf(weatherId)},
                                                       null,
                                                       null,
                                                       sortOrder);
  }

  private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder)
  {
    String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
    long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

    String[] selectionArgs;
    String selection;

    if (startDate == 0)
    {
      selection = sLocationSettingSelection;
      selectionArgs = new String[]{locationSetting};
    }
    else
    {
      selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
      selection = sLocationSettingWithStartDateSelection;
    }

    return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                                                       projection,
                                                       selection,
                                                       selectionArgs,
                                                       null,
                                                       null,
                                                       sortOrder
    );
  }

  private Cursor getWeatherByLocationSettingAndDate(Uri uri, String[] projection, String sortOrder)
  {
    String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
    long date = WeatherContract.WeatherEntry.getDateFromUri(uri);

    return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                                                       projection,
                                                       sLocationSettingAndDaySelection,
                                                       new String[]
                                                       {
                                                           locationSetting,
                                                           Long.toString(date)
                                                       },
                                                       null,
                                                       null,
                                                       sortOrder
    );
  }

  static UriMatcher buildUriMatcher()
  {
    UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Weather URI patterns
    matcher.addURI(WeatherContract.CONTENT_AUTHORITY,
                   WeatherContract.PATH_WEATHER,
                   WEATHER);
    matcher.addURI(WeatherContract.CONTENT_AUTHORITY,
                   WeatherContract.PATH_WEATHER + "/#",
                   WEATHER_WITH_ID);
    matcher.addURI(WeatherContract.CONTENT_AUTHORITY,
                   WeatherContract.PATH_WEATHER + "/*",
                   WEATHER_WITH_LOCATION);
    matcher.addURI(WeatherContract.CONTENT_AUTHORITY,
                   WeatherContract.PATH_WEATHER + "/*/#",
                   WEATHER_WITH_LOCATION_AND_DATE);

    // Location URI pattern
    matcher.addURI(WeatherContract.CONTENT_AUTHORITY,
                   WeatherContract.PATH_LOCATION,
                   LOCATION);
    matcher.addURI(WeatherContract.CONTENT_AUTHORITY,
                   WeatherContract.PATH_LOCATION + "/#",
                   LOCATION_WITH_ID);

    return matcher;
  }

  /*
      Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
      here.
   */
  @Override
  public boolean onCreate()
  {
    mOpenHelper = new WeatherDbHelper(getContext());
    return true;
  }

  @Override
  public String getType(Uri uri)
  {
    // Use the Uri Matcher to determine what kind of URI this is.
    final int match = sUriMatcher.match(uri);

    switch (match)
    {
      case WEATHER_WITH_LOCATION_AND_DATE:
        // With location and date we get a single item
        return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
      case WEATHER_WITH_LOCATION:
        // With only the location we will get a directory
        return WeatherContract.WeatherEntry.CONTENT_TYPE;
      case WEATHER:
        // Weather directory
        return WeatherContract.WeatherEntry.CONTENT_TYPE;
      case LOCATION:
        // Location directory
        return WeatherContract.LocationEntry.CONTENT_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                      String sortOrder)
  {
    // Here's the switch statement that, given a URI, will determine what kind of request it is,
    // and query the database accordingly.
    Cursor retCursor;
    switch (sUriMatcher.match(uri))
    {
      // "weather/*/*"
      case WEATHER_WITH_LOCATION_AND_DATE:
      {
        retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
        break;
      }
      // "weather/*"
      case WEATHER_WITH_LOCATION:
      {
        retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
        break;
      }
      // "weather/#"
      case WEATHER_WITH_ID:
      {
        retCursor = getWeatherById(uri, projection, sortOrder);
        break;
      }
      // "weather"
      case WEATHER:
      {
        retCursor = getAllWeather(uri, projection, sortOrder);
        break;
      }
      // "location"
      case LOCATION:
      {
        retCursor = getAllLocations(uri, projection, sortOrder);
        break;
      }
      // "location/#"
      case LOCATION_WITH_ID:
      {
        retCursor = getLocationById(uri, projection, sortOrder);
        break;
      }

      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
    return retCursor;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values)
  {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    Uri returnUri;

    switch (match)
    {
      case WEATHER:
      {
        normalizeDate(values);
        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
        if (_id > 0)
        {
          returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
        }
        else
        {
          throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        break;
      }
      case LOCATION :
      {
        long _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
        if (_id > 0)
        {
          returnUri = WeatherContract.LocationEntry.buildLocationUri(_id);
        }
        else
        {
          throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        break;
      }
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return returnUri;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs)
  {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsModified = 0;

    if (selection == null)
    {
      // "1" deletes all rows like null does however it returns the count of rows deleted.
      selection = "1";
    }

    switch (match)
    {
      case WEATHER:
      {
        rowsModified = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
        break;
      }
      case LOCATION :
      {
        rowsModified = db.delete(WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
        break;
      }
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    if (rowsModified > 0)
    {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsModified;
  }

  private void normalizeDate(ContentValues values)
  {
    // normalize the date value
    if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE))
    {
      long dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
      values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(
          dateValue));
    }
  }

  @Override
  public int update(
      Uri uri, ContentValues values, String selection, String[] selectionArgs)
  {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsModified = 0;

    switch (match)
    {
      case WEATHER:
      {
        rowsModified = db.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection,
                                 selectionArgs);
        break;
      }
      case LOCATION :
      {
        rowsModified = db.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection,
                                 selectionArgs);
        break;
      }
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    if (rowsModified > 0)
    {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsModified;
  }

  @Override
  public int bulkInsert(Uri uri, ContentValues[] values)
  {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    switch (match)
    {
      case WEATHER:
        db.beginTransaction();
        int returnCount = 0;
        try
        {
          for (ContentValues value : values)
          {
            normalizeDate(value);
            long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
            if (_id != -1)
            {
              returnCount++;
            }
          }
          db.setTransactionSuccessful();
        }
        finally
        {
          db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
      default:
        return super.bulkInsert(uri, values);
    }
  }

  // You do not need to call this method. This is a method specifically to assist the testing
  // framework in running smoothly. You can read more at:
  // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
  @Override
  @TargetApi(11)
  public void shutdown()
  {
    mOpenHelper.close();
    super.shutdown();
  }
}