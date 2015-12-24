package de.doaschdn.muensterbus;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO: document your custom view class.
 */
public class DepartureRow extends LinearLayout {
    //@Bind(R.id.bus_line)
    TextView _tvBusLine;
    //@Bind(R.id.departure_time_live)
    TextView _tvDepartureTimeLive;
    //@Bind(R.id.departure_time_calculated)
    TextView _tvDepartureTimeCalculated;

    View _view;

    public DepartureRow(Context context) {
        super(context);
        init(null, 0);
    }

    public DepartureRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DepartureRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        _view = inflate(getContext(), R.layout.departure_list_row, this);

        //ButterKnife.bind(_view);

        _tvBusLine = (TextView)_view.findViewById(R.id.bus_line);
        _tvDepartureTimeLive = (TextView)_view.findViewById(R.id.departure_time_live);
        _tvDepartureTimeCalculated = (TextView)_view.findViewById(R.id.departure_time_calculated);
    }

    public void setBusLine(String busLine) {
        _tvBusLine.setText(busLine);
    }

    public void setDepartureTimeLive(String time) {
        _tvDepartureTimeLive.setText(time);
    }

    public void setDepartureTimeCalculated(String time) {
        _tvDepartureTimeCalculated.setText(time);
    }
}
