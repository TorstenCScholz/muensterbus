package de.doaschdn.muensterbus;

/**
 * Created by Torsten on 26.12.2015.
 */
public class StaticBusStop extends BusStop {
    private String _staticStop;

    public StaticBusStop(String id, String busStop, String staticStop) {
        super(id, busStop, Direction.STATIC);

        _staticStop = staticStop;
    }

    public void setStaticStop(String staticStop) {
        _staticStop = staticStop;
    }

    public String getStaticStop() {
        return _staticStop;
    }

    @Override
    public boolean belongsToSameGroupAs(BusStop busStop) {
        return busStop instanceof StaticBusStop && busStop.getName().equals(getName());
    }
}
