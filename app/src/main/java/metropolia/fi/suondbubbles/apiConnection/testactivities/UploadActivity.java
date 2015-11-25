package metropolia.fi.suondbubbles.apiConnection.testactivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.adapters.RawFilesAdapter;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.tasks.UploadTask;

public class UploadActivity extends AppCompatActivity implements AsyncResponse {

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
                    Toast.makeText(getApplicationContext(), resourceID, Toast.LENGTH_SHORT).show();
                    showDialogue(resourceID);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    protected void showDialogue(final int resourceID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(android.R.string.yes, new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputStream inResource;
                        inResource = getResources().openRawResource(resourceID);
                        uploadFile(inResource);
                    }

                });
        builder.setNegativeButton(android.R.string.no, new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        builder.setMessage(R.string.upload_question);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void uploadFile(InputStream inResource){
        Toast. makeText (getApplicationContext(), "Uploading file",
                Toast. LENGTH_SHORT ).show();
        UploadTask uploadTask = new UploadTask();
        //uploadTask.delegate = this;
        uploadTask.execute(inResource);

    }

    @Override
    public void processFinish(Object result) {
        Log.d("response", "whatever");
    }
}
