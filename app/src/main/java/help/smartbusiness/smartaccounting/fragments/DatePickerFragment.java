package help.smartbusiness.smartaccounting.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import help.smartbusiness.smartaccounting.Utils.DateParser;

/**
 * Created by gamerboy on 25/5/16.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String DATE_VIEW_ID = "date_view_id";

    private int dateViewId;

    public static DatePickerFragment newInstance(int dateViewId) {
        Bundle args = new Bundle();
        args.putInt(DATE_VIEW_ID, dateViewId);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.dateViewId = getArguments().getInt(DATE_VIEW_ID);

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        TextView dateView = (TextView) getActivity().findViewById(dateViewId);
        String date = DateParser.padSqliteDate(
                String.format("%d-%d-%d", year, month + 1, day));
        if (date != null) {
            dateView.setText(date);
        } else {
            dateView.setText(null);
        }
    }
}