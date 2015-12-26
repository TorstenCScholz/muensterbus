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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout _swipeRefreshLayout;
    @Bind(R.id.departure_list)
    LinearLayout _departureList;
    @Bind(R.id.rdgSelectedDestination)
    RadioGroup _rdgSelectedDestination;
    @Bind(R.id.station_spinner)
    Spinner _spStations;

    private BusStopGroup _selectedBusStopGroup;
    private BusStop selectedBusStop;

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
            } else {
                setBusStopGroup(busStopGroups.get(0));
            }
        }
    }

    class BusStopRequest extends AsyncTask<BusStop, Void, List<Departure>> {

        private final SWMApiEndpointInterface client = SWMClient.createService(SWMApiEndpointInterface.class);

        @Override
        protected List<Departure> doInBackground(BusStop... params) {
            List<Departure> departures = SWMParser.parseBusStopRequests(client.getDeparturesForBusStop(params[0].getId(), System.currentTimeMillis() / 1000));

            return departures;
        }

        @Override
        protected void onPostExecute(List<Departure> departureList) {
            runOnUiThread(new Runnable() {
                public void run() {
                    _swipeRefreshLayout.setRefreshing(false);
                }
            });

            clearDepartures();

            for (Departure departure : departureList) {
                addDeparture(departure);
            }
        }
    }

    private void displayDestinations(List<BusStop> busStops) {
        //clearDepartures();

        for (BusStop busStop : busStops) {
            String orientation;
            switch (busStop.getDirection()) {
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

        _swipeRefreshLayout.setColorSchemeResources(R.color.purple, R.color.blue, R.color.pink);
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (_selectedBusStopGroup == null) { // Nothing to load
                    _swipeRefreshLayout.setRefreshing(false);
                    return;
                }

                BusStop busStop = null;

                if (_selectedBusStopGroup.containsStation()) {
                    busStop = ((BusStopSpinnerWrapper)_spStations.getSelectedItem()).getBusStop();
                } else {
                    int radioButtonID = _rdgSelectedDestination.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = (RadioButton) _rdgSelectedDestination.findViewById(radioButtonID);
                    busStop = (BusStop) selectedRadioButton.getTag();
                }

                updateDepartures(busStop);
            }
        });

        /*_rdBtnInwards.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Inwards: _rdBtnInwards: " + _rdBtnInwards.isChecked() + ", param: " + isChecked);
                Log.d(TAG, "Inwards: _rdBtnOutwards: " + _rdBtnInwards.isChecked());
                if (isChecked) {
                    //_rdBtnOutwards.setChecked(false);
                    updateDepartures();
                }
            }
        });

        _rdBtnOutwards.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Outwards: _rdBtnOutwards: " + _rdBtnOutwards.isChecked() + ", param: " + isChecked);
                Log.d(TAG, "Outwards: _rdBtnInwards: " + _rdBtnOutwards.isChecked());
                if (isChecked) {
                    //_rdBtnInwards.setChecked(false);
                    updateDepartures();
                }
            }
        });*/

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
            } else if (data != null) {
                setBusStopGroup(new Gson().fromJson(data.toString(), BusStopGroup.class));
            }
        }
    }

    private void setBusStopGroup(BusStopGroup busStopGroup) {
        TextView tvBusStopName = (TextView) findViewById(R.id.busstop_name);
        tvBusStopName.setText(busStopGroup.getName());

        _rdgSelectedDestination.removeAllViews();
        _selectedBusStopGroup = busStopGroup;

        if (busStopGroup.containsStation()) {
            _rdgSelectedDestination.setVisibility(View.GONE);
            _spStations.setVisibility(View.VISIBLE);
            ArrayAdapter<BusStopSpinnerWrapper> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    BusStopSpinnerWrapper.fromBusStopList(busStopGroup.getBusStops())
            );
            _spStations.setAdapter(adapter);
            _spStations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BusStopSpinnerWrapper busStopWrapper = (BusStopSpinnerWrapper) parent.getAdapter().getItem(position);
                    updateDepartures(busStopWrapper.getBusStop());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            _spStations.setVisibility(View.GONE);
            _rdgSelectedDestination.setVisibility(View.VISIBLE);

            for (final BusStop busStop : busStopGroup.getBusStops()) {
                RadioButton rdBtnBusStop = new RadioButton(this);
                rdBtnBusStop.setText(busStop.getDirection() == Direction.INWARDS ? "einwärts" : "auswärts");
                rdBtnBusStop.setTag(busStop);
                rdBtnBusStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    BusStop _busStop = busStop;

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            updateDepartures(_busStop);
                        }
                    }
                });
                _rdgSelectedDestination.addView(rdBtnBusStop);
            }

            preselectDirection();
        }
    }

    private void preselectDirection() {
        RadioButton selectedDirection = (RadioButton) _rdgSelectedDestination.getChildAt(0);
        selectedDirection.setChecked(true);
    }

    private void updateDepartures(BusStop busStop) {
        Log.d(TAG, "New departure: " + busStop.toString());
        new BusStopRequest().execute(busStop);
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
        Log.d(TAG, "Adding to list view: " + departure.toString());

        DepartureRow dr = new DepartureRow(this);
        dr.setBusLine(getString(R.string.busline) + " " + departure.getBusLine());
        if (departure.getTimeType().equals(Departure.TimeType.DEPARTURE_IN)) {
            dr.setDepartureTimeLive(departure.getDepartureTime());
            //dr.setDepartureTimeCalculated("-");
        } else if (departure.getTimeType().equals(Departure.TimeType.NOW)) {
            dr.setDepartureTimeLive(getString(R.string.now));
        } else {
            dr.setDepartureTimeCalculated(departure.getDepartureTime());
        }

        _departureList.addView(dr);
    }

    private void clearDepartures() {
        _departureList.removeAllViews();
    }

}
