package au.com.iwsoftware.sunshine.app.forecast;

import java.util.List;

/**
 *
 */
public interface ForecastDataLoaderListener
{
  void forecastDataLoaded(List<ForecastData> data);
  void forecastDataReset();
}
