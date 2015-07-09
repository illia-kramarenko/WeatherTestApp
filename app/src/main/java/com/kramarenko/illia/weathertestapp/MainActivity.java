package com.kramarenko.illia.weathertestapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;
import com.kramarenko.illia.weathertestapp.retrofit.WeatherData;
import com.kramarenko.illia.weathertestapp.retrofit.WeatherWebServiceProxy;

import retrofit.RestAdapter;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected final String TAG = getClass().getSimpleName();
    /**
     *  Displays icon according to weather data
     */
    private ImageView mIconView;

    private final String imageURLbase =
            "http://api.openweathermap.org/img/w/";

    /**
     *  Displays received weather data:
     */
    // Country
    private TextView mCountry;
    // City
    private TextView mCity;
    // Temperature
    private TextView mTemp;
    // Wind
    private TextView mWind;
    // Humidity
    private TextView mHumidity;
    // Weather description
    private TextView mWeather;

    // Celsius unicode symbol
    final String DEGREE_CEL  = "\u2103";

    // Google map object
    private GoogleMap mMap;

    // Retrofit proxy for making GET request
    private WeatherWebServiceProxy mWeatherWebServiceProxy;

    // Weather data gathered from open weather map web-service
    private WeatherData mWeatherData;

    // Warning message
    private final String INVALID_INPUT = "Invalid input";

    // Location
    private String location = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        initViews();

        // Build the RetroFit RestAdapter, which is used to create
        // the RetroFit service instance, and then use it to build
        // the RetrofitWeatherServiceProxy.
        mWeatherWebServiceProxy =
                new RestAdapter.Builder()
                        .setEndpoint(WeatherWebServiceProxy.sWeather_Service_URL)
                        .build()
                        .create(WeatherWebServiceProxy.class);

    }

    // Initialize views
    private void initViews(){
        mIconView = (ImageView) findViewById(R.id.iconView);
        mCountry = (TextView) findViewById(R.id.country);
        mCity = (TextView) findViewById(R.id.city);
        mTemp = (TextView) findViewById(R.id.temp);
        mWind = (TextView) findViewById(R.id.wind);
        mHumidity = (TextView) findViewById(R.id.humidity);
        mWeather = (TextView) findViewById(R.id.weather);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Set up a SearchView
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getString(R.string.hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!s.isEmpty()) {
                    hideKeyboard(MainActivity.this, searchView.getWindowToken());
                    location = s;
                    lookUpForWeather();
                } else {
                    // never actually been here...
                    Toast.makeText(MainActivity.this, INVALID_INPUT, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }


    // Look up for weather, set up map and set the location
    private void lookUpForWeather(){
        // Check if internet connection is OK
        if(isOnline()) {
            new AsyncTask<String, Void, WeatherData>() {
                @Override
                protected WeatherData doInBackground(String... location) {
                    WeatherData wd = mWeatherWebServiceProxy.getWeatherData(location[0]);
                    if (wd != null) {
                        Log.d(TAG, "Weather data is OK");
                        return wd;
                    } else {
                        Log.d(TAG, "Weather data not found for this location");
                        return new WeatherData();
                    }
                }
                @Override
                protected void onPostExecute(WeatherData result) {
                    mWeatherData = result;
                    setResults();
                }
            }.execute(location);
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    // Format and Set weather results on display
    public void setResults(){
        if(mWeatherData != null) {
            setUpMapIfNeeded();

            mCountry.setText(mWeatherData.getSys().getCountry());
            mCity.setText(mWeatherData.getName());
            mTemp.setText(Math.round(mWeatherData.getMain().getTemp()) + DEGREE_CEL);
            mWind.setText(mWeatherData.getWind().getSpeed() + " m/s " + calcWindDirection(mWeatherData.getWind().getDeg()));
            mHumidity.setText(String.valueOf(mWeatherData.getMain().getHumidity()) + "%");
            mWeather.setText(Character.toUpperCase(mWeatherData.getWeathers().get(0).getDescription().charAt(0))
                    + mWeatherData.getWeathers().get(0).getDescription().substring(1));
            String iconName = mWeatherData.getWeathers().get(0).getIcon();

            // Load appropriate icon with Ion library
            Ion.with(mIconView)
                    .load(imageURLbase + iconName + ".png");
        } else
            Log.d(TAG, "Weather data is null in PostExecute");

    }

    // Set up map if it not set yet
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(this);
        } else {
            setUpMap();
        }
    }

    // Callback method when map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            setUpMap();
        }
    }

    // Set location and settings on map
    private void setUpMap() {
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(false);
        if(mWeatherData != null) {
            LatLng lating = new LatLng(mWeatherData.getCoord().getLat(),
                    mWeatherData.getCoord().getLon());
            mMap.addMarker(new MarkerOptions()
                    .position(lating)
                    .title(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lating, 10));
        } else {
            Log.d(TAG, "Weather data is null in setUpMap");
        }
    }

    /**
     * Format wind degree data to get understandable direction
     */
    @SuppressWarnings("ConstantConditions")
    private String calcWindDirection(double deg){
        int mDeg = (int) deg;
        if(mDeg > 337 && mDeg < 23) // 338 - 22
            return "N";
        if(mDeg > 22 && mDeg < 68) // 23 - 67
            return "NE";

        if(mDeg > 67 && mDeg < 114) // 68 - 113
            return "E";

        if(mDeg > 113 && mDeg < 158) // 114 - 157
            return "SE";

        if(mDeg > 157 && mDeg < 203) // 158 - 202
            return "S";

        if(mDeg > 202 && mDeg < 248) // 203 - 247
            return "SW";

        if(mDeg > 247 && mDeg < 293) // 248 - 292
            return "W";

        if(mDeg > 292 && mDeg < 338) // 293 - 337
            return "E";
        else return "";
    }


    // Helper method to check internet connection
    public boolean isOnline() {
       ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }


    // This method is used to hide a keyboard after a user has finished typing.
    public static void hideKeyboard(AppCompatActivity activity, IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                0);
    }
}
