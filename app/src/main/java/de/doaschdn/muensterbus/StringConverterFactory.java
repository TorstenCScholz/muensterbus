package de.doaschdn.muensterbus;

import retrofit.converter.Converter;


/**
 * Created by Torsten on 08.11.2015.
 */
public class StringConverterFactory {
    public static Converter create() {
        return new StringConverter();
    }
}
