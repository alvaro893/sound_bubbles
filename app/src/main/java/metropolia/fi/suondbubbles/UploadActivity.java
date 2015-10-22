package metropolia.fi.suondbubbles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import metropolia.fi.suondbubbles.adapters.RawFilesAdapter;

public class UploadActivity extends AppCompatActivity {

    ListView filesListView;
    Field[] rawFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        rawFiles =R.raw.class.getFields();
        //HashMap<Integer, String> mapAudioFiles = getMapOfAudioFiles();
        filesListView = (ListView) findViewById(R.id.files_list);
        showList();


    }

    // TODO: Delete this method
    public HashMap getMapOfAudioFiles(){
        HashMap<Integer,String> listAudioFiles = new HashMap<>();
        Field[] fields=R.raw.class.getFields();
          ;  try {
                for(int count=0; count < fields.length; count++){
                    String filename = fields[count].getName();
                    Log.i("Raw Asset: ", filename);
                    int resourceID = fields[count].getInt(fields[count]);
                    listAudioFiles.put(resourceID, filename);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        return listAudioFiles;
    }

    private void showList() {
        final ArrayList<Field> rawFilesList = new ArrayList<>(Arrays.asList(rawFiles));
        ArrayAdapter<Field> adapter=new RawFilesAdapter(this, rawFilesList);
        this.filesListView.setAdapter(adapter);

        // set click for every item
        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Field rawFile = rawFilesList.get(position);
                try {
                    int resourceID = rawFile.getInt(rawFile);
                    Toast. makeText (getApplicationContext(),
                        resourceID, Toast. LENGTH_SHORT ).show();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
