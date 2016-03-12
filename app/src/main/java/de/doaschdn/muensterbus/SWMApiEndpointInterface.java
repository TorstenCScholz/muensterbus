package de.doaschdn.muensterbus;

import retrofit.http.GET;
import retrofit.http.Query;

public interface SWMApiEndpointInterface {
    @GET("/search.php")
    String getDestinationsForQuery(@Query("query") String query, @Query("_") long _);

    @GET("/ajaxrequest.php")
    String getDeparturesForBusStop(@Query("mastnr") String query, @Query("_") long _);
}
