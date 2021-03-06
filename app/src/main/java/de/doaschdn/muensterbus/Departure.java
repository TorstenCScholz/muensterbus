package de.doaschdn.muensterbus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java8.util.Optional;

public class Departure {
    private static final long ONE_SECOND_IN_MILLIS = 1000;
    private static final long ONE_MINUTE_IN_MILLIS = 60 * ONE_SECOND_IN_MILLIS;
    private static final long ONE_HOUR_IN_MILLIS = 60 * ONE_MINUTE_IN_MILLIS;
    private static final long ONE_DAY_IN_MILLIS = 24 * ONE_HOUR_IN_MILLIS;

    private static final Pattern PATTERN_DEPARTURE_AT = Pattern.compile("(\\d+):(\\d+)");
    private static final Pattern PATTERN_DEPARTURE_IN = Pattern.compile("(\\d+)\\s+min", Pattern.CASE_INSENSITIVE);

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
                _departureAt = getTransformedDepartureAtText(_departureValue);
                break;
            case DEPARTURE_AT:
                _departureIn = getTransformedDepartureInText(_departureValue);
                _departureAt = _departureValue;
                break;
        }
    }

    private String getTransformedDepartureAtText(String departureValue) {
        Optional<String> departureAtOptional = transformDepartureInToDepartureAt(departureValue);

        if (departureAtOptional.isPresent()) {
            return departureAtOptional.get();
        }

        return "";
    }

    private String getTransformedDepartureInText(String departureValue) {
        Optional<String> departureInOptional = transformDepartureAtToDepartureIn(departureValue);

        if (departureInOptional.isPresent()) {
            return departureInOptional.get();
        }

        return "";
    }

    private Optional<String> transformDepartureAtToDepartureIn(String departureAt) {
        Matcher m = PATTERN_DEPARTURE_AT.matcher(departureAt);

        if (!m.matches()) {
            return Optional.empty();
        }

        int hours = Integer.parseInt(m.group(1));
        int minutes = Integer.parseInt(m.group(2));

        long arrivalInMillis = minutes * ONE_MINUTE_IN_MILLIS + hours * ONE_HOUR_IN_MILLIS;

        Date now = new Date();

        // Check, if arrival is on a new day (i.e. now=23:55, arrival=00:42)
        if (now.getHours() > hours) {
            arrivalInMillis += ONE_DAY_IN_MILLIS;
        }

        long nowInMillis = now.getHours() * ONE_HOUR_IN_MILLIS + now.getMinutes() * ONE_MINUTE_IN_MILLIS;

        return Optional.of(((arrivalInMillis - nowInMillis) / ONE_MINUTE_IN_MILLIS) + "min");
    }

    private Optional<String> transformDepartureInToDepartureAt(String departureIn) {
        Matcher m = PATTERN_DEPARTURE_IN.matcher(departureIn);

        if (!m.matches()) {
            return Optional.empty();
        }

        int minUntilArrival = Integer.parseInt(m.group(1));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, minUntilArrival);

        return Optional.of(new SimpleDateFormat("HH:mm", Locale.GERMANY).format(cal.getTime()));
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
