package universityofedinburgh.myocross;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import universityofedinburgh.myocross.util.Request;
import universityofedinburgh.myocross.util.Result;

/**
 * Created by aliakbars on 20/02/16.
 */
public class BeaconApplication extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                new BgTask().execute();
            }

            @Override
            public void onExitedRegion(Region region) {
                showNotification("Out of Range", "You are out of range!");
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        36593, 63260));
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private class BgTask extends AsyncTask<Void, Void, Result> {

        private ProgressDialog loadingDialog = new ProgressDialog(BeaconApplication.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setCancelable(false);
        }

        @Override
        protected Result doInBackground(Void... params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return Request.get("http://aliakbars.com/patients.json"); //TODO Put real URL here
            } catch (IOException e) {
                return new Result("400", "Unable to retrieve web page. URL may be invalid.");
            } catch (JSONException e) {
                Log.d("uoe", e.getMessage());
                return new Result("400", "Something went wrong while parsing the JSON data.");
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Result result) {
            if (loadingDialog.isShowing())
                loadingDialog.dismiss();
            if (result.getStatus().equals("success")) {
//                try {
                    showNotification("Debug", result.getMessage());
//                } catch (JSONException e) {
//                    new AlertDialog.Builder(BeaconApplication.this)
//                            .setTitle("Error").setMessage("Error while parsing JSON data.")
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .setCancelable(false).setNeutralButton(android.R.string.ok, null).show();
//                }
            } else {
                showNotification("Error", result.getMessage());
//                new AlertDialog.Builder(BeaconApplication.this)
//                        .setTitle("Error").setMessage(result.getMessage())
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setCancelable(false).setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

}
