package metropolia.fi.suondbubbles.dialogFragments;


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

    private ConfirmDialogListener confirmDialogListener;

    public void setConfirmDialogListener(ConfirmDialogListener confirmDialogListener) {
        this.confirmDialogListener = confirmDialogListener;
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
                confirmDialogListener.onDialogCancelClick(ConfirmDialogFragment.this);
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "clicked yes");
                confirmDialogListener.onDialogYesClick(ConfirmDialogFragment.this);

            }
        });


        return builder.create();
    }
}
