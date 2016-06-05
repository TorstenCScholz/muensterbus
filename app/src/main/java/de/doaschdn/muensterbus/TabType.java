package de.doaschdn.muensterbus;

public enum TabType {
    Departure(0),
    Favorites(1);

    private int _value;

    TabType(int value) {
        _value = value;
    }

    public int getValue() {
        return _value;
    }
}
