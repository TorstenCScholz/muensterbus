package de.doaschdn.muensterbus.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.doaschdn.muensterbus.BusStop;
import de.doaschdn.muensterbus.BusStopGroup;
import de.doaschdn.muensterbus.FavoritesAdapter;
import de.doaschdn.muensterbus.R;
import de.doaschdn.muensterbus.thirdparty.DividerItemDecoration;

public class FavoritesFragment extends Fragment {
    RecyclerView _favoritesView;
    View _view;

    public FavoritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_favorites, container, false);

        _favoritesView = (RecyclerView)_view.findViewById(R.id.favorites_list);
        _favoritesView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _favoritesView.setLayoutManager(llm);
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), null);
        _favoritesView.addItemDecoration(divider);

        List<BusStopGroup> favorites = new ArrayList<BusStopGroup>() {{
            add(new BusStopGroup(new LinkedList<BusStop>() {{
                add(new BusStop("4146101", "Aegidiitor", "auswärts"));
                add(new BusStop("4146102", "Aegidiitor", "einwärts"));
            }}));
            add(new BusStopGroup(new LinkedList<BusStop>() {{
                add(new BusStop("4238107", "Danziger Freiheit", "einwärts", "A"));
                add(new BusStop("4238101", "Danziger Freiheit", "auswärts", "B"));
                add(new BusStop("4238108", "Danziger Freiheit", "Endhaltestelle", "C"));
                add(new BusStop("4238103", "Danziger Freiheit", "auswärts", "D"));
            }}));
            add(new BusStopGroup(new LinkedList<BusStop>() {{
                add(new BusStop("4294202", "Emsstraße", "einwärts"));
            }}));
            add(new BusStopGroup(new LinkedList<BusStop>() {{
                add(new BusStop("4140101", "Schützenstraße", "auswärts"));
                add(new BusStop("4140102", "Schützenstraße", "einwärts"));
            }}));
        }};
        FavoritesAdapter adapter = new FavoritesAdapter(getActivity(), favorites);
        _favoritesView.setAdapter(adapter);

        return _view;
    }
}
