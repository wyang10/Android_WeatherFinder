package edu.northeastern.weatherfinder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link CityWeatherListFragment} factory method to
 * create an instance of this fragment.
 */
public class CityWeatherListFragment extends DialogFragment  {

    private CityWeatherListAdapter recyclerAdapter;
    private SharedPreferences sharedPreferences;
    //private WeatherStorage weatherStorage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_weather_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        final Bundle bundle = getArguments();
        final Toolbar toolbar = view.findViewById(R.id.dialogToolbar);
        final RecyclerView recyclerView = view.findViewById(R.id.locationsRecyclerView);
        final LinearLayout linearLayout = view.findViewById(R.id.locationsLinearLayout);

        //toolbar.setTitle(getString(R.string.location_search_heading));
        //close item list
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //linearLayout.setBackgroundColor(Color.parseColor("#2f2f2f"));

        try {
            final JSONArray cityListArray = new JSONArray(bundle.getString("cityList"));
            final ArrayList<Weather> weatherArrayList = new ArrayList<>();
            recyclerAdapter = new CityWeatherListAdapter(view.getContext().getApplicationContext(),
                    weatherArrayList);

            for (int i = 0; i < cityListArray.length(); i++) {
                final JSONObject cityObject = cityListArray.getJSONObject(i);
                final JSONObject weatherObject = cityObject.getJSONArray("weather").getJSONObject(0);
                final JSONObject mainObject = cityObject.getJSONObject("main");
                final JSONObject coordObject = cityObject.getJSONObject("coord");
                final JSONObject sysObject = cityObject.getJSONObject("sys");

                final String city = cityObject.getString("name");
                final String country = sysObject.getString("country");
                final int cityId = cityObject.getInt("id");
                final String description = weatherObject.getString("description");
                final int weatherId = weatherObject.getInt("id");
                final float temperature = UnitConvertor.convertTemperature((float) mainObject.getDouble("temp"), sharedPreferences);
                final double lat = coordObject.getDouble("lat");
                final double lon = coordObject.getDouble("lon");

                Weather weather = new Weather();
                weather.setCity(city);
                weather.setCityId(cityId);
                weather.setCountry(country);
                weather.setWeatherId(weatherId);
                weather.setDescription(description.substring(0, 1).toUpperCase() + description.substring(1));
                weather.setTemperature(temperature);
                weather.setLat(lat);
                weather.setLon(lon);
                weatherArrayList.add(weather);
            }


            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(recyclerAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void close() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getSupportFragmentManager().popBackStack();
        }
    }
}
