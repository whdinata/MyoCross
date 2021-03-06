package universityofedinburgh.myocross;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.scanner.ScanActivity;

import com.estimote.sdk.SystemRequirementsChecker;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import universityofedinburgh.myocross.util.Request;
import universityofedinburgh.myocross.util.Result;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private BeaconManager beaconManager;
    private TextView tvLabel;
    private ImageView imageView;
    private Myo myo;
    private boolean connected;
    private Handler mHandler;
    private final int mInterval = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.preferences = this.getSharedPreferences("universityofedinburgh.myocross", Context.MODE_PRIVATE);
        App.host = App.preferences.getString(App.HOST_KEY, App.host);
        App.url = App.host + App.CHECK;

        initView();
        initialiseMyo();
        mHandler = new Handler();
        //initialiseBeacon();
    }

    private void initView(){
        tvLabel = (TextView) findViewById(R.id.label);
        imageView = (ImageView) findViewById(R.id.image);
    }

    private void initialiseMyo(){
        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e(TAG, "Could not initialize the Hub.");
            finish();
            return;
        }

        Hub.getInstance().setLockingPolicy(Hub.LockingPolicy.NONE);

        DeviceListener mListener = new AbstractDeviceListener() {

            @Override
            public void onAttach(Myo myo, long timestamp) {
                // The object for a Myo is unique - in other words, it's safe to compare two Myo references to
                // see if they're referring to the same Myo.
                // Add the Myo object to our list of known Myo devices. This list is used to implement identifyMyo() below so
                // that we can give each Myo a nice short identifier.
                MainActivity.this.myo = myo;
                // Now that we've added it to our list, get our short ID for it and print it out.
                //Log.i(TAG, "Attached to " + myo.getMacAddress() + ", now known as Myo " + identifyMyo(myo) + ".");
            }

            @Override
            public void onConnect(Myo myo, long timestamp) {
                Toast.makeText(MainActivity.this, "Myo Connected!", Toast.LENGTH_SHORT).show();
                initialiseBeacon();
            }

            @Override
            public void onDisconnect(Myo myo, long timestamp) {
                Toast.makeText(MainActivity.this, "Myo Disconnected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPose(Myo myo, long timestamp, Pose pose) {
//                Toast.makeText(MainActivity.this, "Pose: " + pose, Toast.LENGTH_SHORT).show();
                Log.d("uoe", "Pose: " + pose);

                if(pose.equals(Pose.FIST)){
                    new PressButtonTask().execute();
                }
                //TODO: Do something awesome.
            }
        };

        Hub.getInstance().addListener(mListener);


        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private void initialiseBeacon(){
        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                startRepeatingTask();
            }

            @Override
            public void onExitedRegion(Region region) {
                tvLabel.setText(R.string.label_no_traffic);
                imageView.setImageResource(R.drawable.ic_traffic);
                stopRepeatingTask();
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

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                new BgTask().execute();
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            App.showServerSettings(MainActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Result doInBackground(Void... params) {
            try {
                return Request.get(App.url);
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
            if (result.getStatus().equals("success")) {

                Log.d("uoe", result.getMessage());
                if(result.getMessage().equals("green")) {
                    tvLabel.setText(R.string.label_walk);
                    imageView.setImageResource(R.drawable.ic_walk);
                    myo.vibrate(Myo.VibrationType.LONG);
                } else {
                    tvLabel.setText(R.string.label_stop);
                    imageView.setImageResource(R.drawable.ic_stop);
                    myo.vibrate(Myo.VibrationType.SHORT);
                    myo.vibrate(Myo.VibrationType.SHORT);
                }
            } else {
                showNotification("Error", result.getMessage());
            }
        }
    }

    private class PressButtonTask extends AsyncTask<Void, Void, Result> {

        private ProgressDialog loadingDialog = new ProgressDialog(getApplicationContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myo.vibrate(Myo.VibrationType.SHORT);
            loadingDialog.setCancelable(false);
        }

        @Override
        protected Result doInBackground(Void... params) {

            try {
                // The map can be used later as the POST parameters
                Map map = new HashMap();
                int responseCode = Request.post(App.host + App.PRESS_BUTTON, map);
                return new Result(String.valueOf(responseCode), "Button pressed!");
            } catch (IOException e) {
                return new Result("400", "Unable to retrieve web page. URL may be invalid.");
            } catch (JSONException e) {
                Log.d("uoe", e.getMessage());
                return new Result("400", "Something went wrong while parsing the JSON data.");
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            if (loadingDialog.isShowing())
                loadingDialog.dismiss();
            if (!result.getStatus().equals("400")) {
                // Everything is okay
                // showNotification("Success", result.getMessage()); // Just a different kind of notification
                Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                showNotification("Error", result.getMessage());
            }
        }
    }
}
