package de.doaschdn.muensterbus

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import butterknife.ButterKnife
import butterknife.bindView

class DepartureRow : LinearLayout {
    val _tvBusLine: TextView by bindView(R.id.bus_line)
    val _tvDepartureTimeLive: TextView by bindView(R.id.departure_time_live)
    val _tvDepartureTimeCalculated: TextView by bindView(R.id.departure_time_calculated)

    val _view: View = View.inflate(context, R.layout.departure_list_row, this)

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        ButterKnife.bind(_view)
    }

    fun setBusLine(busLine: String) {
        _tvBusLine.text = busLine
    }

    fun setDepartureTimeLive(time: String) {
        _tvDepartureTimeLive.text = time
    }

    fun setDepartureTimeCalculated(time: String) {
        _tvDepartureTimeCalculated.text = time
    }
}
