package metropolia.fi.suondbubbles.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import metropolia.fi.suondbubbles.R;

/**
 * Created by Alvaro on 03/11/2015.
 */
public class CategoriesAdapter extends ArrayAdapter<String> {
    Activity context;
    String[] categoriesArray;
    // current view objects from getView method (given by position)
    TextView category;

    public CategoriesAdapter(Activity context, String[] categoriesArray) {
        super(context, R.layout.grid_element, categoriesArray);
        this.context = context;
        this.categoriesArray = categoriesArray;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridElement = inflater.inflate(R.layout.grid_element, parent, false);
        category = (TextView) gridElement.findViewById(R.id.grid_name);
        category.setText(categoriesArray[position]);
        // delete placeholders
        ((TextView) gridElement.findViewById(R.id.grid_category)).setText("");
        ((TextView) gridElement.findViewById(R.id.grid_sound_type)).setText("");
        return gridElement;
    }
}