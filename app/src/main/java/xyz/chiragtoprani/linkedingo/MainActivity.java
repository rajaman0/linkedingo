package xyz.chiragtoprani.linkedingo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    GPSTracker gps;

    private SensorManager mSensorManager;
    TextView tvHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Acquiring current longitude and latitude coordinates
        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Toast.makeText(getApplicationContext(), "Your Location is -\nLat: " + latitude +
                    "\nLong: " + longitude, Toast.LENGTH_LONG).show();

            Log.v("location", "cats: " + gps.getLocation());
        }
        else {
            gps.showSettingsAlert();
        }

        // Reference to TextView
        tvHeading = (TextView) findViewById(R.id.tvHeading);

        // Acquiring compass degree
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Intent intent = new Intent(this, CameraActivity.class);
        //startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Not implemented
    }
}
