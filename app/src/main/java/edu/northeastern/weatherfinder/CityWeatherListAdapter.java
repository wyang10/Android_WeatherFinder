package edu.northeastern.weatherfinder;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.northeastern.weatherfinder.R;


public class CityWeatherListAdapter extends RecyclerView.Adapter<CityWeatherListAdapter.CityWeatherItemViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Weather> weatherArrayList;

    private boolean decimalZeroes;
    private String temperatureUnit;

    public CityWeatherListAdapter(Context context, ArrayList<Weather> weatherArrayList){
        this.context = context;
        this.weatherArrayList = weatherArrayList;
        //LayoutInflater is used to instantiate XML layout files into their corresponding View objects at runtime
        this.inflater = LayoutInflater.from(context);
        // is a simple key-value store that allows you to store and retrieve primitive data types persistently across user session
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.decimalZeroes = sharedPreferences.getBoolean("displayDecimalZeroes", false);
        this.temperatureUnit = sharedPreferences.getString("unit", "°C");



    }


    @NonNull
    @Override
    public CityWeatherItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new CityWeatherItemViewHolder(inflater.inflate(R.layout.city_weather_item, parent, false));
    }

    //suppresses lint warnings for the method or class that it is applied to
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onBindViewHolder(@NonNull CityWeatherItemViewHolder holder, int position) {


        //Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
        //get item position
        Weather weather = weatherArrayList.get(position);
        holder.cityTextView.setText(String.format("%s, %s", weather.getCity(), weather.getCountry()));
        holder.descriptionTextView.setText(weather.getDescription());
        holder.cityTextView.setTextColor(Color.parseColor("#9370DB"));
        holder.temperatureTextView.setTextColor(Color.parseColor("#9370DB"));
        holder.descriptionTextView.setTextColor(Color.parseColor("#9370DB"));
        holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

        if (this.decimalZeroes) {
            holder.temperatureTextView.setText(new DecimalFormat("0.0").format(weather.getTemperature()) + " " + this.temperatureUnit);
        } else {
            holder.temperatureTextView.setText(new DecimalFormat("#.#").format(weather.getTemperature()) + " " + this.temperatureUnit);
        }


    }
    

    @Override
    public int getItemCount() {

        return weatherArrayList.size();
    }

    class CityWeatherItemViewHolder extends RecyclerView.ViewHolder {
        private TextView cityTextView;
        private TextView temperatureTextView;
        private TextView descriptionTextView;
        private CardView cardView;


        CityWeatherItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cityTextView = itemView.findViewById(R.id.rowCityTextView);
            temperatureTextView = itemView.findViewById(R.id.rowTemperatureTextView);
            descriptionTextView = itemView.findViewById(R.id.rowDescriptionTextView);
            cardView = itemView.findViewById(R.id.rowCardView);
        }
    }
}























