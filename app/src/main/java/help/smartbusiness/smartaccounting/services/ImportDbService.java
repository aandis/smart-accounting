package help.smartbusiness.smartaccounting.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.DriverServicesHelper;
import help.smartbusiness.smartaccounting.Utils.FileUtils;
import help.smartbusiness.smartaccounting.activities.MainActivity;
import help.smartbusiness.smartaccounting.backup.DbOperation;

/**
 * Created by gamerboy on 8/6/16.
 */
public class ImportDbService extends IntentService {

    public static final String TAG = ImportDbService.class.getSimpleName();

    public ImportDbService(String name) {
        super(name);
    }

    public ImportDbService() {
        super("ImportDbService");
    }

    /**
     * Starts this service to perform action Import. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startImport(Context context) {
        Intent intent = new Intent(context, ImportDbService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "here");
            updateNotificationProgress(0); // 0/1
            boolean downloaded = searchAndDownloadBackup();
            Log.d(TAG, downloaded + "");
            if (downloaded) {
                boolean imported = importBackupToDb();
                if (imported) {
                    notificateSuccess();
                } else {
                    notificateFailed();
                }
            } else {
                notificateFailed();
            }
        }
    }

    private boolean searchAndDownloadBackup() {
        DriverServicesHelper drive = getDriveHelper();
        String backUpId = null;
        backUpId = drive.searchLatest(DbOperation.BACKUP_NAME, DbOperation.MIME_TYPE);
        if (backUpId != null) {
            File localBackupFile = new File(FileUtils.getFullPath(this, DbOperation.BACKUP_NAME));
            return drive.downloadFile(backUpId, DbOperation.MIME_TYPE, localBackupFile);
        } else {
            Log.d(TAG, "No backup");
            notificateNoBackup();
        }
        return false;
    }

    private boolean importBackupToDb() {
        DbOperation operation = new DbOperation();
        return operation.importDbFromLocal(this);
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
//        try {
//            Tasks.await(signInAccountTask);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if (signInAccountTask.isSuccessful()) {
        if (signInAccountTask != null) {
            Log.d(TAG, signInAccountTask.getEmail());
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            this, Arrays.asList(DriveScopes.DRIVE_APPDATA)
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
        } else {
            Log.d(TAG, "megathread");
        }
        return helper;
    }

    private void updateNotificationProgress(int progress) {
//        PugNotification.with(this)
//                .load()
//                .identifier(R.id.import_notify_id)
//                .ongoing(true)
//                .title(R.string.notification_import_importing)
//                .smallIcon(R.drawable.pugnotification_ic_launcher) // TODO
//                .progress()
//                .value(progress, 1, false)
//                .build();
    }

    private void notificateSuccess() {
        cancelProgress();
//        PugNotification.with(this)
//                .load()
//                .title(R.string.notification_import_done)
//                .autoCancel(true)
//                .message(R.string.notification_import_assure)
//                .smallIcon(R.drawable.pugnotification_ic_launcher)
//                .flags(Notification.DEFAULT_ALL)
//                .simple()
//                .build();
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void notificateNoBackup() {
        cancelProgress();
//        PugNotification.with(this)
//                .load()
//                .title(R.string.notification_import_nobackup)
//                .autoCancel(true)
//                .message(R.string.notification_import_nobackup_detail)
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
//                .title(R.string.notification_import_failed)
//                .message(R.string.notification_import_failed_detail)
//                .bigTextStyle(R.string.notification_import_failed_detail_full)
//                .smallIcon(R.drawable.pugnotification_ic_launcher)
//                .flags(Notification.DEFAULT_ALL)
//                .simple()
//                .build();
    }

    private void cancelProgress() {
//        PugNotification.with(this)
//                .cancel(R.id.import_notify_id);
    }

}
