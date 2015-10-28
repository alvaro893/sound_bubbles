package metropolia.fi.suondbubbles.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;

/**
 * Created by alvarob on 22.10.2015.
 */
public class ServerFilesArrayAdapter extends ArrayAdapter<ServerFile> {
    Activity context;
    ArrayList<ServerFile> serverFileArray;

    public ServerFilesArrayAdapter(Activity context, ArrayList <ServerFile> serverFileArray) {
        super(context, R.layout.row_layout, serverFileArray);
        this.context = context;
        this.serverFileArray = serverFileArray;


    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.grid_element, parent, false);
        TextView name, category, soundType;
        name = (TextView) rowView.findViewById(R.id.grid_name);
        category = (TextView) rowView.findViewById(R.id.grid_category);
        soundType = (TextView) rowView.findViewById(R.id.grid_sound_type);

        name.setText(serverFileArray.get(position).getTitle());
        category.setText(serverFileArray.get(position).getCategory());
        soundType.setText(serverFileArray.get(position).getSoundType());
        return rowView;
    }
}
