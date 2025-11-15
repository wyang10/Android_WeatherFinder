package edu.northeastern.weatherfinder;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;


public class UnitConvertor {
    public static float convertTemperature(float temperature, SharedPreferences sp) {
        String unit = sp.getString("unit", "°C");
        return convertTemperature(temperature, unit);
    }

    public static float convertTemperature(float temperature, String unit) {
        float result;
        switch (unit) {
            case "°C":
                result = UnitConvertor.kelvinToCelsius(temperature);
                break;
            case "°F":
                result = UnitConvertor.kelvinToFahrenheit(temperature);
                break;
            default:
                result = temperature;
                break;
        }
        return result;
    }

    public static float kelvinToCelsius(float kelvinTemp) {
        return kelvinTemp - 273.15f;
    }

    public static float kelvinToFahrenheit(float kelvinTemp) {
        return ((kelvinTemp - 273.15f) * 1.8f) + 32;
    }

    public static float convertRain(float rain, SharedPreferences sp) {
        if (sp.getString("lengthUnit", "mm").equals("mm")) {
            return rain;
        } else {
            return rain / 25.4f;
        }
    }

    public static String getRainString(double rain, double percentOfPrecipitation, SharedPreferences sp) {
        StringBuilder sb = new StringBuilder();
        if (rain > 0) {
            sb.append(" (");
            String lengthUnit = sp.getString("lengthUnit", "mm");
            boolean isMetric = lengthUnit.equals("mm");

            if (rain < 0.1) {
                sb.append(isMetric ? "<0.1" : "<0.01");
            } else if (isMetric) {
                sb.append(String.format(Locale.ENGLISH, "%.1f %s", rain, lengthUnit));
            } else {
                sb.append(String.format(Locale.ENGLISH, "%.2f %s", rain, lengthUnit));
            }

            if (percentOfPrecipitation > 0) {
                sb.append(", ").append(String.format(Locale.ENGLISH, "%d%%", (int) (percentOfPrecipitation * 100)));
            }

            sb.append(")");
        }

        return sb.toString();
    }



}
