package de.doaschdn.muensterbus;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    //@Bind(R.id.swipe_refresh) SwipeRefreshLayout _swipeRefreshLayout;
    LinearLayout _departureList;
    SwipeRefreshLayout _swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.main_activity);
        _swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        _departureList = (LinearLayout)findViewById(R.id.departure_list);

        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            private int _busLine = 0;
            @Override
            public void onRefresh() {
                if (_busLine >= 5) {
                    clearDepartures();
                    _busLine = 0;
                }
                addDeparture(new Departure("Linie " + ++_busLine));
                _swipeRefreshLayout.setRefreshing(false);
            }
        });
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
}
