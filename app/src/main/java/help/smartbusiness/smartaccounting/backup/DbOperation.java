package help.smartbusiness.smartaccounting.backup;

import android.content.Context;

import java.io.IOException;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;

/**
 * Created by gamerboy on 5/6/16.
 */
public final class DbOperation {

    public static final String BACKUP_NAME = "backup";
    public static final String MIME_TYPE = "application/octet-stream";

    public DbOperation() {

    }

    public boolean importDbFromLocal(Context context) {
        AccountingDbHelper helper = new AccountingDbHelper(context,
                AccountingDbHelper.DATABASE_NAME, null, AccountingDbHelper.DATABASE_VERSION);
        try {
            helper.importDatabase(context, BACKUP_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean exportDbToLocal(Context context) {
        AccountingDbHelper helper = new AccountingDbHelper(context,
                AccountingDbHelper.DATABASE_NAME, null, AccountingDbHelper.DATABASE_VERSION);
        try {
            helper.exportDatabase(context, BACKUP_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
