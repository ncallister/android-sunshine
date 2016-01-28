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
package au.com.iwsoftware.sunshine.app.openweather;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import au.com.iwsoftware.sunshine.app.data.WeatherContract;
import au.com.iwsoftware.sunshine.app.location.Location;

public class TestGetForecastAsyncTask extends AndroidTestCase
{
  static final String ADD_LOCATION_SETTING = "Sunnydale, CA";
  static final String ADD_LOCATION_CITY = "Sunnydale";
  static final double ADD_LOCATION_LAT = 34.425833;
  static final double ADD_LOCATION_LON = -119.714167;

  // Since we want each test to start with a clean slate, run deleteAllRecords
  // in setUp (called by the test runner before each test).
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    deleteAllRecordsFromProvider();
  }

  @TargetApi(11)
  public void testAddLocation()
  {
    // start from a clean state
    getContext().getContentResolver().delete(WeatherContract.LocationEntry.CONTENT_URI,
                                             WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +
                                                 " = ?",
                                             new String[]{ADD_LOCATION_SETTING});

    GetForecastAsyncTask fwt = new GetForecastAsyncTask(getContext().getContentResolver(), null);
    Location location = new Location();
    location.setLocationSetting(ADD_LOCATION_SETTING);
    location.setCityName(ADD_LOCATION_CITY);
    location.setLatitude(ADD_LOCATION_LAT);
    location.setLongitude(ADD_LOCATION_LON);
    fwt.syncLocationToDatabase(location);

    long locationId = location.getDatabaseID();

        // does addLocation return a valid record ID?
    assertFalse("Error: addLocation returned an invalid ID on insert",
                locationId == -1);

    // test all this twice
    for (int i = 0; i < 2; i++)
    {
      // does the ID point to our location?
      Cursor locationCursor = getContext().getContentResolver().query(
          WeatherContract.LocationEntry.CONTENT_URI,
          new String[]{
              WeatherContract.LocationEntry._ID,
              WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
              WeatherContract.LocationEntry.COLUMN_CITY_NAME,
              WeatherContract.LocationEntry.COLUMN_COORD_LAT,
              WeatherContract.LocationEntry.COLUMN_COORD_LONG
          },
          WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
          new String[]{ADD_LOCATION_SETTING},
          null);

      // these match the indices of the projection
      if (locationCursor.moveToFirst())
      {
        assertEquals("Error: the queried value of locationId does not match the returned value" +
                         "from addLocation", locationId, locationCursor.getLong(0));
        assertEquals("Error: the queried value of location setting is incorrect",
                     ADD_LOCATION_SETTING, locationCursor.getString(1));
        assertEquals("Error: the queried value of location city is incorrect",
                     ADD_LOCATION_CITY, locationCursor.getString(2));
        assertEquals("Error: the queried value of latitude is incorrect",
                     ADD_LOCATION_LAT, locationCursor.getDouble(3));
        assertEquals("Error: the queried value of longitude is incorrect",
                     ADD_LOCATION_LON, locationCursor.getDouble(4));
      }
      else
      {
        fail("Error: the id you used to query returned an empty cursor");
      }

      // there should be no more records
      assertFalse("Error: there should be only one record returned from a location query",
                  locationCursor.moveToNext());

      // add the location againLocation location = new Location();
      Location duplicate = new Location();
      duplicate.setLocationSetting(ADD_LOCATION_SETTING);
      duplicate.setCityName(ADD_LOCATION_CITY);
      duplicate.setLatitude(ADD_LOCATION_LAT);
      duplicate.setLongitude(ADD_LOCATION_LON);
      fwt.syncLocationToDatabase(duplicate);
      long newLocationId = duplicate.getDatabaseID();

      assertEquals("Error: inserting a location again should return the same ID",
                   locationId, newLocationId);
    }
    // reset our state back to normal
    getContext().getContentResolver().delete(WeatherContract.LocationEntry.CONTENT_URI,
                                             WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +
                                                 " = ?",
                                             new String[]{ADD_LOCATION_SETTING});

    // clean up the test so that other tests can use the content provider
    getContext().getContentResolver().
        acquireContentProviderClient(WeatherContract.LocationEntry.CONTENT_URI).
        getLocalContentProvider().shutdown();
  }

  public void deleteAllRecordsFromProvider()
  {
    mContext.getContentResolver().delete(
        WeatherContract.WeatherEntry.CONTENT_URI,
        null,
        null
    );
    mContext.getContentResolver().delete(
        WeatherContract.LocationEntry.CONTENT_URI,
        null,
        null
    );

    Cursor cursor = mContext.getContentResolver().query(
        WeatherContract.WeatherEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );
    assertEquals("Error: Records not deleted from Weather table during delete", 0,
                 cursor.getCount());
//    cursor.close();

    cursor = mContext.getContentResolver().query(
        WeatherContract.LocationEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );
    assertEquals("Error: Records not deleted from Location table during delete", 0,
                 cursor.getCount());
//    cursor.close();
  }
}
