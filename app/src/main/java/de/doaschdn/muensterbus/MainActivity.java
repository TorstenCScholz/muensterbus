package de.doaschdn.muensterbus;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.doaschdn.muensterbus.thirdparty.DividerItemDecoration;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout _swipeRefreshLayout;
    @Bind(R.id.departure_list)
    RecyclerView _departureView;
    @Bind(R.id.rdgSelectedDestination)
    RadioGroup _rdgSelectedDestination;
    @Bind(R.id.station_spinner)
    Spinner _spStations;
    @Bind(R.id.loading_panel)
    RelativeLayout _loadingPanel;

    private BusStopGroup _selectedBusStopGroup;

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
            return SWMParser.parseBusStopRequests(
                    client.getDeparturesForBusStop(
                            params[0].getId(),
                            System.currentTimeMillis() / 1000
                    )
            );
        }

        @Override
        protected void onPostExecute(List<Departure> departureList) {
            runOnUiThread(new Runnable() {
                public void run() {
                    _swipeRefreshLayout.setRefreshing(false);
                    displayLoadingPanel(false);
                }
            });

            DepartureAdapter adapter = new DepartureAdapter(getApplicationContext(), departureList);
            _departureView.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);

        _swipeRefreshLayout.setColorSchemeResources(R.color.primary_dark, R.color.primary, R.color.primary_light);
        _swipeRefreshLayout.setOnRefreshListener(getOnRefreshListener());

        initDepartureView();

        handleIntent(getIntent());

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
    }

    @NonNull
    private SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (_selectedBusStopGroup == null) { // Nothing to load
                    _swipeRefreshLayout.setRefreshing(false);
                    return;
                }

                BusStop busStop;

                if (_selectedBusStopGroup.containsStation()) {
                    busStop = ((BusStopSpinnerWrapper) _spStations.getSelectedItem()).getBusStop();
                } else {
                    int radioButtonID = _rdgSelectedDestination.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = (RadioButton) _rdgSelectedDestination.findViewById(radioButtonID);
                    busStop = (BusStop) selectedRadioButton.getTag();
                }

                updateDepartures(busStop);
            }
        };
    }

    private void initDepartureView() {
        _departureView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _departureView.setLayoutManager(llm);
        DividerItemDecoration divider = new DividerItemDecoration(this, null);
        _departureView.addItemDecoration(divider);

        displayLoadingPanel(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
            return;
        }

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

    private void setBusStopGroup(BusStopGroup busStopGroup) {
        TextView tvBusStopName = (TextView) findViewById(R.id.busstop_name);
        tvBusStopName.setText(busStopGroup.getName());

        _rdgSelectedDestination.removeAllViews();
        _selectedBusStopGroup = busStopGroup;

        if (busStopGroup.containsStation()) {
            initBusStopStations(busStopGroup);
        } else {
            initBusStopGroup(busStopGroup);
        }
    }

    private void initBusStopGroup(BusStopGroup busStopGroup) {
        enableStationSelection(false);

        for (final BusStop busStop : busStopGroup.getBusStops()) {
            RadioButton rdBtnBusStop = new RadioButton(this);
            rdBtnBusStop.setText(busStop.getDirection());
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

    private void enableStationSelection(boolean enabled) {
        if (enabled) {
            _rdgSelectedDestination.setVisibility(View.GONE);
            _spStations.setVisibility(View.VISIBLE);
        }
        else {
            _spStations.setVisibility(View.GONE);
            _rdgSelectedDestination.setVisibility(View.VISIBLE);
        }
    }

    private void initBusStopStations(BusStopGroup busStopGroup) {
        enableStationSelection(true);

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
    }

    private void preselectDirection() {
        RadioButton selectedDirection = (RadioButton) _rdgSelectedDestination.getChildAt(0);
        selectedDirection.setChecked(true);
    }

    private void updateDepartures(BusStop busStop) {
        Log.d(TAG, "New departure: " + busStop.toString());

        displayLoadingPanel(true);

        new BusStopRequest().execute(busStop);
    }

    private void displayLoadingPanel(boolean show) {
        _loadingPanel.setVisibility(show ? View.VISIBLE : View.GONE);
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
}
