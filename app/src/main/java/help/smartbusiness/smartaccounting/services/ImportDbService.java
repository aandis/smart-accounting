package help.smartbusiness.smartaccounting.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;

import help.smartbusiness.smartaccounting.Utils.AuthHelper;
import help.smartbusiness.smartaccounting.Utils.DriverServicesHelper;
import help.smartbusiness.smartaccounting.Utils.FileUtils;
import help.smartbusiness.smartaccounting.Utils.GoogleHelper;
import help.smartbusiness.smartaccounting.activities.MainActivity;
import help.smartbusiness.smartaccounting.backup.DbOperation;

/**
 * Created by gamerboy on 8/6/16.
 */
public class ImportDbService extends IntentService {

    public static final String TAG = ImportDbService.class.getSimpleName();

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
        DriverServicesHelper drive = GoogleHelper.getDriveHelper(this);
        if (drive != null) {
            String backUpId;
            backUpId = drive.searchLatest(DbOperation.BACKUP_NAME, DbOperation.MIME_TYPE);
            if (backUpId != null) {
                Log.d(TAG, String.format("Backup found - %s", backUpId));
                File localBackupFile = new File(FileUtils.getFullPath(this, DbOperation.BACKUP_NAME));
                return drive.downloadFile(backUpId, localBackupFile);
            }
            notificateNoBackup();
            return false;
        }
        Log.d(TAG, "Driver helper null, signing out user.");
        AuthHelper.signOutUser(this);
        // TODO Add signin notification.
        return false;
    }

    private boolean importBackupToDb() {
        DbOperation operation = new DbOperation();
        return operation.importDbFromLocal(this);
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
