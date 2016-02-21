package universityofedinburgh.myocross;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;

/**
 * Created by aliakbars on 11/20/13.
 * Modified by aliakbars on 02/21/16.
 */
public class App {

    public static SharedPreferences preferences;

    public static String host = "http://192.168.0.1";
    public static final String CHECK = "/get_status";
    public static final String PRESS_BUTTON = "/press_button";
    public static final String HOST_KEY = "universityofedinburgh.myocross.host";
    public static String url = host + CHECK;

    public static String DEBUG_TAG = "uoe";

    public static String get(View v) {
        if (v instanceof EditText) {
            return ((EditText) v).getText().toString();
        } else {
            throw new UnsupportedOperationException("Getting data from " + v.toString() + " is not implemented.");
        }
    }

    public static void showServerSettings(Context c) {
        final EditText ipAddressInput = new EditText(c);
        ipAddressInput.setHint("http://192.168.0.1");
        ipAddressInput.setText(App.host);
        new AlertDialog.Builder(c)
                .setTitle("Server")
                .setMessage("URL or IP address:")
                .setView(ipAddressInput)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        App.host = String.valueOf(ipAddressInput.getText());
                        App.preferences.edit().putString(App.HOST_KEY, App.host).commit();
                        url = App.host + App.CHECK;
                    }
                })
                .setNegativeButton("Cancel", null).show();
    }
}
