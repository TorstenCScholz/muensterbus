package de.doaschdn.muensterbus;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.doaschdn.muensterbus.tabs.BusStopListingFragment;
import de.doaschdn.muensterbus.tabs.TabPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    List<WeakReference<Fragment>> _fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //ButterKnife.bind(this);

        initTabLayout();

        handleIntent(getIntent());

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
    }

    private void initTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.bus_stop_listing)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.favorites)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        initTabAdapter(tabLayout);
    }

    @Override
    public void onAttachFragment (Fragment fragment) {
        _fragments.add(new WeakReference(fragment));
    }

    public List<Fragment> getActiveFragments() {
        ArrayList<Fragment> ret = new ArrayList<>();

        for(WeakReference<Fragment> ref : _fragments) {
            Fragment f = ref.get();
            if(f != null) {
                if(f.isVisible()) {
                    ret.add(f);
                }
            }
        }

        return ret;
    }

    private void initTabAdapter(TabLayout tabLayout) {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.tab_pager);
        final PagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
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
        BusStopListingFragment fragment = (BusStopListingFragment)getActiveFragments().get(0);

        if (query != null) {
            fragment.executeQuery(query);
        } else if (data != null) {
            fragment.setBusStopGroup(new Gson().fromJson(data.toString(), BusStopGroup.class));
        }
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
