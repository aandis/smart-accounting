package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import help.smartbusiness.smartaccounting.Utils.AuthHelper;
import help.smartbusiness.smartaccounting.Utils.GoogleHelper;

public class BackupActivity extends SmartAccountingActivity {

    public static final String TAG = BackupActivity.class.getSimpleName();

    public static final String LOGOUT_REQUEST = "logout";

    private static final int REQUEST_CODE_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(LOGOUT_REQUEST)) {
            logoutUser();
            requestSignIn();
        } else {
            if (AuthHelper.isSignedIn(this)) {
                onLoggedIn();
            } else {
                requestSignIn();
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
                logEvent("app_login", "email", googleAccount.getEmail());
                AuthHelper.signInUser(this);
                onLoggedIn();
            })
//        .addOnFailureListener() TODO
        ;
    }

    private void logoutUser() {
        AuthHelper.signOutUser(this);
        getIntent().removeExtra(LOGOUT_REQUEST);
    }

    private void requestSignIn() {
        GoogleSignInClient client = GoogleHelper.getSignInClient(this);
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }
}
