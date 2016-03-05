package de.doaschdn.muensterbus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Torsten on 05.03.2016.
 */
public class DepartureAdapter extends RecyclerView.Adapter<DepartureAdapter.DepartureListHolder> {

    private Context _context;
    private List<Departure> _departureList;

    public DepartureAdapter(Context context, List<Departure> departureList) {
        _context = context;
        _departureList = departureList;
    }

    @Override
    public DepartureListHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.departure_list_row, viewGroup, false);

        return new DepartureListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DepartureListHolder holder, int position) {
        Departure departureInfo = _departureList.get(position);
        holder._tvBusLine.setText(_context.getResources().getString(R.string.busline) + " " + departureInfo.getBusLine());
        holder._tvArrivalIn.setText(departureInfo.getTimeType() == Departure.TimeType.NOW ? _context.getString(R.string.now) : departureInfo.getDepartureIn());
        holder._tvArrivalAt.setText(departureInfo.getDepartureAt());
    }

    @Override
    public int getItemCount() {
        return _departureList.size();
    }

    public static class DepartureListHolder extends RecyclerView.ViewHolder {
        protected TextView _tvBusLine;
        protected TextView _tvArrivalIn;
        protected TextView _tvArrivalAt;

        public DepartureListHolder(View v) {
            super(v);

            _tvBusLine = (TextView) v.findViewById(R.id.bus_line);
            _tvArrivalIn = (TextView) v.findViewById(R.id.departure_time_live);
            _tvArrivalAt = (TextView) v.findViewById(R.id.departure_time_calculated);
        }
    }
}
