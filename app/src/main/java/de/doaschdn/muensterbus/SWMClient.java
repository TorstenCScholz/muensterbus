package de.doaschdn.muensterbus;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Torsten on 08.11.2015.
 */
public class SWMClient {
    // Takes two arguments: First is the term to be searched, second is the current unix time
    private static final String BASE_URL = "http://www.stadtwerke-muenster.de/fis";

    private static RestAdapter.Builder builder = new RestAdapter.Builder()
            .setEndpoint(BASE_URL)
            .setConverter(StringConverterFactory.create())
            .setClient(new OkClient(new OkHttpClient()));

    public static <S> S createService(Class<S> serviceClass) {
        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }
}
