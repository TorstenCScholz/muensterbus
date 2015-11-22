package de.doaschdn.muensterbus;

/**
 * Created by Torsten on 07.11.2015.
 */
public class Departure {
    private String _busLine;
    private String _departureTime;

    public Departure(String busLine, String departureIn) {
        _busLine = busLine;
        _departureTime = departureIn;
    }

    public String getBusLine() {
        return _busLine;
    }

    public void setBusLine(String busLine) {
        this._busLine = busLine;
    }

    public String getDepartureTime() {
        return _departureTime;
    }

    public void setDepartureTime(String departureTime) {
        _departureTime = departureTime;
    }

    @Override
    public String toString() {
        return "@Departure: {Line: " + _busLine + ", Departure in: " + _departureTime + "}";
    }
}
