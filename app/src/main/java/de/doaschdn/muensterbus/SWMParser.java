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

    private static final String SEARCH_QUERY_RESULT_REGEX = "<a class=\"inactive\" name=\"efahyperlinks\" href=\"http://www.stadtwerke-muenster.de/fis/(\\d+?)\" target=\"_self\"><span style=\"font-weight:bold;\">(.+?)</span>(.+?) <span class=\"richtung\">(?:\\((.*?)\\))?</span></a>";

    public static List<Destination> parseSearchQueryResults(final String queryResults) {
        List<Destination> allMatches = new LinkedList<>();
        Matcher m = Pattern.compile(SEARCH_QUERY_RESULT_REGEX, Pattern.CASE_INSENSITIVE).matcher(queryResults);

        while (m.find()) {
            Destination destination = parseSingleSearchQueryResult(m.group());

            if (destination != null) {
                allMatches.add(destination);
            }
        }

        return allMatches;
    }

    public static Destination parseSingleSearchQueryResult(final String queryResult) {
        Matcher m = Pattern.compile(SEARCH_QUERY_RESULT_REGEX, Pattern.CASE_INSENSITIVE).matcher(queryResult);

        if (m.matches()) {
            String id = m.group(1);
            String busStopPart1 = m.group(2);
            String busStopPart2 = m.group(3);
            boolean inwards = false;
            if (m.group(4) != null) {
                inwards = m.group(4).startsWith("ein");
            }
            String busStop = busStopPart1 + busStopPart2;

            Log.d(TAG, "Id: " + id + ", Stop: " + busStop + ", Inwards? " + inwards);

            return new Destination(id, busStop, inwards);
        }

        return null;
    }
}
