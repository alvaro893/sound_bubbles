package metropolia.fi.suondbubbles.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;

/**
 * Created by alvarob on 22.10.2015.
 */
public class ServerFilesArrayAdapter extends ArrayAdapter<ServerFile> {
    Activity context;
    ArrayList<ServerFile> serverFileArray;
    // current view objects from getView method (given by position)
    TextView name, category, soundType;
    ImageButton btn_pause;

    public ServerFilesArrayAdapter(Activity context, ArrayList <ServerFile> serverFileArray) {
        super(context, R.layout.grid_element, serverFileArray);
        this.context = context;
        this.serverFileArray = serverFileArray;

    }

    private void setChildViews(View rowView){
        name = (TextView) rowView.findViewById(R.id.grid_name);
        category = (TextView) rowView.findViewById(R.id.grid_category);
        soundType = (TextView) rowView.findViewById(R.id.grid_sound_type);
        btn_pause = (ImageButton) rowView.findViewById(R.id.pause_button);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.grid_element, parent, false);

        setChildViews(rowView);

        name.setText(serverFileArray.get(position).getTitle());
        category.setText(serverFileArray.get(position).getFileExtension());
        soundType.setText("" + serverFileArray.get(position).getLenght() + " seconds");
        return rowView;
    }

    public boolean switchToButtonPause(View rowView){
        if(rowView != null){
            setChildViews(rowView);
            name.setVisibility(View.GONE);
            category.setVisibility(View.GONE);
            soundType.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
            return true;
        }else
            return false;
    }

    public boolean backToNormal(View rowView){
        if(rowView != null){
            setChildViews(rowView);
            name.setVisibility(View.VISIBLE);
            category.setVisibility(View.VISIBLE);
            soundType.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
            return true;
        }else
            return false;
    }

    public ImageButton getBtn_pause() {
        return btn_pause;
    }

    public TextView getName() {
        return name;
    }
}
