package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import help.smartbusiness.smartaccounting.SmartAccounting;

import static android.content.Context.MODE_PRIVATE;

public class AuthHelper {
    public static final String TAG = AuthHelper.class.getSimpleName();

    private static final String GOOGLE_LOGGED_IN = "logged_in";
    private static final String ACCOUNTING_PREFERENCES =
            SmartAccounting.class.getPackage().getName() + ".preferences";

    public static boolean isSignedIn(Context context) {
        return getPreferences(context).getBoolean(GOOGLE_LOGGED_IN, false);
    }

    public static void signInUser(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(GOOGLE_LOGGED_IN, true).apply();
    }

    public static void signOutUser(Context context) {
        GoogleHelper.signOutUser(context);
        getPreferences(context).edit().putBoolean(GOOGLE_LOGGED_IN, false).apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(ACCOUNTING_PREFERENCES, MODE_PRIVATE);
    }
}
