package de.doaschdn.muensterbus;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import de.doaschdn.muensterbus.de.doaschdn.muensterbus.util.StringUtil;

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
        MatrixCursor cursor = new MatrixCursor(new String[] { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA });
        Log.d(TAG, "Searching for: " + searchTerm);

        List<BusStopGroup> busStopGroupList = getBusStopGroupsFor(searchTerm);

        int idCounter = 0;
        for (BusStopGroup busStopGroup : busStopGroupList) {
            cursor.addRow(new Object[] { idCounter++, busStopGroup.getName(), new Gson().toJson(busStopGroup) });
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

    public static List<BusStopGroup> getBusStopGroupsFor(String searchTerm) {
        String results = client.getDestinationsForQuery(searchTerm, System.currentTimeMillis() / 1000L);
        List<BusStop> busStopList = SWMParser.parseSearchQueryResults(results);
        return BusStopGroup.createFromBusStopList(busStopList);
    }
}
