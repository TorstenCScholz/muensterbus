package de.doaschdn.muensterbus;

/**
 * Created by Torsten on 08.11.2015.
 */
public class Destination {
    private String _id;
    private String _busStop;
    private Orientation _orientation;

    public Destination(String id, String busStop, Orientation orientation) {
        _id = id;
        _busStop = busStop;
        _orientation = orientation;
    }

    public String getBusStop() {
        return _busStop;
    }

    public void setBusStop(String busStop) {
        this._busStop = busStop;
    }

    public Orientation getOrientation() {
        return _orientation;
    }

    public void setInwards(Orientation orientation) {
        this._orientation = orientation;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }
}
