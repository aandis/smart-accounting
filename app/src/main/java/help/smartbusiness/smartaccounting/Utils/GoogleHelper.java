package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;

import help.smartbusiness.smartaccounting.R;

public class GoogleHelper {

    public static final String TAG = GoogleHelper.class.getSimpleName();

    public static GoogleSignInOptions getSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                .build();
    }

    public static GoogleSignInClient getSignInClient(Context context) {
        return GoogleSignIn.getClient(context, getSignInOptions());
    }

    public static void signOutUser(Context context) {
        getSignInClient(context).signOut();
    }

    public static DriverServicesHelper getDriveHelper(Context context) {
        DriverServicesHelper helper = null;

        GoogleSignInAccount signedInAccount = GoogleSignIn.getLastSignedInAccount(context);
        if (signedInAccount != null) {
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                        context, Collections.singleton(DriveScopes.DRIVE_APPDATA)
                    );
            credential.setSelectedAccount(signedInAccount.getAccount());
            Drive googleDriveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential
            )
            .setApplicationName(context.getResources().getString(R.string.app_name))
            .build();
            helper = new DriverServicesHelper(googleDriveService);
        } else {
            Log.d(TAG, "No google account found!");
        }
        return helper;
    }
}
