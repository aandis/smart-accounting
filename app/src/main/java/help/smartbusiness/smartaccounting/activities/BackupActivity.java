package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            logEvent("app_login", "email", account.getEmail());
            AuthHelper.signInUser(this);
            onLoggedIn();
        } catch (ApiException e) {
            int code = e.getStatusCode();
            String errorDescription = GoogleSignInStatusCodes.getStatusCodeString(code);
            report("signInResult:failed code=" + e.getStatusCode() + " error=" + errorDescription);
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode() + " error=" + errorDescription);

            // Let's retry.
            startActivity(new Intent(this, BackupActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
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
