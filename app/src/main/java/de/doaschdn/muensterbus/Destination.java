package de.doaschdn.muensterbus;

/**
 * Created by Torsten on 08.11.2015.
 */
public class Destination {
    private String _id;
    private String _busStop;
    private boolean _inwards;

    public Destination(String id, String busStop, boolean inwards) {
        _id = id;
        _busStop = busStop;
        _inwards = inwards;
    }

    public String getBusStop() {
        return _busStop;
    }

    public void setBusStop(String busStop) {
        this._busStop = busStop;
    }

    public boolean isInwards() {
        return _inwards;
    }

    public void setInwards(boolean inwards) {
        this._inwards = inwards;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }
}
