package seattle.edu.picmap;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.app.ActionBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private Location location;
    private DBHelper dbHelper;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;

    private LocationManager locationManager;

    // Define a listener that responds to location updates
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location loc) {
            // Called when a new location is found by the network location provider.
            location = loc;
            zoomInCurrentLocation();
            locationManager.removeUpdates(this);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getApplicationContext().deleteDatabase("PICMAP.db"); //to delete database
        dbHelper = new DBHelper(getApplicationContext());
        setContentView(R.layout.activity_maps);
        ActionBar bar = getActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#99CC00")));
        setUpMapIfNeeded();
        getLocationUpdate();
        DBHelper.LocationCursor cursor = dbHelper.queryLocations();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PicMapLocation location = cursor.getLocation();
            addMarker(location.getLatitude(), location.getLongitude(), location.getFileLocation());
            cursor.moveToNext();
        }

        Button cameraBtn = (Button) findViewById(R.id.click_picture);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        mMap.setInfoWindowAdapter(new MyInfoViewAdapter(getLayoutInflater()));
        mMap.setOnMarkerClickListener(new MyMarkerClickListener());
        mMap.setOnInfoWindowClickListener(new MyInfoWindowClickListener(
                getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocationUpdate();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            zoomInCurrentLocation();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                zoomInCurrentLocation();
            }
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    void takePicture() {
        if (mMap.getMyLocation() != null) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // create a file to save the image
            fileUri = getOutputMediaFileUri();
            // set the image file name
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // start the image capture Intent
            startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle("GPS Settings");
            // Setting Dialog Message
            alertDialog.setMessage("We couldn't get a location fix for you. Please verify that your GPS is enabled and retry.");

            // On pressing Settings button
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Location photoLocation = mMap.getMyLocation();
            dbHelper.insertLocation(photoLocation.getLatitude(), photoLocation.getLongitude(), fileUri.getPath());
            addMarker(photoLocation.getLatitude(), photoLocation.getLongitude(), fileUri.getPath());
        }
    }

    private void addMarker(double latitude, double longitude, String imagePath) {
        mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(imagePath)
                        .icon(BitmapDescriptorFactory.defaultMarker()));
    }

    private void zoomInCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if(location == null) {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        }
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))// Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                clearMarker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearMarker() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("Clear Markers");
        alertDialogBuilder
                .setMessage("Are you sure you want to delete all markers!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, delete all markers
                        mMap.clear();
                        dbHelper.deleteALL();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * Create a file Uri for saving an image
     */
    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile(MapsActivity.MEDIA_TYPE_IMAGE));
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //noinspection ConstantConditions
        if (MapsActivity.MEDIA_TYPE_IMAGE == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }
    private void getLocationUpdate(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
}
