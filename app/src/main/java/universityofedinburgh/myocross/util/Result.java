package universityofedinburgh.myocross.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by aliakbars on 11/20/13.
 */
public class Result {

    private String status;
    private String message;

    public Result(InputStream is) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder jsonresponse = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonresponse.append(line);
        }
        JSONObject json = new JSONObject(jsonresponse.toString());
        this.setStatus("success");
        this.setMessage(json.getString("state"));
    }

    public Result(String status, String message) {
        this.setStatus(status);
        this.setMessage(message);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
