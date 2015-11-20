package metropolia.fi.suondbubbles.dialogFragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import metropolia.fi.suondbubbles.R;

public class VolumeControlFragment extends DialogFragment{




    public interface VolumeListener {
        void onVolumeChange(int volume);
        void onOkClick(DialogFragment dialog);
    }

    private final String DEBUG_TAG = "VolumeControlFragment";
    private int volume;

    private VolumeListener mListener;
    private SeekBar volume_bar;
    private TextView current_volume_value;
    private StringBuilder temp;

    public void setVolume(int volume) {
        this.volume = volume;
    }


    public void setmListener(VolumeListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_volume, null);
        builder.setView(layout);

        Button ok_button =(Button)layout.findViewById(R.id.volume_dialog_ok_button);
        volume_bar = (SeekBar)layout.findViewById(R.id.voleme_seek_bar);
        current_volume_value = (TextView)layout.findViewById(R.id.current_volume);


        volume_bar.setProgress(volume);
        temp = new StringBuilder();
        temp.append(volume);
        current_volume_value.setText(temp);



        volume_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    temp.setLength(0);
                    temp.append(progress);
                    current_volume_value.setText(temp);
                    mListener.onVolumeChange(volume_bar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "clicked ok");

                mListener.onOkClick(VolumeControlFragment.this);
            }
        });

        return builder.create();
    }

}
