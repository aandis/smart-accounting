package help.smartbusiness.smartaccounting.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.utils.AuthHelper;
import help.smartbusiness.smartaccounting.utils.DriverServicesHelper;
import help.smartbusiness.smartaccounting.utils.FileUtils;
import help.smartbusiness.smartaccounting.utils.GoogleHelper;
import help.smartbusiness.smartaccounting.utils.NotificationHelper;
import help.smartbusiness.smartaccounting.activities.BackupActivity;
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
            setupNotificationChannel();
            updateNotificationProgress(0); // 0/1
            boolean downloaded = searchAndDownloadBackup();
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
            } else {
                notificateNoBackup();
                return false;
            }
        } else {
            Log.d(TAG, "Driver helper null, signing out user.");
            AuthHelper.signOutUser(this);
            return false;
        }
    }

    private boolean importBackupToDb() {
        DbOperation operation = new DbOperation();
        return operation.importDbFromLocal(this);
    }

    private void updateNotificationProgress(int progress) {
        NotificationHelper.stickyNotification(
            this,
            R.string.notification_import_importing,
            R.string.notification_import_importing_detail,
            R.id.import_notify_id
        );
    }

    private void notificateSuccess() {
        cancelProgress();
        NotificationHelper.simpleNotification(
            this,
            R.string.notification_import_done,
            R.string.notification_import_assure
        );
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void notificateNoBackup() {
        cancelProgress();
        NotificationHelper.simpleNotification(
            this,
            R.string.notification_import_nobackup,
            R.string.notification_import_nobackup_detail
        );
    }


    private void notificateFailed() {
        cancelProgress();
        NotificationHelper.actionNotification(
            this,
            R.string.notification_import_failed,
            R.string.notification_import_failed_detail,
            R.string.notification_import_failed_detail_full,
            new Intent(this, BackupActivity.class)
        );
    }

    private void cancelProgress() {
        NotificationHelper.cancelNotification(this, R.id.import_notify_id);
    }

    private void setupNotificationChannel() {
        NotificationHelper.createNotificationChannel(
            this,
            getString(R.string.notification_backup_import_channel_name),
            getString(R.string.notification_backup_import_channel_description)
        );
    }
}
