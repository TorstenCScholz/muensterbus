package de.doaschdn.muensterbus;

import java.util.List;

public class BusStopSpinnerWrapper {
    private BusStop _busStop;

    public BusStopSpinnerWrapper(BusStop busStop) {
        _busStop = busStop;
    }

    public BusStop getBusStop() {
        return _busStop;
    }

    @Override
    public String toString() {
        String ret = _busStop.getStation();

        if (_busStop.getDirection() != Direction.NONE) {
            ret += " (" + DirectionMap.translate(_busStop.getDirection()) + ")";
        }

        return ret;
    }

    public static BusStopSpinnerWrapper[] fromBusStopList(List<BusStop> busStops) {
        BusStopSpinnerWrapper[] wrapped = new BusStopSpinnerWrapper[busStops.size()];
        for (int i = 0; i < busStops.size(); ++i) {
            wrapped[i] = new BusStopSpinnerWrapper(busStops.get(i));
        }

        return wrapped;
    }
}
