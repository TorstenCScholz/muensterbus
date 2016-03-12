package de.doaschdn.muensterbus;

import retrofit.converter.Converter;

public class StringConverterFactory {
    public static Converter create() {
        return new StringConverter();
    }
}
