package com.m.aspirego.merchant_module.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.m.aspirego.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String MSG_KEY = "add";
    private GoogleMap mMap;
    TextView lat_lan_text;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private String current_lattitude, current_longnitude;
    private ImageView iv_search;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString(MSG_KEY);
            lat_lan_text.setText(string);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lat_lan_text = findViewById(R.id.lat_lan_text);
        iv_search=(ImageView)findViewById(R.id.iv_search);

        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (!checkingPermissionAreEnabledOrNot())
            requestMultiplePermission();

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAutoPlaceSelectionIntent();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        //mMap.getMyLocation();
    }

    @Override
    public void onCameraMove() {
        GeoRunnable geoRunnable=new GeoRunnable(mMap.getCameraPosition().target);
        Log.e("camera move",""+mMap.getCameraPosition().target.toString());
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(geoRunnable,500);
    }

    private String getCompleteAddressString(LatLng latLng) {
        double LATITUDE=latLng.latitude;
        double LONGITUDE=latLng.longitude;
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Current address", strReturnedAddress.toString());
            } else {
                Log.w("Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Current address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
//            lattitude=String.valueOf(mLocation.getLatitude());
//            longnitude=String.valueOf(mLocation.getLongitude());
        } else {
            callToast("Location not Detected");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i("suspended", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.i("failed", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location==null)
        {
            callToast("Location not detected");
            return;
        }
        if(current_lattitude==null && current_longnitude==null)
        {
            if(mMap!=null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 15));
                current_lattitude = Double.toString(location.getLatitude());
                current_longnitude = Double.toString(location.getLongitude());
                Log.e("current latlngs", "lat=" + current_lattitude + " long=" + current_longnitude);
                LatLng cur_latlng = new LatLng(location.getLatitude(), location.getLongitude());
                handler.postDelayed(new GeoRunnable(cur_latlng), 0);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMap.setOnCameraMoveListener(MapsActivity.this);
                    }
                },100);
            }
        }
    }


    public boolean checkingPermissionAreEnabledOrNot() {
        int coarseloc = ContextCompat.checkSelfPermission(MapsActivity.this, ACCESS_COARSE_LOCATION);
        int accessloc = ContextCompat.checkSelfPermission(MapsActivity.this, ACCESS_FINE_LOCATION);
        return coarseloc == PackageManager.PERMISSION_GRANTED && accessloc == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMultiplePermission() {

        ActivityCompat.requestPermissions(MapsActivity.this, new String[]
                {
                        ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION
                }, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 100:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkingPermissionAreEnabledOrNot())
                    {
                        startLocationUpdates();
                    } else {
                        callToast("Location permission is mandatory to use this app.");
                       finish();
                    }
                }
                return;
        }
    }


    private void callToast(String msg) {
        Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void saveAdd(View view) {
        if(mMap.getCameraPosition().target!=null){
            Intent intent=new Intent();
            intent.putExtra("latlng",mMap.getCameraPosition().target);
            intent.putExtra("add",lat_lan_text.getText().toString());
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    public class  GeoRunnable implements Runnable{
    LatLng latLng;

    public GeoRunnable(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public void run() {
        String add=getCompleteAddressString(latLng);
        Log.e("address",""+add);
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(MSG_KEY, add);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
  }

   private void setAutoPlaceSelectionIntent()
   {
       try {
           Intent intent =
                   new PlaceAutocomplete
                           .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                           .build(this);
           startActivityForResult(intent, 1);
       } catch (GooglePlayServicesRepairableException e) {
           // TODO: Handle the error.
       } catch (GooglePlayServicesNotAvailableException e) {
           // TODO: Handle the error.
       }
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);

                if(mMap!=null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 15));
                    current_lattitude = Double.toString(place.getLatLng().latitude);
                    current_longnitude = Double.toString(place.getLatLng().longitude);
                    Log.e("current latlngs", "lat=" + current_lattitude + " long=" + current_longnitude);
                    LatLng cur_latlng = place.getLatLng();
                    handler.postDelayed(new GeoRunnable(cur_latlng), 0);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMap.setOnCameraMoveListener(MapsActivity.this);
                        }
                    },100);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
