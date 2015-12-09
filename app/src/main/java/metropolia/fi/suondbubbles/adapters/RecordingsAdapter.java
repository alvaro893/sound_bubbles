package metropolia.fi.suondbubbles.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.dialogFragments.ConfirmDialogFragment;
import metropolia.fi.suondbubbles.helper.Record;

/**
 * Created by alvarob on 8.12.2015.
 */
public class RecordingsAdapter extends ArrayAdapter<Record> {
    Activity context;
    ArrayList<Record> recordsArray;
    ConfirmDialogFragment confirmDialogFragment;
    private final DrawerLayout drawerLayout;
    private final String DEBUG_TAG = getClass().getSimpleName();

    public RecordingsAdapter(Activity context, ArrayList<Record> recordsArray, DrawerLayout drawerLayout) {
        super(context, R.layout.row_record, recordsArray);
        this.context = context;
        this.recordsArray = recordsArray;
        this.drawerLayout = drawerLayout;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // set row item
        View rowView = inflater.inflate(R.layout.row_record, parent, false);
        TextView nameRecord;

        // set name of record
        nameRecord = (TextView) rowView.findViewById(R.id.row_record_name_tv);
        nameRecord.setText(recordsArray.get(position).getName());

        // delete button
        ImageButton deleteButton;
        deleteButton = (ImageButton) rowView.findViewById(R.id.delete_record_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG_TAG, "delete position:" + position);
                deleteRecord(recordsArray.get(position).getPath());
                drawerLayout.closeDrawers();
            }
        });
        return rowView;
    }


    private void deleteRecord(String path) {
        File file = new File(path);
        boolean result = file.delete();
        if(result){
            Toast.makeText(context, "record deleted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "record don't deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
