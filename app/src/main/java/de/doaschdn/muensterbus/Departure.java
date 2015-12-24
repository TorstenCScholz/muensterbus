package de.doaschdn.muensterbus;

/**
 * Created by Torsten on 07.11.2015.
 */
public class Departure {
    public enum TimeType {
        NOW,
        DEPARTURE_IN,
        DEPARTURE_AT
    }

    private String _busLine;
    private TimeType _timeType;
    private String _departureValue;

    public Departure(String busLine, TimeType type, String departureValue) {
        _busLine = busLine;
        _timeType = type;
        _departureValue = departureValue;
    }

    public String getBusLine() {
        return _busLine;
    }

    public void setBusLine(String busLine) {
        this._busLine = busLine;
    }

    public TimeType getTimeType() {
        return _timeType;
    }

    public void setTimeType(TimeType type) {
        _timeType = type;
    }

    public String getDepartureTime() {
        return _departureValue;
    }

    public void setDepartureTime(String departureTime) {
        _departureValue = departureTime;
    }

    @Override
    public String toString() {
        return "@Departure: {Line: " + _busLine + ", Departure in: " + _departureValue + "}";
    }
}
