package help.smartbusiness.smartaccounting.activities.helpers

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import help.smartbusiness.smartaccounting.activities.TransactionListActivity
import help.smartbusiness.smartaccounting.fragments.MultiDatePickerFragment

class FilterTransaction {
    companion object {
        @JvmStatic
        fun showDialog(context: Context, customerId: Long) {
            val datePickerFragment = MultiDatePickerFragment()
            datePickerFragment.onDateSetCallback = { fromDate: String, toDate: String ->
                val filterIntent = Intent(context, TransactionListActivity::class.java)
                filterIntent.putExtra(TransactionListActivity.CUSTOMER_ID, customerId)
                filterIntent.putExtra(TransactionListActivity.FILTER_FROM_DATE, fromDate)
                filterIntent.putExtra(TransactionListActivity.FILTER_TO_DATE, toDate)
                context.startActivity(filterIntent)
                datePickerFragment.dismiss()
            }
            datePickerFragment.show((context as FragmentActivity).supportFragmentManager, "filterDatePicker")
        }

        @JvmStatic
        fun getFilterQuery(fromDate: String?, toDate: String?): String? {
            fromDate ?: return null
            toDate ?: return null
            return "date >= ? AND date <= ?"
        }

        @JvmStatic
        fun getFilterArgs(fromDate: String?, toDate: String?): Array<String>? {
            fromDate ?: return null
            toDate ?: return null
            return arrayOf(fromDate, toDate)
        }
    }
}