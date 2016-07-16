package xyz.chiragtoprani.linkedingo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CameraActivity extends AppCompatActivity implements SensorEventListener {


    private Camera mCamera;
    private CameraPreview mPreview;

    private SensorManager sensorManager;
    private View view;
    private long lastUpdate;

    LocationManager locMgr;

    private DrawSurfaceView mDrawView;
    private float [] mGravity = new float[3], mGeomagnetic = new float[3];
    float[] rMat = new float[9];
    float[] iMat = new float[9];
    float[] orientation = new float[3];

    public void onSensorChanged(SensorEvent event) {
        if (mDrawView != null) {
            mDrawView.setOffset(event.values[0]);
            Log.v("TAG", event.values[0] + " ");
            mDrawView.invalidate();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public static Bitmap screenshot(WebView webView, float scale11) {
        try {
            float scale = webView.getScale();
            int height = (int) (webView.getContentHeight() * scale + 0.5);
            Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        // Create an instance of Camera
        if (!checkCameraHardware(this)){
            //no camera
            Log.d("Camera", "no camera");
        }
        mCamera = getCameraInstance();
        mDrawView = (DrawSurfaceView) findViewById(R.id.drawSurfaceView);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        locMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE); // <2>
        LocationProvider high = locMgr.getProvider(locMgr.getBestProvider(LocationUtils.createFineCriteria(), true));
        // using high accuracy provider... to listen for updates
        locMgr.requestLocationUpdates(high.getName(), 0, 0f, new LocationListener() {

            public void onLocationChanged(Location location) {
                mDrawView.setMyLocation(location.getLatitude(), location.getLongitude());
                mDrawView.invalidate();
            }

            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            public void onProviderEnabled(String s) {
                // try switching to a different provider
            }

            public void onProviderDisabled(String s) {
                // try switching to a different provider
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_map);
        final CameraActivity me = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(me, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void onPause(){
        super.onPause();
        mCamera.release();
        sensorManager.unregisterListener(this);

    }
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exception = sw.toString();
            Log.d("Camera", "Camera is not available " + exception);
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors

        sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }


}
