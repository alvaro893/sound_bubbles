package metropolia.fi.suondbubbles.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import metropolia.fi.suondbubbles.R;


/**
 * Created by alvarob on 22.10.2015.
 */
public class RawFilesAdapter extends ArrayAdapter<Field> {
    Activity context;
    ArrayList<Field> rawFiles;

    public RawFilesAdapter(Activity context, ArrayList<Field> rawFiles) {
        super(context, R.layout.row_layout, rawFiles);
        this.context = context;
        this.rawFiles = rawFiles;
    }

    public View getView(int position, View convertView, ViewGroup
            parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView name;
        name = (TextView) rowView.findViewById(R.id.row_name);

        name.setText(rawFiles.get(position).getName());
        return rowView;
    }
}
