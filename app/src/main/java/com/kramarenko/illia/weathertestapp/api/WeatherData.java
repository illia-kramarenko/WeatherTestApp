package com.kramarenko.illia.weathertestapp.api;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * {    "coord":{ "lon":-86.78, "lat":36.17 },
 *      "sys":{ "message":0.0138,
 *              "country":"United States of America", "sunrise":1431427373,
 *              "sunset":1431477841 },
 *      "weather":[ { "id":802, "main":"Clouds",
 *              "description":"scattered clouds", "icon":"03d" } ],
 *      "base":"stations",
 *      "main":{ "temp":289.847, "temp_min":289.847, "temp_max":289.847,
 *              "pressure":1010.71, "sea_level":1035.76, "grnd_level":1010.71, "humidity":76},
 *      "wind":{ "speed":2.42, "deg":310.002 }, "clouds":{ "all":36 },
 *      "dt":1431435983,
 *      "id":4644585,
 *      "name":"Nashville",
 *      "cod":200 }
 */
public class WeatherData {
    /*
     * These fields store the WeatherData's state.  We use
     * the @SerializedName annotation to make an explicit mapping
     * between the Json names and the fields in this class.  If we
     * named these fields the same as the Json names we won't need to
     * use this annotation.
     */
    @SerializedName("name")
    private String mName;
    @SerializedName("dt")
    private long mDate;
    @SerializedName("cod")
    private long mCod;
    @SerializedName("weather")
    private List<Weather> mWeathers = new ArrayList<Weather>();
    @SerializedName("sys")
    private Sys mSys;
    @SerializedName("main")
    private Main mMain;
    @SerializedName("wind")
    private Wind mWind;
    @SerializedName("coord")
    private Coord mCoord;

    /**
     * Constructor that initializes the POJO.
     */
    public WeatherData(String name,
                       long date,
                       long cod,
                       Coord coord,
                       Sys sys,
                       Main main,
                       Wind wind,
                       List<Weather> weathers) {
	mName = name;
	mDate = date;
	mCod = cod;
    mCoord = coord;
	mSys = sys;
	mMain = main;
	mWind = wind;
	mWeathers = weathers;
    }

	public WeatherData() {
		mName = "";
		mDate = 0;
		mCod = 0;
		mCoord = new Coord();
		mSys = new Sys();
		mMain = new Main();
		mWind = new Wind();
		mWeathers = new ArrayList<Weather>();
		mWeathers.add(0, new Weather());
	}


    /*
     * Access methods for data members
     */

    /**
     * Access method for the System info
     *

     */
    public Sys getSys() {
	return mSys;
    }

    /**
     * Access method for the Main info
     *
     */
    public Main getMain() {
	return mMain;
    }

    /**
     * Access method for the Wind info
     *
     */
    public Wind getWind() {
	return mWind;
    }

    /**
     * Access method for location's name
     *
     */
    public String getName() {
	return mName;
    }

    /**
     * Access method for the data's date
     *
     */
    public long getDate() {
	return mDate;
    }

    /**
     * Access method for the cod data
     *
     */
    public long getCod() {
	return mCod;
    }

    /**
     * Access method for the coord data
     *
     */
    public Coord getCoord(){
        return mCoord;
    }

    /**
     * Access method for the Weather objects
     *
     */
    public List<Weather> getWeathers() {
	return mWeathers;
    }


    /**
     * Inner class representing coordinates of selected location
     */
	public static class Coord {
		@SerializedName("lon")
		private double mLon;
		@SerializedName("lat")
		private double mLat;

		public Coord(double lon,
					   double lat) {
			mLon = lon;
            mLat = lat;
		}

		public Coord() {
			mLon = 0;
			mLat = 0;
		}
	/*
	 * Access methods for data members.
	 */

		public double getLon() {
			return mLon;
		}

		public double getLat() {
			return mLat;
		}

	}

    /**
     * Inner class representing a description of a current weather
     * condition.
     */
    public static class Weather {
	@SerializedName("id")
        private long mId;
	@SerializedName("main")
        private String mMain;
	@SerializedName("description")
        private String mDescription;
	@SerializedName("icon")
        private String mIcon;

	public Weather(long id,
                       String main,
                       String description,
                       String icon) {
	    mId = id;
	    mMain = main;
	    mDescription = description;
	    mIcon = icon;
	}

	public Weather() {
		mId = 0;
		mMain = "";
		mDescription = "Location not found";
		mIcon = "";
	}

	/*
	 * Access methods for data members.
	 */

	public long getId() {
	    return mId;
	}

	public String getMain() {
	    return mMain;
	}

	public String getDescription() {
	    return mDescription;
	}

	public String getIcon() {
	    return mIcon;
	}

    }

    /**
     * Inner class representing system data.
     */
    public static class Sys {
	@SerializedName("sunrise")
        private long mSunrise;
	@SerializedName("sunset")
        private long mSunset;
	@SerializedName("country")
        private String mCountry;

	public Sys(long sunrise,
                   long sunset,
                   String country) {
	    mSunrise = sunrise;
	    mSunset = sunset;
	    mCountry = country;
	}

	public Sys() {
		mSunrise = 0;
		mSunset = 0;
		mCountry = "Location not found";
	}

	/*
	 * Access methods for data members
	 */

	public long getSunrise() {
	    return mSunrise;
	}

	public long getSunset() {
	    return mSunset;
	}

	public String getCountry() {
	    return mCountry;
	}
    }

    /**
     * Inner class representing the core weather data
     */
    public static class Main {
	@SerializedName("temp")
        private double mTemp;
	@SerializedName("humidity")
        private long mHumidity;
	@SerializedName("pressure")
        private double mPressure;

	public Main(double temp,
                    long humidity,
                    double pressure) {
	    mTemp = temp;
	    mHumidity = humidity;
	    mPressure = pressure;
	}

	public Main() {
		mTemp = 0;
		mHumidity = 0;
		mPressure = 0;
	}

	/*
	 * Access methods for data members
	 */

	public double getPressure() {
	    return mPressure;
	}

	public double getTemp() {
	    return mTemp;
	}

	public long getHumidity() {
	    return mHumidity;
	}
    }

    /**
     * Inner class representing wind data
     */
    public static class Wind {
	@SerializedName("speed")
        private double mSpeed;
	@SerializedName("deg")
        private double mDeg;

	public Wind(double speed,
                    double deg) {
	    mSpeed = speed;
	    mDeg = deg;
	}


	public Wind() {
		mSpeed = 0;
		mDeg = 0;
	}
	/*
	 * Access methods for data members
	 */

	public double getSpeed() {
	    return mSpeed;
	}

	public double getDeg() {
	    return mDeg;
	}
    }


}
