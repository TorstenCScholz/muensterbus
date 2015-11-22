package de.doaschdn.muensterbus;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout _swipeRefreshLayout;
    @Bind(R.id.departure_list) LinearLayout _departureList;
    @Bind(R.id.rdBtnIn) RadioButton _rdBtnInwards;
    @Bind(R.id.rdBtnOut)RadioButton _rdBtnOutwards;

    class QueryUpdater extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            List<BusStop> busStops = SWMParser.parseSearchQueryResults(result);

            displayDestinations(busStops);

            _swipeRefreshLayout.setRefreshing(false);
        }
    }

    class QuerySearchForSpecificTerm extends AsyncTask<String, Void, List<BusStopGroup>> {

        private String _busStopName;

        @Override
        protected List<BusStopGroup> doInBackground(String... params) {
            _busStopName = params[0];
            return SearchContentProvider.getBusStopGroupsFor(_busStopName);
        }

        @Override
        protected void onPostExecute(List<BusStopGroup> busStopGroups) {
            if (busStopGroups.size() != 1) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.busstop_not_found)
                        .setMessage(MessageFormat.format(getText(R.string.busstop_not_found_desc).toString(), _busStopName))
                        .setCancelable(true)
                        .setPositiveButton("Ok", null)
                        .show();
            }
            else {
                setBusStop(busStopGroups.get(0));
            }
        }
    }

    class BusStopRequest extends AsyncTask<String, Void, List<Departure>> {

        private final SWMApiEndpointInterface client = SWMClient.createService(SWMApiEndpointInterface.class);

        @Override
        protected List<Departure> doInBackground(String... params) {
            return SWMParser.parseBusStopRequests(client.getDeparturesForBusStop(params[0], System.currentTimeMillis() / 1000));
        }

        @Override
        protected void onPostExecute(List<Departure> departureList) {
            clearDepartures();

            for (Departure departure : departureList) {
                addDeparture(departure);
            }
        }
    }

    private void displayDestinations(List<BusStop> busStops) {
        clearDepartures();

        for (BusStop busStop : busStops) {
            String orientation;
            switch (busStop.getOrientation()) {
                case INWARDS:
                    orientation = "<-";
                    break;
                case OUTWARDS:
                    orientation = "->";
                    break;
                default:
                    orientation = "*";
                    break;
            }
            //addDeparture(new Departure(busStop.getId() + ": " + busStop.getName() + ". " + orientation));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);

        /*_etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new QueryUpdater().execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });*/

        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });

        handleIntent(getIntent());

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Object data = intent.getData();

            Log.d(TAG, "Handling search for: " + query);
            Log.d(TAG, "Data: " + (data != null ? data.toString() : "<null>"));

            if (query != null) {
                new QuerySearchForSpecificTerm().execute(query);
            }
            else if (data != null) {
                setBusStop(new Gson().fromJson(data.toString(), BusStopGroup.class));
            }
        }
    }

    private void setBusStop(BusStopGroup group) {
        TextView tvBusStopName = (TextView)findViewById(R.id.busstop_name);
        tvBusStopName.setText(group.getName());

        // TODO: Group items must be dynamic
        _rdBtnInwards.setEnabled(group.containsOrientation(Orientation.INWARDS));
        _rdBtnOutwards.setEnabled(group.containsOrientation(Orientation.OUTWARDS));

        new BusStopRequest().execute(group.getBusStops().get(0).getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            onSearchRequested();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addDeparture(Departure departure) {
        TextView tv = new TextView(this);
        tv.setText(departure.getBusLine() + ": " + departure.getDepartureTime());

        _departureList.addView(tv);
    }

    private void clearDepartures() {
        _departureList.removeAllViews();
    }

}
