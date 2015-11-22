package de.doaschdn.muensterbus;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Torsten on 08.11.2015.
 */
public interface SWMApiEndpointInterface {
    @GET("/search.php")
    String getDestinationsForQuery(@Query("query") String query, @Query("_") long _);

    @GET("/ajaxrequest.php")
    String getDeparturesForBusStop(@Query("mastnr") String query, @Query("_") long _);
}
