package de.doaschdn.muensterbus;

/**
 * Created by Torsten on 07.11.2015.
 */
public class Departure {
    private String _busLine;

    public Departure(String busLine) {
        _busLine = busLine;
    }

    public String getBusLine() {
        return _busLine;
    }

    public void setBusLine(String busLine) {
        this._busLine = busLine;
    }
}
