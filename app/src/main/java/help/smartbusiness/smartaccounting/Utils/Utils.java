package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by gamerboy on 28/5/16.
 */
public class Utils {
    public static float parseFloat(String number) {
        try {
            return Float.parseFloat(number);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static Long parseLong(String number) {
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException ex) {
            return -1l;
        }
    }

    public static void notifyError(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
