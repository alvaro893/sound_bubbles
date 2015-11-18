package metropolia.fi.suondbubbles.apiConnection.tasks;

import android.os.AsyncTask;

import metropolia.fi.suondbubbles.apiConnection.ServerConnection;

/**
 * Created by Alvaro on 02/11/2015.
 */
public class CategoryTask extends AsyncTask<Void, Void, String[]> {


    @Override
    protected String[] doInBackground(Void... voids) {
        ServerConnection serverConnection = new ServerConnection();
        return serverConnection.getCategories();
    }
}
