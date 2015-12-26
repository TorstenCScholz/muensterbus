package de.doaschdn.muensterbus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Torsten on 08.11.2015.
 */
public class BusStop implements Serializable {
    private static final String STATIC_BUS_STOP_NAME_REGEX = "(?:(.+)\\s)?(\\w\\d?)(?:\\s(.+))?";

    private String _id;
    private String _name;
    private Direction _direction;
    private String _station;

    public BusStop(String id, String busStop, Direction direction) {
        this(id, busStop, direction, null);
    }

    public BusStop(String id, String busStop, Direction direction, String station) {
        _id = id;
        _name = busStop;
        _direction = direction;
        _station = station;
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

    public void setDirection(Direction direction) {
        this._direction = direction;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getStation() {
        return _station;
    }

    public void setStation(String station) {
        _station = station;
    }

    public boolean isStation() {
        return getStation() != null;
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
        return "@BusStop[Id: " + _id + ", Name: " + _name + ", Direction: " + _direction.toString() + ", Station: " + _station + "]";
    }

    public boolean belongsToSameGroupAs(BusStop busStop) {
        return busStop != null && getName().equals(busStop.getName());
    }

    public static boolean containsStationName(String fullName) {
        Matcher m = Pattern.compile(STATIC_BUS_STOP_NAME_REGEX, Pattern.CASE_INSENSITIVE).matcher(fullName);

        return m.matches();
    }

    public static BusStop fromStationName(String id, String fullName, Direction direction) {
        Matcher m = Pattern.compile(STATIC_BUS_STOP_NAME_REGEX, Pattern.CASE_INSENSITIVE).matcher(fullName);

        if (m.matches()) {
            String prefix = m.group(1) != null ? m.group(1) : "";
            String station = m.group(2);
            String postfix = m.group(3) != null ? m.group(3) : "";

            return new BusStop(id, prefix + postfix, direction, station);
        }

        return null;
    }
}
