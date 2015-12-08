package metropolia.fi.suondbubbles.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import pl.droidsonroids.gif.GifImageView;

public class ServerFilesArrayAdapter extends ArrayAdapter<ServerFile> {
    private Activity context;
    private ArrayList<ServerFile> serverFileArray;
    // current view objects from getView method (given by position)
    private TextView name, soundLength;
    private GifImageView gifImageView;
    private ProgressBar progressBarWithPause;

    public ServerFilesArrayAdapter(Activity context, ArrayList <ServerFile> serverFileArray) {
        super(context, R.layout.grid_element, serverFileArray);
        this.context = context;
        this.serverFileArray = serverFileArray;

    }

    private void setChildViews(View gridElement){
        name = (TextView) gridElement.findViewById(R.id.grid_name);
        soundLength = (TextView) gridElement.findViewById(R.id.grid_sound_length);
        gifImageView = (GifImageView)gridElement.findViewById(R.id.gifView);
        progressBarWithPause = (ProgressBar)gridElement.findViewById(R.id.progressBar);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridElement = inflater.inflate(R.layout.grid_element, parent, false);

        setChildViews(gridElement);

        name.setText(serverFileArray.get(position).getTitle());
        if(serverFileArray.get(position).getLength()!= 0) {
            soundLength.setText("" + serverFileArray.get(position).getLength() + " sec");
        }
        else {
            soundLength.setText("");
        }
        return gridElement;
    }



    public boolean switchToProgressBarWithPause(View gridElement){
        if(gridElement != null){
            setChildViews(gridElement);
            name.setVisibility(View.GONE);
            soundLength.setVisibility(View.GONE);
            gifImageView.setVisibility(View.GONE);
            progressBarWithPause.setVisibility(View.VISIBLE);
            return true;
        }else
            return false;
    }

    public boolean switchToGifImage(View gridElement){
        if(gridElement != null){
            setChildViews(gridElement);
            name.setVisibility(View.GONE);
            soundLength.setVisibility(View.GONE);
            gifImageView.setVisibility(View.VISIBLE);
            progressBarWithPause.setVisibility(View.GONE);
            return true;
        }else
            return false;
    }

    public boolean backToNormal(View gridElement){
        if(gridElement != null){
            setChildViews(gridElement);
            name.setVisibility(View.VISIBLE);
            soundLength.setVisibility(View.VISIBLE);
            gifImageView.setVisibility(View.GONE);
            progressBarWithPause.setVisibility(View.GONE);
            return true;
        }else
            return false;
    }

    public ProgressBar getProgressBarWithPause() {
        return progressBarWithPause;
    }

    public TextView getName() {
        return name;
    }
}
