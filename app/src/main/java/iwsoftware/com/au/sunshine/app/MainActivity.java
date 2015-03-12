package iwsoftware.com.au.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null)
    {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.container, new ForecastFragment())
          .commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  private void launchMap()
  {
    String location = PreferenceManager.getDefaultSharedPreferences(this).getString(
        getString(R.string.pref_key_location), getString(R.string.pref_default_location));
    Intent mapIntent = new Intent(Intent.ACTION_VIEW);
    Uri locationUri = Uri.parse(String.format("geo:0,0?q=%s", location));
    mapIntent.setData(locationUri);
    if (mapIntent.resolveActivity(getPackageManager()) != null)
    {
      startActivity(mapIntent);
    }
    else
    {
      Toast.makeText(this, getString(R.string.error_no_maps), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings)
    {
      startActivity(new Intent(this, SettingsActivity.class));
      return true;
    }
    else if (id == R.id.action_view_map)
    {
      launchMap();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

}
