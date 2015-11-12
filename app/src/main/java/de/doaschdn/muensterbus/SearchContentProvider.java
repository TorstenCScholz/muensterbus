package de.doaschdn.muensterbus;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.List;

/**
 * Created by Torsten on 12.11.2015.
 */
public class SearchContentProvider extends ContentProvider {
    private static final String TAG = "SearchContentProvider";

    private static final SWMApiEndpointInterface client = SWMClient.createService(SWMApiEndpointInterface.class);

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String searchTerm = uri.getLastPathSegment().toLowerCase();
        Log.d(TAG, "Searching for: " + searchTerm);

        String results = client.getDestinationsForQuery(searchTerm, System.currentTimeMillis() / 1000L);
        MatrixCursor cursor = new MatrixCursor(new String[] { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1 });
        List<Destination> destinationList = SWMParser.parseSearchQueryResults(results);
        destinationList = Destination.uniquifyByBusStop(destinationList);

        for (Destination destination : destinationList) {
            cursor.addRow(new Object[] { Integer.parseInt(destination.getId()), destination.getBusStop() });
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.de.doaschdn.search";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
