package de.doaschdn.muensterbus;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //@Bind(R.id.swipe_refresh) SwipeRefreshLayout _swipeRefreshLayout;
    LinearLayout _departureList;
    SwipeRefreshLayout _swipeRefreshLayout;
    RadioButton _rdBtnInwards;
    RadioButton _rdBtnOutwards;
    EditText _etDestination;

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
            addDeparture(new Departure(busStop.getId() + ": " + busStop.getName() + ". " + orientation));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.main_activity);
        _swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        _departureList = (LinearLayout)findViewById(R.id.departure_list);
        _rdBtnInwards = (RadioButton)findViewById(R.id.rdBtnIn);
        _rdBtnOutwards = (RadioButton)findViewById(R.id.rdBtnOut);
        //_etDestination = (EditText)findViewById(R.id.destination);

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
        onSearchRequested();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            Log.d(TAG, "Handling search for: " + query);
            //List<Destination> destinations = SWMParser.parseSearchQueryResults(query);

            //displayDestinations(destinations);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addDeparture(Departure departure) {
        TextView tv = new TextView(this);
        tv.setText(departure.getBusLine());

        _departureList.addView(tv);
    }

    private void clearDepartures() {
        _departureList.removeAllViews();
    }

    /*private void setDestination(Destination destination) {
        RadioButton radioButton = destination.isInwards() ? _rdBtnInwards : _rdBtnOutwards;
        radioButton.setChecked(true);

        _etDestination.setText(destination.getBusStop());
    }*/
}
