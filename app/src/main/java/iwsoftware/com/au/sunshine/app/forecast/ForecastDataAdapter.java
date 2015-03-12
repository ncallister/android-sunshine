package iwsoftware.com.au.sunshine.app.forecast;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 *
 */
public class ForecastDataAdapter extends BaseAdapter
{
  private List<ForecastData> data;

  private LayoutInflater inflater;
  private int fieldId;
  private int resource;
  private Context context;

  public ForecastDataAdapter(Context context, int resource, int textViewResourceId, List<ForecastData> objects)
  {
    init(context, resource, textViewResourceId, objects);
  }

  private void init(Context context, int resource, int textViewResourceId, List<ForecastData> objects)
  {
    inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    data = objects;
    fieldId = textViewResourceId;
    this.resource = resource;
    this.context = context;
  }

  @Override
  public int getCount()
  {
    return data.size();
  }

  @Override
  public ForecastData getItem(int position)
  {
    return data.get(position);
  }

  @Override
  public long getItemId(int position)
  {
    return position;
  }

  public void clear()
  {
    data.clear();
    notifyDataSetChanged();
  }

  public void add(ForecastData forecast)
  {
    data.add(forecast);
    notifyDataSetChanged();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    return createViewFromResource(position, convertView, parent, resource);
  }

  private View createViewFromResource(int position, View convertView, ViewGroup parent,
                                      int useResource)
  {
    View view;
    TextView text;

    if (convertView == null)
    {
      view = inflater.inflate(useResource, parent, false);
    }
    else
    {
      view = convertView;
    }

    try
    {
      if (fieldId == 0)
      {
        //  If no custom field is assigned, assume the whole resource is a TextView
        text = (TextView) view;
      }
      else
      {
        //  Otherwise, find the TextView field within the layout
        text = (TextView) view.findViewById(fieldId);
      }
    }
    catch (ClassCastException e)
    {
      Log.e(ForecastDataAdapter.class.getSimpleName(), "You must supply a resource ID for a TextView");
      throw new IllegalStateException(
          "ForecastDataAdapter requires the resource ID to be a TextView", e);
    }

    ForecastData item = getItem(position);
    ForecastRenderer renderer = ForecastRenderer.getRenderer(this.context);
    text.setText(renderer.renderSummary(this.context, item, 1));

    return view;
  }
}
