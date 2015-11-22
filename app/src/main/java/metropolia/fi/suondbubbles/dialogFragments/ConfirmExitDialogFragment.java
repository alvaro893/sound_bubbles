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


public class ConfirmExitDialogFragment extends DialogFragment{

    private final String DEBUG_TAG = "ConfirmExitDialog";


    public interface ConfirmExitDialogListener {
        void onDialogYesClick(DialogFragment dialog);
        void onDialogCancelClick(DialogFragment dialog);

    }

    private  ConfirmExitDialogListener confirmExitDialogListener;


    public void setConfirmExitDialogListener(ConfirmExitDialogListener confirmExitDialogListener) {
        this.confirmExitDialogListener = confirmExitDialogListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_confirm_exit, null);
        builder.setView(layout);

        Button cancel_button =(Button)layout.findViewById(R.id.btn_cancel);
        Button yes_button = (Button)layout.findViewById(R.id.btn_yes);


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "clicked cancel");
                confirmExitDialogListener.onDialogCancelClick(ConfirmExitDialogFragment.this);
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "clicked yes");
                confirmExitDialogListener.onDialogYesClick(ConfirmExitDialogFragment.this);

            }
        });


        return builder.create();
    }
}
