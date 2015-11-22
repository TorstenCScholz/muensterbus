package de.doaschdn.muensterbus;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Torsten on 08.11.2015.
 */
public class SWMParser {
    private static final String TAG = "SWMParser";

    private static final String SEARCH_QUERY_RESULT_REGEX = "<a class=\"inactive\" name=\"efahyperlinks\" href=\"http://www.stadtwerke-muenster.de/fis/(\\d+?)\" target=\"_self\">(.*?)<span style=\"font-weight:bold;\">(.+?)</span>(.*?) <span class=\"richtung\">(?:\\((.*?)\\))?</span></a>";
    private static final String BUSSTOP_REQUEST_RESULT_REGEX = "<div class=\"\\w+\"><div class=\"line\">([^<]+?)</div><div class=\"direction\">([^<]+?)</div><div class=\"\\w+\">([^>]*?)</div><br class=\"clear\" /></div>";

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

    public static BusStop parseSingleSearchQueryResult(final String queryResult) {
        Matcher m = Pattern.compile(SEARCH_QUERY_RESULT_REGEX, Pattern.CASE_INSENSITIVE).matcher(queryResult);

        if (m.matches()) {
            String id = m.group(1);
            String busStopPart1 = m.group(2);
            String busStopPart2 = m.group(3);
            String busStopPart3 = m.group(4);
            Orientation orientation = Orientation.NONE;

            if (m.group(5) != null) {
                orientation = m.group(5).startsWith("ein") ? Orientation.INWARDS : Orientation.OUTWARDS;
            }
            String busStop = busStopPart1 + busStopPart2 + busStopPart3;

            Log.d(TAG, "Id: " + id + ", Stop: " + busStop + ", Orientation: " + orientation);

            return new BusStop(id, busStop, orientation);
        }

        return null;
    }

    public static List<Departure> parseBusStopRequests(final String queryResult) {
        List<Departure> departures = new LinkedList<>();
        Matcher m = Pattern.compile(BUSSTOP_REQUEST_RESULT_REGEX, Pattern.CASE_INSENSITIVE).matcher(queryResult);

        while (m.find()) {
            String busLine = m.group(1);
            String direction = m.group(2);
            String departureTime = m.group(3);

            if (departureTime.isEmpty()) {
                departureTime = "now";
            }

            Departure departure = new Departure(busLine, departureTime);
            Log.d(TAG, "Found Departure: " + departure.toString());
            departures.add(departure);
        }

        return departures;
    }
}
