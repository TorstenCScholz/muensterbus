package de.doaschdn.muensterbus.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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
            add(new BusStopGroup(new BusStop("0", "Test Station 1", "einwärts")));
            add(new BusStopGroup(new BusStop("1", "Test Station 2", "auswärts")));
            add(new BusStopGroup(new BusStop("2", "Test Station 3", "Endstation")));
            add(new BusStopGroup(new BusStop("3", "Test Station 4", "unbekannt")));
        }};
        FavoritesAdapter adapter = new FavoritesAdapter(getActivity(), favorites);
        _favoritesView.setAdapter(adapter);

        return _view;
    }
}
