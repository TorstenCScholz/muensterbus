package de.doaschdn.muensterbus;

import java.util.HashMap;
import java.util.Map;

public class DirectionMap {
    private static Map<Direction, String> _m = new HashMap<Direction, String>();

    static {
        _m.put(Direction.INWARDS, "einwärts");
        _m.put(Direction.OUTWARDS, "auswärts");
        _m.put(Direction.END, "Ende");
    }

    public static String translate(Direction direction) {
        return _m.get(direction);
    }
}
