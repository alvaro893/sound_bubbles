package metropolia.fi.suondbubbles.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by alvarob on 22.10.2015.
 */
public class ServerFilesArrayAdapter extends ArrayAdapter<ServerFile> {
    private Activity context;
    private ArrayList<ServerFile> serverFileArray;
    // current view objects from getView method (given by position)
    private TextView name, category, soundType;
    private ImageButton btn_pause;
    private GifImageView gifImageView;
    private ProgressBar progressBarWithPause;

    public ServerFilesArrayAdapter(Activity context, ArrayList <ServerFile> serverFileArray) {
        super(context, R.layout.grid_element, serverFileArray);
        this.context = context;
        this.serverFileArray = serverFileArray;

    }

    private void setChildViews(View gridElement){
        name = (TextView) gridElement.findViewById(R.id.grid_name);
        category = (TextView) gridElement.findViewById(R.id.grid_category);
        soundType = (TextView) gridElement.findViewById(R.id.grid_sound_type);
        btn_pause = (ImageButton) gridElement.findViewById(R.id.pause_button);
        gifImageView = (GifImageView)gridElement.findViewById(R.id.gifView);
        progressBarWithPause = (ProgressBar)gridElement.findViewById(R.id.progressBar);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridElement = inflater.inflate(R.layout.grid_element, parent, false);

        setChildViews(gridElement);

        name.setText(serverFileArray.get(position).getTitle());
        category.setText(serverFileArray.get(position).getFileExtension());
        if(serverFileArray.get(position).getLength()!= 0) {
            soundType.setText("" + serverFileArray.get(position).getLength() + " seconds");
        }
        else {
            soundType.setText("");
        }
        return gridElement;
    }

    public boolean switchToButtonPause(View gridElement){
        if(gridElement != null){
            setChildViews(gridElement);
            name.setVisibility(View.GONE);
            category.setVisibility(View.GONE);
            soundType.setVisibility(View.GONE);
            gifImageView.setVisibility(View.GONE);
            progressBarWithPause.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
            return true;
        }else
            return false;
    }

    public boolean switchToProgressBarWithPause(View gridElement){
        if(gridElement != null){
            setChildViews(gridElement);
            name.setVisibility(View.GONE);
            category.setVisibility(View.GONE);
            soundType.setVisibility(View.GONE);
            gifImageView.setVisibility(View.GONE);
            btn_pause.setVisibility(View.GONE);
            progressBarWithPause.setVisibility(View.VISIBLE);
            return true;
        }else
            return false;
    }

    public boolean switchToGifImage(View gridElement){
        if(gridElement != null){
            setChildViews(gridElement);
            name.setVisibility(View.GONE);
            category.setVisibility(View.GONE);
            soundType.setVisibility(View.GONE);
            gifImageView.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
            progressBarWithPause.setVisibility(View.GONE);
            return true;
        }else
            return false;
    }

    public boolean backToNormal(View gridElement){
        if(gridElement != null){
            setChildViews(gridElement);
            name.setVisibility(View.VISIBLE);
            category.setVisibility(View.VISIBLE);
            soundType.setVisibility(View.VISIBLE);
            gifImageView.setVisibility(View.GONE);
            btn_pause.setVisibility(View.GONE);
            progressBarWithPause.setVisibility(View.GONE);
            return true;
        }else
            return false;
    }

    public ImageButton getBtn_pause() {
        return btn_pause;
    }

    public ProgressBar getProgressBarWithPause() {
        return progressBarWithPause;
    }

    public TextView getName() {
        return name;
    }
}
