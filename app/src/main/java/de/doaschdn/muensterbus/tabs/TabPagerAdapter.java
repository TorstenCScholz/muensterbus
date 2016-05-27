package de.doaschdn.muensterbus.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_TABS = 2;

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BusStopListingFragment();
            case 1:
                return new FavoritesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }
}
