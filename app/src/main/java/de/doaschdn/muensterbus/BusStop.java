package de.doaschdn.muensterbus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Torsten on 08.11.2015.
 */
public class BusStop implements Serializable {
    private String _id;
    private String _name;
    private Direction _direction;

    public BusStop(String id, String busStop, Direction direction) {
        _id = id;
        _name = busStop;
        _direction = direction;
    }

    public String getName() {
        return _name;
    }

    public void setName(String busStop) {
        this._name = busStop;
    }

    public Direction getDirection() {
        return _direction;
    }

    public void setInwards(Direction direction) {
        this._direction = direction;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public static List<BusStop> uniquifyByName(List<BusStop> busStopList) {
        Set<BusStop> uniqueBusStops = new TreeSet<>(new Comparator<BusStop>() {
            @Override
            public int compare(BusStop lhs, BusStop rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        uniqueBusStops.addAll(busStopList);

        // Convert object array to destination list
        Object[] uniqueDestinationArray = uniqueBusStops.toArray();
        return Arrays.asList(Arrays.copyOf(uniqueDestinationArray, uniqueDestinationArray.length, BusStop[].class));
    }

    @Override
    public String toString() {
        return "@BusStop[Id: " + _id + ", Name: " + _name + ", Direction: " + _direction.toString() + "]";
    }
}
