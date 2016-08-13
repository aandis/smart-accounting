package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import help.smartbusiness.smartaccounting.R;

public class BackupActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = BackupActivity.class.getSimpleName();
    public static final String LOGOUT_REQUEST = "logout";
    public static final String ACCOUNTING_PREFERENCES =
            BackupActivity.class.getPackage().getName() + ".preferences";
    public static final String GOOGLE_LOGGED_IN = "logged_in";

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 10;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(LOGOUT_REQUEST)) {
            buildDriveClient();
        } else {
            if (getPreferences().getBoolean(GOOGLE_LOGGED_IN, false)) {
                onLoggedIn();
            } else {
                buildDriveClient();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        SharedPreferences.Editor editor = getPreferences().edit();
        if (getIntent().getBooleanExtra(LOGOUT_REQUEST, false)) {
            editor.putBoolean(GOOGLE_LOGGED_IN, false).apply();
            getIntent().removeExtra(LOGOUT_REQUEST);
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        } else {
            editor.putBoolean(GOOGLE_LOGGED_IN, true).apply();
            onLoggedIn();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private void onLoggedIn() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        setContentView(R.layout.activity_backup);
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this,
                    result.getErrorCode(),
                    0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(
                ACCOUNTING_PREFERENCES, MODE_PRIVATE);
    }

    private void buildDriveClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
}
