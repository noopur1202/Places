package com.workstation.places;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener, android.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mGoogleMap;
    public Double mLatitude;
    public Double mLongitude;
    private Marker mCurrentLocationMarker;
    private boolean drawRoute;
    private AsyncTask<String, Integer, List<List<HashMap<String, String>>>> parserTaskForRoute;
    private CameraUpdate cameraUpdate;
    private Polyline polyline;
    private String latlng;
    private ArrayList<ServiceOrFuelStationModel> stationPointArrayList;
    private AsyncTask<String, Integer, List<List<HashMap<String, String>>>> parserTask;
    private AsyncTask<String, Integer, HashMap<String, String>> detailsParserTask;
    private AsyncTask<String, Void, String> getServiceStationPointsAsyncTask;
    private AsyncTask<String, Void, String> getStationDetailsAsyncTask;
    private LocationManager locationManager;
    private AsyncTask<String, Void, String> getFuelStationPointsAsyncTask;
//    private RingInflatorPreferencemanager mRingInflatorPreferenceManager = null;
    public Location location;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private String provider;
    final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    LocationManager mLocManager;
    private LatLng latLng = null;
    Context mContext;
    MapView mapView;
//    GooglePlaces googlePlaces;
    //    PlacesList nearPlaces;
    private GoogleApiClient mGoogleApiClient;
    boolean stationNameFlag;
    public String stationName,stationAddress, stationIcon, callNumber;
//    CallBroadcastmanager callBroadcastManager;
//    private BaseActivity mContext;
    public static TextView internet,internetBottom;
    public static RelativeLayout internetlayout,internetBottomLayout;
    private BroadcastReceiver mNetworkReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mapView = (MapView)findViewById(R.id.mapView);

        mContext = (MapActivity)this;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        this.mLatitude = mRingInflatorPreferenceManager.getDouble(RingInflatorPreferencemanager.Key.CAR_LAT, 0);
//        this.mLongitude = mRingInflatorPreferenceManager.getDouble(RingInflatorPreferencemanager.Key.CAR_LNG, 0);

        try {
            MapsInitializer.initialize(getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        mapView.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//        mLatitude=lat;
//        mLongitude=lng;
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMarkerClickListener(this);
    }

    private void registerForLocationUpdate() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    private void moveToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            } else {
                registerForLocationUpdate();
            }
        }
    }

    private void fetchServiceStation() {
        stationNameFlag = true;
        String URL;
        URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + mLatitude + "," + mLongitude + "&radius=5000&types=car_repair&key=AIzaSyB9LtIklJ1WWM3NlhoAD4bVvGfJiebTFSQ";
        getServiceStationPointsAsyncTask = new StationPointsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL);
    }

    private void fetchStationDetails(String placeId){
        String URL;
        URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=AIzaSyB9LtIklJ1WWM3NlhoAD4bVvGfJiebTFSQ";
        getStationDetailsAsyncTask = new StationPointsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            Toast.makeText(this, "Location or Network provider is not enabled. Please enable", Toast.LENGTH_LONG).show();
            registerForLocationUpdate();
        } else {
            moveToCurrentLocation();
        }
        moveToCurrentLocation();
        fetchServiceStation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private class StationPointsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... url) {
// For storing data from web service
            String data = "";
            try {
// Fetching the data from web service
                Log.d("LOG TAG", "the url for google server is " + url[0]);
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            Log.d("LOG TAG", "the data is " + data);
            return data;
        }
        // Executes in UI thread, after the execution of
// doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("LOG TAG", "the result from server is " + result);
//if draw route then call decode polyline async task

            if (stationNameFlag){
                parserTask = new ParserTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);
                stationNameFlag = false;
            }else {
                detailsParserTask = new DetailsParserTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);
            }
// Invokes the thread for parsing the JSON data
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
// Creating an http connection to communicate with url
            urlConnection = (HttpsURLConnection) url.openConnection();
// Connecting to url
            urlConnection.connect();
// Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("LOG TAG", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            Float stationRating = null;
            List<List<HashMap<String, String>>> routes = null;
            stationPointArrayList = new ArrayList<>();

            try {
                JSONObject jb = new JSONObject(jsonData[0]);
                JSONArray jsonObject1 = (JSONArray) jb.get("results");
                for (int i = 0; i < jsonObject1.length(); i++) {
                    JSONObject jsonObject2 = (JSONObject) jsonObject1.get(i);
                    JSONObject jsonObject3 = (JSONObject) jsonObject2.get("geometry");
                    JSONObject location = (JSONObject) jsonObject3.get("location");
                    LatLng point = new LatLng(Double.parseDouble(location.get("lat").toString()), Double.parseDouble(location.get("lng").toString()));
                    String stationName = (String) jsonObject2.get("name");
                    String stationId = (String) jsonObject2.get("place_id");
                    String stationIcon = (String) jsonObject2.get("icon");
                    String stationAddress = (String) jsonObject2.get("vicinity");
                    ServiceOrFuelStationModel serviceOrFuelStationObject = new ServiceOrFuelStationModel();
                    serviceOrFuelStationObject.setLatLng(point);
                    serviceOrFuelStationObject.setName(stationName);
                    serviceOrFuelStationObject.setIcon(stationIcon);
                    serviceOrFuelStationObject.setStationid(stationId);
                    serviceOrFuelStationObject.setAddress(stationAddress);
                    stationPointArrayList.add(serviceOrFuelStationObject);
                    System.out.println("Lat = " + location.get("lat"));
                    System.out.println("Lng = " + location.get("lng"));
                    System.out.println("name = " + stationName);
                    System.out.println("icon = " + stationIcon);
                    System.out.println("placeId = " + stationId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            LatLng position = null;
            String stationIcon = null;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            // Traversing through all the routes
            Log.d("LOG TAG", "the station List size is" + stationPointArrayList.size());
            if (stationPointArrayList.size() != 0) {
                for (int i = 0; i < stationPointArrayList.size(); i++) {
                    position = stationPointArrayList.get(i).getLatLng();
                    stationName = stationPointArrayList.get(i).getName();
                    stationAddress = stationPointArrayList.get(i).getAddress();
                    stationIcon = stationPointArrayList.get(i).getIcon();

                    mGoogleMap.addMarker(new MarkerOptions().position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker()));
                    builder.include(position);
                    LatLngBounds bounds = builder.build();
                }

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        LatLng clickPosition = marker.getPosition();
                        String clickStationId = null;

                        for (int i = 0; i<stationPointArrayList.size();i++){

                            LatLng comparePosition = stationPointArrayList.get(i).getLatLng();

                            if (clickPosition.equals(comparePosition)){
                                clickStationId = stationPointArrayList.get(i).getStationid();
                            }
                        }
                        fetchStationDetails(clickStationId);
                        return false;
                    }
                });
            }

        }
    }

    private class DetailsParserTask extends AsyncTask<String, Integer, HashMap<String,String>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected HashMap<String,String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;
            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Start parsing Google place details in JSON format
                hPlaceDetails = placeDetailsJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return hPlaceDetails;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(HashMap<String,String> hPlaceDetails){

            String name = hPlaceDetails.get("name");
            String icon = hPlaceDetails.get("icon");
            String vicinity = hPlaceDetails.get("vicinity");
            String lat = hPlaceDetails.get("lat");
            String lng = hPlaceDetails.get("lng");
            String formatted_address = hPlaceDetails.get("formatted_address");
            String formatted_phone = hPlaceDetails.get("formatted_phone");
            String website = hPlaceDetails.get("website");
            Float rating = Float.valueOf(hPlaceDetails.get("rating"));
            String international_phone_number = hPlaceDetails.get("international_phone_number");

            System.out.println("name = " + name);
            System.out.println("phone = " + international_phone_number);
            System.out.println("address = " + formatted_address);
            System.out.println("vicinity = " + vicinity);
            System.out.println("rating = " + rating);
            System.out.println("lat,lng = " + lat + "," + lng);

//            setPlaceDetails(name,vicinity,formatted_address,rating,international_phone_number,lat,lng);
        }
    }

//    private void setPlaceDetails(String name, String vicinity, String formatted_address, Float rating, String international_phone_number, final String lat, final String lng) {
//
//        AssistanceViewModel.mBottomSheetBehavior.setPeekHeight(250);
//        mActivityAssistanceBinding.title.setText(name);
//
//        String[] splitVicinity = vicinity.split(",");
//
//        vicinity = splitVicinity[splitVicinity.length-2] + ", " + splitVicinity[splitVicinity.length-1];
//
//        mActivityAssistanceBinding.vicinity.setText(vicinity);
//        mActivityAssistanceBinding.rating.setText(String.valueOf(rating));
//        mActivityAssistanceBinding.address.setText(formatted_address);
//        mActivityAssistanceBinding.ratingBar.setRating(rating);
//
//        callNumber = international_phone_number;
//
//        mActivityAssistanceBinding.callLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callPhone(callNumber);
//            }
//        });
//
//        mActivityAssistanceBinding.directionsLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Float.valueOf(lat),Float.valueOf(lng));
////                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
////                mContext.startActivity(intent);
//
//                Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
//            }
//        });
//    }

//    private void callPhone(String number) {
//
//        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
//        phoneIntent.setData(Uri.parse("tel:" + number));
//
//        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            return;
//        }
//        mContext.startActivity(phoneIntent);
//        callBroadcastManager = new CallBroadcastmanager();
//        mContext.registerReceiver(callBroadcastManager,new IntentFilter(Intent.ACTION_MAIN));
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
