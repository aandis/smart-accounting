package help.smartbusiness.smartaccounting.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.AuthHelper;
import help.smartbusiness.smartaccounting.Utils.DriverServicesHelper;
import help.smartbusiness.smartaccounting.Utils.FileUtils;
import help.smartbusiness.smartaccounting.Utils.GoogleHelper;
import help.smartbusiness.smartaccounting.Utils.NotificationHelper;
import help.smartbusiness.smartaccounting.activities.BackupActivity;
import help.smartbusiness.smartaccounting.backup.DbOperation;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class ExportDbService extends IntentService {

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
            boolean exportedToDrive = exportDbToDrive();
            if (exportedToDrive) {
                notificateSuccess();
            } else {
                notificateFailed();
            }
        } else {
            notificateFailed();
        }
    }

    private boolean exportDbToDrive() {
        DriverServicesHelper drive = GoogleHelper.getDriveHelper(this);
        if (drive != null) {
            File file = new File(FileUtils.getFullPath(this, DbOperation.BACKUP_NAME));
            String mime = DbOperation.MIME_TYPE;
            String driveId = drive.uploadFile(file, mime);
            if (driveId != null) {
                Log.d(TAG, "Uploaded file with id " + driveId);
                return true;
            } else {
                Log.d(TAG, "Couldn't backup!");
                return false;
            }
        } else {
            Log.d(TAG, "Driver helper null, signing out user.");
            AuthHelper.signOutUser(this);
            return false;
        }
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
        NotificationHelper.simpleNotification(
            this,
            R.string.notification_backup_done,
            R.string.notification_backup_done_assure
        );
    }

    private void notificateFailed() {
        cancelProgress();
        NotificationHelper.actionNotification(
                this,
                R.string.notification_backup_failed,
                R.string.notification_backup_failed_detail,
                R.string.notification_backup_failed_detail_full,
                new Intent(this, BackupActivity.class)
        );
    }

    private void cancelProgress() {
//        PugNotification.with(this)
//                .cancel(R.id.export_notify_id);
    }
}
