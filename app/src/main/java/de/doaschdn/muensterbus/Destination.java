package de.doaschdn.muensterbus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    public static List<Destination> uniquifyByBusStop(List<Destination> destinationList) {
        Set<Destination> uniqueDestinations = new TreeSet<>(new Comparator<Destination>() {
            @Override
            public int compare(Destination lhs, Destination rhs) {
                return lhs.getBusStop().compareTo(rhs.getBusStop());
            }
        });
        uniqueDestinations.addAll(destinationList);

        // Convert object array to destination list
        Object[] uniqueDestinationArray = uniqueDestinations.toArray();
        return Arrays.asList(Arrays.copyOf(uniqueDestinationArray, uniqueDestinationArray.length, Destination[].class));
    }
}
