package help.smartbusiness.smartaccounting.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;

import br.com.goncalves.pugnotification.notification.PugNotification;
import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.FileUtils;
import help.smartbusiness.smartaccounting.Utils.SynchronousDrive;
import help.smartbusiness.smartaccounting.activities.BackupActivity;
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
            updateNotificationProgress(0);
            exportDb();
        }
    }

    private void exportDb() {
        DbOperation operation = new DbOperation();
        boolean exportedToLocal = operation.exportDbToLocal(this);
        if (exportedToLocal) {
            updateNotificationProgress(1);
            exportDbToDrive();
        } else {
            notificateFailed();
        }
    }

    private void exportDbToDrive() {
        SynchronousDrive drive = new SynchronousDrive(this, this);
        File file = new File(FileUtils.getFullPath(this, DbOperation.BACKUP_NAME));
        String mime = DbOperation.MIME_TYPE;
        String driveId;
        try {
            driveId = drive.uploadFile(file, mime);
            if (driveId != null) {
                Log.d(TAG, "Uploaded file with id " + driveId);
                notificateSuccess();
            } else {
                notificateFailed();
            }
        } finally {
            drive.disconnect();
        }
    }

    private void updateNotificationProgress(int progress) {
        PugNotification.with(this)
                .load()
                .identifier(R.id.export_notify_id)
                .ongoing(true)
                .title("Backing up database.")
                .smallIcon(R.drawable.pugnotification_ic_launcher) // TODO
                .progress()
                .value(progress, 2, false)
                .build();
    }

    private void notificateSuccess() {
        cancelProgress();
        PugNotification.with(this)
                .load()
                .title("Backup completed!")
                .message("Your data is now safely backed up.")
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
    }

    private void notificateFailed() {
        cancelProgress();
        PugNotification.with(this)
                .load()
                .click(BackupActivity.class, null)
                .title("Backup failed.")
                .message("There was an error during backup.")
                .bigTextStyle("There was an error while backing up your data. Please make sure you are connected to the internet and have sufficient storage space in your phone.")
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
    }

    private void cancelProgress() {
        PugNotification.with(this)
                .cancel(R.id.export_notify_id);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "failed");
    }
}
