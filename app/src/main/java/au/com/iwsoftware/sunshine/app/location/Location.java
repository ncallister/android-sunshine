package au.com.iwsoftware.sunshine.app.location;

/**
 *
 */
public class Location
{
  private String locationSetting;
  private String cityName;
  private double latitude;
  private double longitude;
  private long databaseID;

  public static final long DATABASE_ID_UNKNOWN = -1;

  public Location()
  {
    this.databaseID = DATABASE_ID_UNKNOWN;
  }

  public Location(Location clone)
  {
    this.locationSetting = clone.getLocationSetting();
    this.cityName = clone.getCityName();
    this.latitude = clone.getLatitude();
    this.longitude = clone.getLongitude();
    this.databaseID = clone.getDatabaseID();
  }

  public String getLocationSetting()
  {
    return locationSetting;
  }

  public void setLocationSetting(String locationSetting)
  {
    this.locationSetting = locationSetting;
  }

  public String getCityName()
  {
    return cityName;
  }

  public void setCityName(String cityName)
  {
    this.cityName = cityName;
  }

  public double getLatitude()
  {
    return latitude;
  }

  public void setLatitude(double latitude)
  {
    this.latitude = latitude;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public void setLongitude(double longitude)
  {
    this.longitude = longitude;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof Location))
    {
      return false;
    }

    Location location = (Location) o;

    return !(getLocationSetting() != null ? !getLocationSetting().equals(
        location.getLocationSetting()) :
        location.getLocationSetting() != null);

  }

  @Override
  public int hashCode()
  {
    return getLocationSetting() != null ? getLocationSetting().hashCode() : 0;
  }

  public long getDatabaseID()
  {
    return databaseID;
  }

  public void setDatabaseID(long databaseID)
  {
    this.databaseID = databaseID;
  }
}
