package de.doaschdn.muensterbus;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Departure {
    public enum TimeType {
        NOW,
        DEPARTURE_IN,
        DEPARTURE_AT
    }

    private String _busLine;
    private TimeType _timeType;
    private String _departureValue;
    private String _departureIn;
    private String _departureAt;

    public Departure(String busLine, TimeType type, String departureValue) {
        _busLine = busLine;
        _timeType = type;
        _departureValue = departureValue;

        calculateDepartures();
    }

    private void calculateDepartures() {
        switch (getTimeType()) {
            case NOW:
                _departureIn = "0";
                _departureAt = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
                break;
            case DEPARTURE_IN:
                _departureIn = _departureValue;
                _departureAt = calculateDepartureAt(_departureValue);
                break;
            case DEPARTURE_AT:
                _departureIn = calculateDepartureIn(_departureValue);
                _departureAt = _departureValue;
                break;
        }
    }

    private String calculateDepartureIn(String departureAt) {
        Matcher m = Pattern.compile("(\\d+):(\\d+)").matcher(departureAt);

        if (m.matches()) {
            int hours = Integer.parseInt(m.group(1));
            int minutes = Integer.parseInt(m.group(2));

            long arrivalInMillis = minutes * 60 * 1000 + hours * 60 * 60 * 1000;

            Date now = new Date();

            // Check, if arrival is on a new day (i.e. now=23:55, arrival=00:42)
            if (now.getHours() > hours) {
                // Add a day
                arrivalInMillis += 24 * 60 * 60 * 1000;
            }

            long nowInMillis = now.getHours() * 60 * 60 * 1000 + now.getMinutes() * 60 * 1000;

            return ((arrivalInMillis - nowInMillis) / (60 * 1000)) + "min";
        }

        return "";
    }

    private String calculateDepartureAt(String departureIn) {
        Matcher m = Pattern.compile("(\\d+)min", Pattern.CASE_INSENSITIVE).matcher(departureIn);

        if (m.matches()) {
            int minUntilArrival = Integer.parseInt(m.group(1));
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MINUTE, minUntilArrival);

            return new SimpleDateFormat("HH:mm", Locale.GERMANY).format(cal.getTime());
        }

        return "";
    }

    public String getBusLine() {
        return _busLine;
    }

    public TimeType getTimeType() {
        return _timeType;
    }

    public String getDepartureIn() {
        return _departureIn;
    }

    public String getDepartureAt() {
        return _departureAt;
    }

    @Override
    public String toString() {
        return "@Departure: {Line: " + _busLine + ", Departure in: " + _departureValue + "}";
    }
}
