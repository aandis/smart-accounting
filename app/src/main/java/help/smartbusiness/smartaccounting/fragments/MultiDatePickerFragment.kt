package help.smartbusiness.smartaccounting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import help.smartbusiness.smartaccounting.R
import help.smartbusiness.smartaccounting.databinding.FragmentMultiDatePickerBinding
import org.threeten.bp.format.DateTimeFormatter

/**
 * A multi date picker dialog [Fragment] to select a range of dates.
 */
class MultiDatePickerFragment : DialogFragment() {
    private val TAG: String? = MultiDatePickerFragment::class.simpleName
    private var _binding: FragmentMultiDatePickerBinding? = null
    private val binding get() = _binding!!

    private var dateFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    lateinit var onDateSetCallback: ((fromDate: String, toDate: String) -> Unit)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMultiDatePickerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dateRangeFilterButton.setOnClickListener(filterClickListener)
    }

    private val filterClickListener: View.OnClickListener = View.OnClickListener {
        val dateRange = binding.dateRangeCalendar.selectedDates
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