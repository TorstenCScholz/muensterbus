package de.doaschdn.muensterbus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.doaschdn.muensterbus.de.doaschdn.muensterbus.util.StringUtil;

/**
 * Created by Torsten on 14.11.2015.
 */
public class BusStopGroup implements Serializable {
    private List<BusStop> _busStops;

    public BusStopGroup(BusStop busStop) {
        _busStops = new LinkedList<>();
        addBusStop(busStop);
    }

    public BusStopGroup(List<BusStop> busStops) {
        _busStops = new LinkedList<>();
        _busStops.addAll(busStops);
    }

    public void addBusStop(BusStop busStop) {
        if (_busStops.contains(busStop)) {
            return;
        }

        _busStops.add(busStop);
    }

    public void removeBusStop(BusStop busStop) {
        if (_busStops.size() == 1) {
            throw new IllegalStateException("Cannot remove a bus stop from a group of one.");
        }
        _busStops.remove(busStop);
    }

    public final List<BusStop> getBusStops() {
        return _busStops;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("@BusStopGroup {\n");
        for (BusStop busStop : _busStops) {
            builder.append("  ").append(busStop.toString()).append("\n");
        }
        builder.append("}");

        return builder.toString();
    }

    public String getName() {
        String commonPrefix = _busStops.get(0).getName();

        for (int i = 1; i < _busStops.size(); ++i) {
            commonPrefix = StringUtil.getLongestCommonPrefix(commonPrefix, _busStops.get(i).getName());
        }

        return commonPrefix;
    }

    public boolean hasDirection(Direction direction) {
        if (direction != Direction.NONE) {
            for (BusStop busStop : _busStops) {
                if (busStop.getDirection() == direction) {
                    return true;
                }
            }
        }

        return false;
    }

    public static List<BusStopGroup> createFromBusStopList(List<BusStop> busStopList) {
        BusStop[] busStopArray = busStopList.toArray(new BusStop[busStopList.size()]);
        Map<String, BusStopGroup> busStopGroupMap = new HashMap<>();

        for (BusStop busStop1 : busStopArray) {
            BusStopGroup group = new BusStopGroup(busStop1);

            for (BusStop busStop2 : busStopArray) {
                if (busStop1 == busStop2) {
                    continue;
                }
                if (busStop1.belongsToSameGroupAs((busStop2))) {
                    group.addBusStop(busStop2);
                }
            }

            String groupName = group.getName();
            if (!busStopGroupMap.containsKey(groupName)) {
                busStopGroupMap.put(groupName, group);
            }
        }

        return new ArrayList<>(busStopGroupMap.values());
    }
}
