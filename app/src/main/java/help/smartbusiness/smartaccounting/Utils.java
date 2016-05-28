package help.smartbusiness.smartaccounting;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by gamerboy on 28/5/16.
 */
public class Utils {
    public static float parseNumber(String number) {
        try {
            return Float.parseFloat(number);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static void notifyError(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
