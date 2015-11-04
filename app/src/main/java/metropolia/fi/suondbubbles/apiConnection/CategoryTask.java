package metropolia.fi.suondbubbles.apiConnection;

import android.os.AsyncTask;

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
