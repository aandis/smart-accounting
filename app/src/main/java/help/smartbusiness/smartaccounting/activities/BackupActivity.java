package help.smartbusiness.smartaccounting.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.utils.AuthHelper;
import help.smartbusiness.smartaccounting.utils.GoogleHelper;

public class BackupActivity extends SmartAccountingActivity {

    public static final String TAG = BackupActivity.class.getSimpleName();

    public static final String LOGOUT_REQUEST = "logout";

    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        final int rc = result.getResultCode();
                        final Intent data = result.getData();

                        Log.d(TAG, "SignIn rc=" + rc + ", hasData=" + (data != null));
                        if (data != null && data.getExtras() != null && !data.getExtras().isEmpty()) {
                            StringBuilder keys = new StringBuilder();
                            for (String k : data.getExtras().keySet()) {
                                if (keys.length() > 0) keys.append(", ");
                                keys.append(k);
                            }
                            Log.d(TAG, "SignIn intent extras keys: [" + keys + "]");
                        }

                        if (rc == Activity.RESULT_OK && data != null) {
                            handleSignInResult(data);
                        } else if (rc == Activity.RESULT_CANCELED) {
                            if (data != null) {
                                try {
                                    // Will usually throw; we catch to log the specific status code.
                                    GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                                    Log.w(TAG, "RESULT_CANCELED but no ApiException thrown (unexpected).");
                                } catch (ApiException e) {
                                    int code = e.getStatusCode();
                                    String desc = GoogleSignInStatusCodes.getStatusCodeString(code);
                                    Log.w(TAG, "RESULT_CANCELED: ApiException code=" + code + " (" + desc + ")", e);
                                }
                            } else {
                                Log.w(TAG, "RESULT_CANCELED with null data (user dismissed or activity finished).");
                            }
                            finish(); // keep your original behavior
                        } else {
                            if (data != null) {
                                try {
                                    GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                                } catch (ApiException e) {
                                    int code = e.getStatusCode();
                                    String desc = GoogleSignInStatusCodes.getStatusCodeString(code);
                                    Log.w(TAG, "Unexpected rc=" + rc + ", ApiException=" + code + " (" + desc + ")", e);
                                }
                            }
                            report("Unknown resultcode from signin dialog " + rc);
                        }
                    }
            );

    private static String signingSha1(Context c) {
        try {
            var pm = c.getPackageManager();
            byte[] sig = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                var pkgInfo = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                if (pkgInfo.signingInfo != null && pkgInfo.signingInfo.getApkContentsSigners().length > 0) {
                    sig = pkgInfo.signingInfo.getApkContentsSigners()[0].toByteArray();
                }
            } else {
                var pkgInfo = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_SIGNATURES);
                if (pkgInfo.signatures != null && pkgInfo.signatures.length > 0) {
                    sig = pkgInfo.signatures[0].toByteArray();
                }
            }
            if (sig == null) return "unknown";
            var md = java.security.MessageDigest.getInstance("SHA1");
            byte[] d = md.digest(sig);
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02X:", b));
            return sb.substring(0, sb.length()-1);
        } catch (Exception e) { return "unknown"; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "default_web_client_id=" + getString(R.string.default_web_client_id));
        Log.d(TAG, "Package=" + getPackageName() + " signing SHA1=" + signingSha1(this));
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
            Log.e(TAG, "signInResult:failed code=" + code + " error=" + errorDescription);
            report("signInResult:failed code=" + code + " error=" + errorDescription);

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
