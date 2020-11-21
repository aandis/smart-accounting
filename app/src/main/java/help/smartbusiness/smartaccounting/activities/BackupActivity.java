package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;

public class BackupActivity extends SmartAccountingActivity {

    public static final String TAG = BackupActivity.class.getSimpleName();

    public static final String LOGOUT_REQUEST = "logout";
    public static final String GOOGLE_LOGGED_IN = "logged_in";
    public static final String ACCOUNTING_PREFERENCES =
            BackupActivity.class.getPackage().getName() + ".preferences";

    private static final int REQUEST_CODE_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(LOGOUT_REQUEST)) {
            logoutUser();
            buildDriveClient();
        } else {
            if (getPreferences().getBoolean(GOOGLE_LOGGED_IN, false)) {
                onLoggedIn();
            } else {
                buildDriveClient();
            }
        }
    }

    private void onLoggedIn() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult result) {
//        setContentView(R.layout.activity_backup);
//        if (!result.hasResolution()) {
//            // show the localized error dialog.
//            GoogleApiAvailability.getInstance().getErrorDialog(this,
//                    result.getErrorCode(),
//                    0).show();
//            return;
//        }
//        try {
//            result.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
//        } catch (IntentSender.SendIntentException e) {
//            report(e);
//            Log.e(TAG, "Exception while starting resolution activity", e);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                } else if (resultCode == RESULT_CANCELED) {
                    finish();
                } else {
                    report("Unknown resultcode from signin dialog " + resultCode);
                }
                break;
        }
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener(googleAccount -> {
                Log.d(TAG, "Signed in as " + googleAccount.getEmail());
                logEvent("app_login", "email", googleAccount.getEmail());

                SharedPreferences.Editor editor = getPreferences().edit();
                editor.putBoolean(GOOGLE_LOGGED_IN, true).apply();
                onLoggedIn();
            })
//        .addOnFailureListener() TODO
        ;
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(ACCOUNTING_PREFERENCES, MODE_PRIVATE);
    }

    private void logoutUser() {
        GoogleSignInClient client = GoogleSignIn.getClient(this, getGoogleSignInOptions());
        client.signOut();

        getPreferences().edit().putBoolean(GOOGLE_LOGGED_IN, false).apply();
        getIntent().removeExtra(LOGOUT_REQUEST);
    }

    private void buildDriveClient() {
        GoogleSignInClient client = GoogleSignIn.getClient(this, getGoogleSignInOptions());
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInOptions getGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .build();
    }
}
