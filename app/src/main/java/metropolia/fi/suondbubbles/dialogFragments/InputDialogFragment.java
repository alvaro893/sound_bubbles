package metropolia.fi.suondbubbles.dialogFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import metropolia.fi.suondbubbles.R;


public class InputDialogFragment extends DialogFragment {

    private final String DEBUG_TAG = getClass().getSimpleName();


    public interface InputDialogListener {
        void onDialogYesClick(String title, String category, DialogFragment dialog);
        void onDialogCancelClick(DialogFragment dialog);

    }

    InputDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (InputDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement InputDialogListener");
        }
    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View layout = inflater.inflate(R.layout.fragment_input_dialog, null);
        builder.setView(layout);

        Button cancel_button =(Button)layout.findViewById(R.id.btn_cancel);
        Button yes_button = (Button)layout.findViewById(R.id.btn_yes);


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "clicked cancel");
                mListener.onDialogCancelClick(InputDialogFragment.this);
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "clicked yes");

                View rootView = v.getRootView();
                EditText input = (EditText)rootView.findViewById(R.id.insert_dialog_et);
                RadioGroup radioGroup = (RadioGroup)rootView.findViewById(R.id.radio_group);

                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton rbutton = (RadioButton) radioGroup.findViewById(id);
                String title = input.getText().toString();
                String category = rbutton.getText().toString();

                Log.d(DEBUG_TAG, title + "-" + category);
                if(input.getText().toString().isEmpty()){
                    input.setError("Field is empty");
                }else{
                    mListener.onDialogYesClick(title, category, InputDialogFragment.this);
                }
            }
        });


        return builder.create();
    }
}
