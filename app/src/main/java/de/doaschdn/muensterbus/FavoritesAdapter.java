package de.doaschdn.muensterbus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesListHolder> {

    private Context _context;
    private List<BusStopGroup> _busStopList;

    public FavoritesAdapter(Context context, List<BusStopGroup> busStopList) {
        _context = context;
        _busStopList = busStopList;
    }

    @Override
    public FavoritesListHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.favorites_list_row, viewGroup, false);

        return new FavoritesListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoritesListHolder holder, int position) {
        BusStopGroup busStopGroupInfo = _busStopList.get(position);
        holder.setBusStopGroup(busStopGroupInfo);
    }

    @Override
    public int getItemCount() {
        return _busStopList.size();
    }

    public static class FavoritesListHolder extends RecyclerView.ViewHolder {
        protected TextView _tvFavoritesLine;
        private BusStopGroup _busStopGroup;

        public FavoritesListHolder(View v) {
            super(v);

            _tvFavoritesLine = (TextView) v.findViewById(R.id.favorites_line);
            getLayoutPosition();
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d("TAG", "Clicked " + getBusStopGroup().getName() + "!");
                }
            });
        }

        public void setBusStopGroup(BusStopGroup busStopGroup) {
            _busStopGroup = busStopGroup;
            _tvFavoritesLine.setText(_busStopGroup.getName());
        }

        public BusStopGroup getBusStopGroup() {
            return _busStopGroup;
        }
    }
}
