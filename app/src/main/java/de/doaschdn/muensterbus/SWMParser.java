package de.doaschdn.muensterbus;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SWMParser {
    private static final String TAG = "SWMParser";

    private static final int REGEX_MATCH_BUSSTOP_ID = 1;
    private static final int REGEX_MATCH_BUSSTOP_PART_START = 2;
    private static final int REGEX_MATCH_BUSSTOP_PART_MIDDLE = 3;
    private static final int REGEX_MATCH_BUSSTOP_PART_END = 4;
    private static final int REGEX_MATCH_BUSSTOP_DIRECTION = 5;

    private static final int REGEX_MATCH_BUSLINE_NAME = 1;
    private static final int REGEX_MATCH_BUSLINE_DIRECTION = 2;
    private static final int REGEX_MATCH_BUSLINE_DEPARTURETIME = 3;

    private static final String SEARCH_QUERY_RESULT_REGEX = "<a class=\"inactive\" name=\"efahyperlinks\" href=\"" + SWMClient.USED_BASE_URL + "/(\\d+?)\" target=\"_self\">(.*?)<span style=\"font-weight:bold;\">(.+?)</span>(.*?) <span class=\"richtung\">(?:\\((.*?)\\))?</span></a>";
    private static final String BUSSTOP_REQUEST_RESULT_REGEX = "<div class=\"\\w+\"><div class=\"line\">([^<]+?)</div><div class=\"direction\">([^<]+?)</div><div class=\"\\w+\">((?:[^<]*?)|(?:<div class=\"borden\"></div>))</div><br class=\"clear\" /></div>";

    // Because the class is to be considered static
    private SWMParser() {
    }

    public static List<BusStop> parseSearchQueryResults(final String queryResults) {
        List<BusStop> allMatches = new LinkedList<>();
        Matcher m = Pattern.compile(SEARCH_QUERY_RESULT_REGEX, Pattern.CASE_INSENSITIVE).matcher(queryResults);

        while (m.find()) {
            BusStop busStop = parseSingleSearchQueryResult(m.group());

            if (busStop != null) {
                allMatches.add(busStop);
            }
        }

        return allMatches;
    }

    @Nullable
    public static BusStop parseSingleSearchQueryResult(final String queryResult) {
        Matcher m = Pattern.compile(SEARCH_QUERY_RESULT_REGEX, Pattern.CASE_INSENSITIVE).matcher(queryResult);

        if (!m.matches()) {
            return null;
        }

        String id = m.group(REGEX_MATCH_BUSSTOP_ID);
        String direction = null;
        String busStop = combineBusStopParts(
                m.group(REGEX_MATCH_BUSSTOP_PART_START),
                m.group(REGEX_MATCH_BUSSTOP_PART_MIDDLE),
                m.group(REGEX_MATCH_BUSSTOP_PART_END)
        );

        String matchBusStopDirection = m.group(REGEX_MATCH_BUSSTOP_DIRECTION);
        if (matchBusStopDirection != null) {
            direction = matchBusStopDirection;
        }

        if (BusStop.containsStationName(busStop)) {
            return BusStop.fromStationName(id, busStop, direction);
        }

        Log.d(TAG, "Id: " + id + ", Stop: " + busStop + ", Direction: " + direction);

        return new BusStop(id, busStop, direction);
    }

    private static String combineBusStopParts(String partStart, String partMiddle, String partEnd) {
        return partStart + partMiddle + partEnd;
    }

    public static List<Departure> parseBusStopRequests(final String queryResult) {
        List<Departure> departures = new LinkedList<>();
        Matcher m = Pattern.compile(BUSSTOP_REQUEST_RESULT_REGEX, Pattern.CASE_INSENSITIVE).matcher(queryResult);

        Log.d(TAG, "Analysing: " + queryResult);

        while (m.find()) {
            String busLine = m.group(REGEX_MATCH_BUSLINE_NAME);
            String direction = m.group(REGEX_MATCH_BUSLINE_DIRECTION);
            String departureTime = m.group(REGEX_MATCH_BUSLINE_DEPARTURETIME);
            Departure.TimeType time = getDepartureTime(departureTime);

            if (time == Departure.TimeType.NOW) {
                departureTime = "0";
            }

            Departure departure = new Departure(busLine, time, departureTime);
            Log.d(TAG, "Found Departure: " + departure.toString());
            departures.add(departure);
        }

        return departures;
    }

    private static Departure.TimeType getDepartureTime(String departureTime) {
        if (departureTime.startsWith("<div")) {
            return Departure.TimeType.NOW;
        } else if (departureTime.contains(":")) {
            return Departure.TimeType.DEPARTURE_AT;
        }

        return Departure.TimeType.DEPARTURE_IN;
    }
}
