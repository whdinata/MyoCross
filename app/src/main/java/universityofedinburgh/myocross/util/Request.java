package universityofedinburgh.myocross.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by aliakbars on 15/11/15.
 */
public class Request {

    public static int post(String myurl, Map params) throws IOException, JSONException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;
        String urlParameters = "";

        Iterator it = params.entrySet().iterator();
        boolean first = true;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if (first) {
                first = false;
            } else {
                urlParameters += "&";
            }
            urlParameters += pair.getKey() + "=" + pair.getValue();
            it.remove(); // avoids a ConcurrentModificationException
        }
        Log.d("uoe", urlParameters);
        byte[] postData       = urlParameters.getBytes("UTF-8");
        int    postDataLength = postData.length;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.flush();
            wr.close();
            // Starts the query
            conn.connect();
//            Log.d("uoe", "The response is: " + response);
            is = conn.getInputStream();

            return conn.getResponseCode();

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static Result get(String myurl) throws IOException, JSONException {
        InputStream is = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setUseCaches(false);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("uoe", "The response is: " + response);
            is = conn.getInputStream();

            return new Result(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
