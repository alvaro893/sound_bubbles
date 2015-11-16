package metropolia.fi.suondbubbles.dialogFragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import metropolia.fi.suondbubbles.R;


public class ConfirmDialogFragment extends DialogFragment {

    private final String DEBUG_TAG = "ConfirmDialogFragment";


    public interface ConfirmDialogListener {
        void onDialogYesClick(DialogFragment dialog);
        void onDialogCancelClick(DialogFragment dialog);

    }

    ConfirmDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConfirmDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ConfirmDialogListener");
        }
    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_confirm, null);
        builder.setView(layout);

        Button cancel_button =(Button)layout.findViewById(R.id.btn_cancel);
        Button yes_button = (Button)layout.findViewById(R.id.btn_yes);


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "clicked cancel");
                mListener.onDialogCancelClick(ConfirmDialogFragment.this);
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "clicked yes");
                mListener.onDialogYesClick(ConfirmDialogFragment.this);

            }
        });


        return builder.create();
    }
}
