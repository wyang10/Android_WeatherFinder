package edu.northeastern.weatherfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;


public class WeatherSearchActivity extends AppCompatActivity {
    private Button getWeatherButton;
    private EditText city_editText;
    private String city;
    private LinearLayout mainLayout;
    private String LABEL = "WeatherSearchActivity";
    private CityWeatherListFragment fragment;
    private View weatherFindView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_search);
        getWeatherButton = findViewById(R.id.get_weather_button);
        city_editText = findViewById(R.id.city_editText);
        weatherFindView = getCurrentFocus();

        Handler mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    // Update the UI with the data received from the background thread
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(android.R.id.content, fragment)
                            .addToBackStack(null).commit();

                } else if (message.what == 0) {
                    //prompt to user that their search has failed
                    Snackbar.make(getCurrentFocus(), getString(R.string.fail_prompt), Snackbar.LENGTH_LONG).show();

                }
            }
        };

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View currentView = getCurrentFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                city = city_editText.getText().toString().trim();

                if (!city.isEmpty()) {
                    //Create a new HandlerThread object by calling the constructor with a name for the thread
                    HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
                    handlerThread.start();
                    //Create a Handler object using the looper from the HandlerThread, as shown below:
                    Handler handler = new Handler(handlerThread.getLooper());
                    //Post a Runnable object to the Handler to be executed on the background thread, as shown below:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Perform background task here
                            //1. make request to the web service and get response
                            String response = null;

                            try {
                                response = getCityWeatherInfo(city);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //2. convert response format
                            if (response != null) {
                                JSONArray cityList = convertResponse(response);

                                if (cityList == null) {
                                    Message message = Message.obtain();
                                    message.what = 0;
                                    message.obj = "search failed";
                                    mainHandler.sendMessage(message);
                                    return;
                                }

                                // 3. start a new fragment with new city list
                                fragment = new CityWeatherListFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("cityList", cityList.toString());
                                fragment.setArguments(bundle);

                                // 4. Send a message to the main thread
                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = "search success";
                                mainHandler.sendMessage(message);

                            }

                        }
                    });


                }
            }
        });

    }

    private JSONArray convertResponse(String res) {
        JSONArray resJsonArray = null;
        try {
            JSONObject reader = new JSONObject(res);
            final int cityCount = reader.optInt("count");
            if (cityCount == 0) {
                Log.v(LABEL, "city not found: " + cityCount);
                return null;
            }

             resJsonArray = reader.getJSONArray("list");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resJsonArray;
    }

    private String getCityWeatherInfo(String cityName) throws IOException {
        String response = "";
        URL geoURL = generateURL(cityName);
        HttpURLConnection geoUrlConnection = (HttpURLConnection) geoURL.openConnection();

        if (geoUrlConnection.getResponseCode() == 200) {
            Log.v(LABEL, "success: " + geoUrlConnection.getResponseCode());

            InputStreamReader inputStreamReader = new InputStreamReader(geoUrlConnection.getInputStream());
            BufferedReader r = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            response += stringBuilder.toString();

            r.close();
            geoUrlConnection.disconnect();
            // Background work finished successfully
            Log.i("Task", "done successfully");

        } else {
            Log.e(LABEL, "Failed to get response");
            return null;
        }

        Log.v(LABEL, "geo url response" + response);


        return response;
    }

    private URL generateURL(String cityName) throws MalformedURLException {
        String apiKey = getString(R.string.apiKey);

        StringBuilder urlBuilder = new StringBuilder(getString(R.string.weather_web_service_api));
        urlBuilder.append(cityName)
                .append("&lang=").append(Locale.getDefault().getLanguage())
                .append("&mode=json")
                .append("&appid=").append(apiKey);

        return new URL(urlBuilder.toString());
    }



}
