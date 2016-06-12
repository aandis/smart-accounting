package help.smartbusiness.smartaccounting.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.io.File;

import help.smartbusiness.smartaccounting.Utils.FileUtils;
import help.smartbusiness.smartaccounting.Utils.SynchronousDrive;
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
        Intent intent = new Intent(context, ExportDbService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            boolean downloaded = searchAndDownloadBackup();
            if (downloaded) {
                importBackupToDb();
            }
        }
    }

    private boolean searchAndDownloadBackup() {
        SynchronousDrive drive = new SynchronousDrive(this);
        String backUpId = null;
        backUpId = drive.searchLatest(DbOperation.BACKUP_NAME, DbOperation.MIME_TYPE);
        if (backUpId != null) {
            try {
                File localBackupFile = new File(FileUtils.getFullPath(this, DbOperation.BACKUP_NAME));
                return drive.downloadFile(backUpId, localBackupFile);
            } finally {
                drive.disconnect();
            }
        }
        return false;
    }

    private boolean importBackupToDb() {
        DbOperation operation = new DbOperation();
        return operation.importDbFromLocal(this);
    }

    /**
     * Handle action Import in the provided background thread with the provided
     * parameters.
     */
    private void handleActionImport() {
    }
}
