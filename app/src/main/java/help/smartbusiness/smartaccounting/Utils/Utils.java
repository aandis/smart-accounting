package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by gamerboy on 28/5/16.
 */
public class Utils {
    public static double parseDouble(String number) {
        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static Long parseLong(String number) {
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }

    /**
     * Converts a value represented in smallest unit as long to decimal format.
     * Exmaple -        242 become 2.42, 3693 becomes 36.93
     * @param longStr   The long value as {@link CharSequence}.
     * @return          The decimal value as string or null.
     */
    public static String convertLongToDecimal(CharSequence longStr) {
        if (longStr != null && !longStr.toString().isEmpty()) {
            long value = Utils.parseLong(longStr.toString());
            if (value != -1L) {
                return String.valueOf(value/100.0);
            }
        }
        return null;
    }

    public static void notifyError(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
