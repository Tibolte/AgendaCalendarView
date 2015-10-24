package com.github.tibolte.agendacalendarview.weather;

import com.github.tibolte.agendacalendarview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Weather Icon View allows you to create Weather Icon for your Android application
 * It's based on Erik Flowers project located at: https://github.com/erikflowers/weather-icons
 * This project is open-source and can be found at: https://github.com/pwittchen/WeatherIconView
 *
 * @author Piotr Wittchen
 */
public class WeatherIconView extends TextView {
    private final static String PATH_TO_WEATHER_FONT = "fonts/weather.ttf";
    private final static int DEFAULT_WEATHER_ICON_SIZE = 100;
    private final static int DEFAULT_WEATHER_ICON_COLOR = Color.BLACK;
    private Typeface mWeatherFont;

    // region Constructors

    public WeatherIconView(Context context) {
        super(context);
        initialize(context);
    }

    public WeatherIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(context, attrs);
        initialize(context);
    }

    public WeatherIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeAttributes(context, attrs);
        initialize(context);
    }

    // endregion

    // region Private methods

    private void initialize(Context context) {
        if (isInEditMode()) {
            return;
        }
        mWeatherFont = Typeface.createFromAsset(context.getAssets(), PATH_TO_WEATHER_FONT);
        setTypeface(mWeatherFont);
    }

    private void initializeAttributes(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeatherIconView);
        initIconResource(typedArray);
        initIconSize(typedArray);
        initIconColor(typedArray);
        typedArray.recycle();
    }

    private void initIconResource(TypedArray typedArray) {
        String weatherIconCode = typedArray.getString(R.styleable.WeatherIconView_weatherIconResource);

        if (weatherIconCode == null || weatherIconCode.isEmpty()) {
            return;
        }

        setText(weatherIconCode);
    }

    private void initIconSize(TypedArray typedArray) {
        int weatherIconSize = typedArray.getInt(R.styleable.WeatherIconView_weatherIconSize, DEFAULT_WEATHER_ICON_SIZE);
        setTextSize(weatherIconSize);
    }

    private void initIconColor(TypedArray typedArray) {
        int weatherIconColor = typedArray.getColor(R.styleable.WeatherIconView_weatherIconColor, DEFAULT_WEATHER_ICON_COLOR);
        setTextColor(weatherIconColor);
    }

    // endregion

    // region Setters

    /**
     * sets weather icon basing on String resources
     * Icons are created from weather-icons TTF font by Erik Flowers
     * Full icons reference can be found at: http://erikflowers.github.io/weather-icons/
     *
     * @param iconCode icon code located in res/values/strings.xml file
     */
    public void setIconResource(String iconCode) {
        setText(iconCode);
    }

    /**
     * sets icon size
     *
     * @param size icon size as an integer; default size is equal to 100
     */
    public void setIconSize(int size) {
        setTextSize(size);
    }

    /**
     * sets icon color resource
     *
     * @param colorResource color resource - eg. Color.RED or any custom color; default is
     *                      Color.BLACK
     */
    public void setIconColor(int colorResource) {
        setTextColor(colorResource);
    }

    /**
     * sets weather icon based on Dark Sky forecast
     *
     * @param forecastDescription A machine-readable text summary, suitable for selecting an icon
     *                            for display.
     */
    public void setIconForForecast(String forecastDescription) {
        if (forecastDescription.equals("clear-day")) {
            setIconResource(getResources().getString(R.string.wi_day_sunny));
        } else if (forecastDescription.equals("clear-night")) {
            setIconResource(getResources().getString(R.string.wi_night_clear));
        } else if (forecastDescription.equals("rain")) {
            setIconResource(getResources().getString(R.string.wi_day_rain));
        } else if (forecastDescription.equals("snow")) {
            setIconResource(getResources().getString(R.string.wi_day_snow));
        } else if (forecastDescription.equals("sleet")) {
            setIconResource(getResources().getString(R.string.wi_day_rain_mix));
        } else if (forecastDescription.equals("wind")) {
            setIconResource(getResources().getString(R.string.wi_windy));
        } else if (forecastDescription.equals("fog")) {
            setIconResource(getResources().getString(R.string.wi_fog));
        } else if (forecastDescription.equals("cloudy")) {
            setIconResource(getResources().getString(R.string.wi_cloud));
        } else if (forecastDescription.equals("partly-cloudy-day")) {
            setIconResource(getResources().getString(R.string.wi_day_cloudy));
        } else if (forecastDescription.equals("partly-cloudy-night")) {
            setIconResource(getResources().getString(R.string.wi_night_cloudy));
        }
    }

    // endregion
}
