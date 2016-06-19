package help.smartbusiness.smartaccounting.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;

import br.com.goncalves.pugnotification.notification.PugNotification;
import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.FileUtils;
import help.smartbusiness.smartaccounting.Utils.SynchronousDrive;
import help.smartbusiness.smartaccounting.activities.BackupActivity;
import help.smartbusiness.smartaccounting.activities.MainActivity;
import help.smartbusiness.smartaccounting.backup.DbOperation;

/**
 * Created by gamerboy on 8/6/16.
 */
public class ImportDbService extends IntentService implements GoogleApiClient.OnConnectionFailedListener {

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
        SynchronousDrive drive = new SynchronousDrive(this, this);
        String backUpId = null;
        backUpId = drive.searchLatest(DbOperation.BACKUP_NAME, DbOperation.MIME_TYPE);
        try {
            if (backUpId != null) {
                File localBackupFile = new File(FileUtils.getFullPath(this, DbOperation.BACKUP_NAME));
                return drive.downloadFile(backUpId, localBackupFile);
            } else {
                notificateNoBackup();
            }
        } finally {
            drive.disconnect();
        }
        return false;
    }

    private boolean importBackupToDb() {
        DbOperation operation = new DbOperation();
        return operation.importDbFromLocal(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateNotificationProgress(int progress) {
        PugNotification.with(this)
                .load()
                .identifier(R.id.import_notify_id)
                .ongoing(true)
                .title(R.string.notification_import_importing)
                .smallIcon(R.drawable.pugnotification_ic_launcher) // TODO
                .progress()
                .value(progress, 1, false)
                .build();
    }

    private void notificateSuccess() {
        cancelProgress();
        PugNotification.with(this)
                .load()
                .title(R.string.notification_import_done)
                .autoCancel(true)
                .message(R.string.notification_import_assure)
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void notificateNoBackup() {
        cancelProgress();
        PugNotification.with(this)
                .load()
                .title(R.string.notification_import_nobackup)
                .autoCancel(true)
                .message(R.string.notification_import_nobackup_detail)
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
                .title(R.string.notification_import_failed)
                .message(R.string.notification_import_failed_detail)
                .bigTextStyle(R.string.notification_import_failed_detail_full)
                .smallIcon(R.drawable.pugnotification_ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
    }

    private void cancelProgress() {
        PugNotification.with(this)
                .cancel(R.id.import_notify_id);
    }

}
