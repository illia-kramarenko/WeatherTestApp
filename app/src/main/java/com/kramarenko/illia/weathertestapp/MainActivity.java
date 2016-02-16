package com.kramarenko.illia.weathertestapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.rxbinding.widget.RxSearchView;
import com.koushikdutta.ion.Ion;
import com.kramarenko.illia.weathertestapp.api.ApiService;
import com.kramarenko.illia.weathertestapp.api.WeatherData;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected final String TAG = getClass().getSimpleName();
    private ImageView mIconView;

    private final String imageURLbase =
            "http://api.openweathermap.org/img/w/";

    private TextView mCountry;
    private TextView mCity;
    private TextView mTemp;
    private TextView mWind;
    private TextView mHumidity;
    private TextView mWeather;
    final String DEGREE_CEL  = "\u2103";
    private GoogleMap mMap;
    private WeatherData mWeatherData;
    private final String INVALID_INPUT = "Invalid input";
    private String location = "";
    private Subscription sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        initViews();
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

        RxSearchView.queryTextChanges(searchView)
                .debounce(700, TimeUnit.MILLISECONDS)
                .filter(cs -> !TextUtils.isEmpty(cs))
                .filter(cs -> isOnline())
                .map(CharSequence::toString)
                .subscribe(this::makeCall);

        return true;
    }

    private void makeCall(String s) {
        location = s;
        Log.d("### Call ###", ">>>>>>>>>>>>>> call made: " + s);
        sub = ApiService.getService().getWeather(s, ApiService.APP_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setResults, this::onError);
    }

    public void setResults(WeatherData _w){
        mWeatherData = _w;
        if(mWeatherData != null) {
            setUpMapIfNeeded();

            mCountry.setText(_w.getSys().getCountry());
            mCity.setText(_w.getName());
            mTemp.setText(Math.round(_w.getMain().getTemp()) + DEGREE_CEL);
            mWind.setText(_w.getWind().getSpeed() + " m/s " + calcWindDirection(_w.getWind().getDeg()));
            mHumidity.setText(String.valueOf(_w.getMain().getHumidity()) + "%");
            mWeather.setText(Character.toUpperCase(_w.getWeathers().get(0).getDescription().charAt(0))
                    + _w.getWeathers().get(0).getDescription().substring(1));
            String iconName = _w.getWeathers().get(0).getIcon();

            // Load appropriate icon with Ion library
            Ion.with(mIconView)
                    .load(imageURLbase + iconName + ".png");
        } else
            Log.d(TAG, "Weather data is null in PostExecute");

    }

    private void onError(Throwable t) {
        Toast.makeText(this, "Something BAD happened", Toast.LENGTH_SHORT).show();
        t.printStackTrace();
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
            mMap.clear();
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
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else {
            Toast.makeText(this, "No Connection", Toast.LENGTH_SHORT).show();
            return false;
        }

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
