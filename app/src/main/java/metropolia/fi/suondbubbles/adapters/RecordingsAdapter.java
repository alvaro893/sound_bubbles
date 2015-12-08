package metropolia.fi.suondbubbles.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.helper.Record;

/**
 * Created by alvarob on 8.12.2015.
 */
public class RecordingsAdapter extends ArrayAdapter<Record>{
    Activity context;
    ArrayList<Record> recordsArray;

    public RecordingsAdapter(Activity context, ArrayList<Record> recordsArray) {
        super(context, R.layout.row_record, recordsArray);
        this.context = context;
        this.recordsArray = recordsArray;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_record, parent, false);
        TextView nameRecord;
        nameRecord = (TextView) rowView.findViewById(R.id.row_record_name_tv);
        nameRecord.setText(recordsArray.get(position).getName());
        return rowView;
    }


    public void setNoFilesView() {
    }
}
