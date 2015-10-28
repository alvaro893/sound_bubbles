package metropolia.fi.suondbubbles.apiConnection;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by alvarob on 7.10.2015.
 */

public class ServerConnectionTest {
ServerConnection sc;

    @Before
    public void setUp() throws Exception {
        sc = new ServerConnection();
    }

//    @After
//    public void tearDown() throws Exception {
//
//    }

    @Test
    public void testAuth() throws Exception {
        assertTrue(sc.auth(null, null).length() > 40);
    }

    @Test
    public void testSearch() throws Exception {

        assertTrue(true);

    }

    @Test
    public void testUpload() throws Exception {
        assertTrue(true);
    }
}