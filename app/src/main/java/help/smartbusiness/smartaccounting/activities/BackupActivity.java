package help.smartbusiness.smartaccounting.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import help.smartbusiness.smartaccounting.utils.AuthHelper;
import help.smartbusiness.smartaccounting.utils.GoogleHelper;

public class BackupActivity extends SmartAccountingActivity {

    public static final String TAG = BackupActivity.class.getSimpleName();

    public static final String LOGOUT_REQUEST = "logout";

    private final ActivityResultLauncher<Intent> signInLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    handleSignInResult(result.getData());
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    finish();
                } else {
                    report("Unknown resultcode from signin dialog " + result.getResultCode());
                }
            }
        );

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
        signInLauncher.launch(client.getSignInIntent());
    }
}
