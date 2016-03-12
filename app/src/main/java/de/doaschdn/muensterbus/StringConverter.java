package de.doaschdn.muensterbus;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import retrofit.mime.TypedString;

/**
 * Created by Torsten on 08.11.2015.
 */
public class StringConverter implements Converter {

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        try {
            return IOUtils.toString(body.in(), "UTF-8");
        } catch (IOException e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        return new TypedString(object.toString());
    }
}
