package de.doaschdn.muensterbus.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.doaschdn.muensterbus.R;

public class BusStopListingFragment extends Fragment {
    public BusStopListingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bus_stop_listing, container, false);
    }
}
