package help.smartbusiness.smartaccounting.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.util.Collections;
import java.util.Objects;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.DriverServicesHelper;
import help.smartbusiness.smartaccounting.Utils.FileUtils;
import help.smartbusiness.smartaccounting.backup.DbOperation;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class ExportDbService extends IntentService implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = ExportDbService.class.getSimpleName();

    /**
     * Starts this service to perform action Export. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startExport(Context context) {
        Intent intent = new Intent(context, ExportDbService.class);
        context.startService(intent);
    }

    public ExportDbService() {
        super("ExportDbService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            updateNotificationProgress(0); // 0/2
            exportDb();
        }
    }

    private void exportDb() {
        DbOperation operation = new DbOperation();
        boolean exportedToLocal = operation.exportDbToLocal(this);
        if (exportedToLocal) {
            updateNotificationProgress(1); // 1/2
            exportDbToDrive();
        } else {
            notificateFailed();
        }
    }

    private void exportDbToDrive() {
        DriverServicesHelper drive = getDriveHelper();
        File file = new File(FileUtils.getFullPath(this, DbOperation.BACKUP_NAME));
        String mime = DbOperation.MIME_TYPE;
        String driveId;
        driveId = drive.uploadFile(file, mime);
        if (driveId != null) {
            Log.d(TAG, "Uploaded file with id " + driveId);
            notificateSuccess();
        } else {
            Log.d(TAG, "is null");
            notificateFailed();
        }
    }

    private GoogleSignInOptions getGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                .build();
    }

    private DriverServicesHelper getDriveHelper() {
        DriverServicesHelper helper = null;

        GoogleSignInClient client = GoogleSignIn.getClient(this, getGoogleSignInOptions());
        GoogleSignInAccount signInAccountTask = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccountTask != null) {
            Log.d(TAG, signInAccountTask.getEmail());
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            this, Collections.singleton(DriveScopes.DRIVE_APPDATA)
                    );
            credential.setSelectedAccount(Objects.requireNonNull(signInAccountTask).getAccount());
            Drive googleDriveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential
            )
            .setApplicationName(getResources().getString(R.string.app_name))
            .build();
            helper = new DriverServicesHelper(googleDriveService);
        }
        return helper;
    }

    private void updateNotificationProgress(int progress) {
//        PugNotification.with(this)
//                .load()
//                .identifier(R.id.export_notify_id)
//                .ongoing(true)
//                .title(R.string.notification_backup_backing)
//                .smallIcon(R.drawable.pugnotification_ic_launcher) // TODO
//                .progress()
//                .value(progress, 2, false)
//                .build();
    }

    private void notificateSuccess() {
        cancelProgress();
//        PugNotification.with(this)
//                .load()
//                .title(R.string.notification_backup_done)
//                .autoCancel(true)
//                .message(R.string.notification_backup_done_assure)
//                .smallIcon(R.drawable.pugnotification_ic_launcher)
//                .flags(Notification.DEFAULT_ALL)
//                .simple()
//                .build();
    }

    private void notificateFailed() {
        cancelProgress();
//        PugNotification.with(this)
//                .load()
//                .click(BackupActivity.class, null)
//                .title(R.string.notification_backup_failed)
//                .message(R.string.notification_backup_failed_detail)
//                .bigTextStyle(R.string.notification_backup_failed_detail_full)
//                .smallIcon(R.drawable.pugnotification_ic_launcher)
//                .flags(Notification.DEFAULT_ALL)
//                .simple()
//                .build();
    }

    private void cancelProgress() {
//        PugNotification.with(this)
//                .cancel(R.id.export_notify_id);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "failed");
    }
}
