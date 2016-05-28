package de.doaschdn.muensterbus.tabs;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.List;

import de.doaschdn.muensterbus.BusStop;
import de.doaschdn.muensterbus.BusStopGroup;
import de.doaschdn.muensterbus.BusStopSpinnerWrapper;
import de.doaschdn.muensterbus.Departure;
import de.doaschdn.muensterbus.DepartureAdapter;
import de.doaschdn.muensterbus.R;
import de.doaschdn.muensterbus.SWMApiEndpointInterface;
import de.doaschdn.muensterbus.SWMClient;
import de.doaschdn.muensterbus.SWMParser;
import de.doaschdn.muensterbus.SearchContentProvider;
import de.doaschdn.muensterbus.thirdparty.DividerItemDecoration;

public class BusStopListingFragment extends Fragment {
    private final static String TAG = "BusStopListingFragment";

    View _view;
    SwipeRefreshLayout _swipeRefreshLayout;
    RecyclerView _departureView;
    RadioGroup _rdgSelectedDestination;
    Spinner _spStations;
    RelativeLayout _loadingPanel;

    private BusStopGroup _selectedBusStopGroup;

    public BusStopListingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_bus_stop_listing, container, false);

        initBindings();
        initDepartureView();

        _swipeRefreshLayout.setColorSchemeResources(R.color.primary_dark, R.color.primary, R.color.primary_light);
        _swipeRefreshLayout.setOnRefreshListener(getOnRefreshListener());

        return _view;
    }

    private void initBindings() {
        _swipeRefreshLayout = (SwipeRefreshLayout)_view.findViewById(R.id.swipe_refresh);
        _departureView = (RecyclerView)_view.findViewById(R.id.departure_list);
        _rdgSelectedDestination = (RadioGroup)_view.findViewById(R.id.rdgSelectedDestination);
        _spStations = (Spinner)_view.findViewById(R.id.station_spinner);
        _loadingPanel = (RelativeLayout)_view.findViewById(R.id.loading_panel);
    }

    private void initDepartureView() {
        _departureView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _departureView.setLayoutManager(llm);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), null);
        _departureView.addItemDecoration(divider);

        displayLoadingPanel(false);
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

    private void updateDepartures(BusStop busStop) {
        Log.d(TAG, "New departure: " + busStop.toString());

        displayLoadingPanel(true);

        new BusStopRequest().execute(busStop);
    }

    public void setBusStopGroup(BusStopGroup busStopGroup) {
        TextView tvBusStopName = (TextView) _view.findViewById(R.id.busstop_name);
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
            RadioButton rdBtnBusStop = new RadioButton(getContext());
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
                getContext(),
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

    private void displayLoadingPanel(boolean show) {
        _loadingPanel.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void executeQuery(String query) {
        new QuerySearchForSpecificTerm().execute(query);
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
                new AlertDialog.Builder(getContext())
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
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    _swipeRefreshLayout.setRefreshing(false);
//                    displayLoadingPanel(false);
//                }
//            });

            DepartureAdapter adapter = new DepartureAdapter(getContext(), departureList);
            _departureView.setAdapter(adapter);
        }
    }
}
