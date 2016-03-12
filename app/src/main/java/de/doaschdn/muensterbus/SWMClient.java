package de.doaschdn.muensterbus;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class SWMClient {
    // Takes two arguments: First is the term to be searched, second is the current unix time
    public static final String BASE_URL = "http://www.stadtwerke-muenster.de/fis";
    public static final String BASE_URL_DEV = "http://192.168.2.103:4567";

    public static final String USED_BASE_URL = BASE_URL;

    private static RestAdapter.Builder builder = new RestAdapter.Builder()
            .setEndpoint(USED_BASE_URL)
            .setConverter(StringConverterFactory.create())
            .setClient(new OkClient(new OkHttpClient()));

    public static <S> S createService(Class<S> serviceClass) {
        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }
}
