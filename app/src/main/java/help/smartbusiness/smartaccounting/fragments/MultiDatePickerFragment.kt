package help.smartbusiness.smartaccounting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import help.smartbusiness.smartaccounting.R
import kotlinx.android.synthetic.main.fragment_multi_date_picker.*
import org.threeten.bp.format.DateTimeFormatter

/**
 * A multi date picker dialog [Fragment] to select a range of dates.
 */
class MultiDatePickerFragment : DialogFragment() {
    private val TAG: String? = MultiDatePickerFragment::class.simpleName

    private var dateFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    lateinit var onDateSetCallback: ((fromDate: String, toDate: String) -> Unit)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multi_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        date_range_filter_button.setOnClickListener(filterClickListener)
    }

    private val filterClickListener: View.OnClickListener = View.OnClickListener {
        val dateRange = date_range_calendar.selectedDates
        if (dateRange.size == 0) {
            Toast.makeText(context, R.string.filter_date_range_bad, Toast.LENGTH_SHORT).show()
        } else {
            val fromDate = dateRange.first().date.format(dateFormat)
            val toDate = dateRange.last().date.format(dateFormat)
            onDateSetCallback.invoke(fromDate, toDate)
            dismiss()
        }
    }
}