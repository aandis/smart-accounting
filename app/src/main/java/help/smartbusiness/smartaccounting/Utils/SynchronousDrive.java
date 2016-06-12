package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by gamerboy on 8/6/16.
 */
public class SynchronousDrive implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = SynchronousDrive.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    public SynchronousDrive(Context context) {
        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private boolean connected() {
        if (!mGoogleApiClient.isConnected()) {
            ConnectionResult result = mGoogleApiClient.blockingConnect();
            return result.isSuccess();
        }
        return false;
    }

    public String uploadFile(File file, String mimeType) {
        if (connected()) {
            try {
                DriveFolder appFolder = getAppFolder();
                if (appFolder != null) {
                    DriveContents cont = fileToContents(null, file);
                    if (cont == null) {
                        return null;
                    }
                    MetadataChangeSet meta = new MetadataChangeSet
                            .Builder()
                            .setTitle(file.getName())
                            .setMimeType(mimeType)
                            .build();
                    DriveFolder.DriveFileResult r1 = appFolder
                            .createFile(mGoogleApiClient, meta, cont)
                            .await();
                    DriveFile dFil = r1.getStatus().isSuccess()
                            ? r1.getDriveFile() : null;
                    if (dFil != null) {
                        // Necessary?
                        DriveResource.MetadataResult r2 = dFil
                                .getMetadata(mGoogleApiClient)
                                .await();
                        if (r2.getStatus().isSuccess()) {
                            return r2.getMetadata().getDriveId().encodeToString();
                        } else {
                            // TODO Couldn't get created file metadata.
                        }
                    } else {
                        // TODO Couldn't create file.
                    }
                } else {
                    // TODO Couldn't get app folder.
                }
            } catch (Exception e) {
                // TODO Exception while uploading.
            }
        }
        return null;
    }

    private DriveFolder getAppFolder() {
        return Drive.DriveApi.getAppFolder(mGoogleApiClient);
    }

    private DriveContents fileToContents(DriveContents cont, File file) {
        if (file == null) {
            return null;
        }
        if (cont == null) {
            DriveApi.DriveContentsResult contentsResult =
                    Drive.DriveApi.newDriveContents(mGoogleApiClient).await();
            cont = contentsResult.getStatus().isSuccess() ?
                    contentsResult.getDriveContents() : null;
        }
        if (cont != null) {
            try {
                OutputStream oos = cont.getOutputStream();
                if (oos != null) {
                    try {
                        InputStream is = new FileInputStream(file);
                        byte[] buf = new byte[4096];
                        int c;
                        while ((c = is.read(buf, 0, buf.length)) > 0) {
                            oos.write(buf, 0, c);
                            oos.flush();
                        }
                    } finally {
                        oos.close();
                    }
                } else {
                    // TODO OOS null
                }
                return cont;
            } catch (Exception ignore) {
                // TODO Exception while converting file to contents.
            }
        } else {
            // TODO Drive contents null.
        }
        return null;
    }

    public String searchLatest(String title, String mime) {
        if (connected()) {
            try {
                // Add query conditions, build query
                ArrayList<Filter> filters = new ArrayList<>();
                filters.add(Filters.in(SearchableField.PARENTS,
                        getAppFolder().getDriveId()));
                if (title != null) {
                    filters.add(Filters.eq(SearchableField.TITLE, title));
                }
                if (mime != null) {
                    filters.add(Filters.eq(SearchableField.MIME_TYPE, mime));
                }
                SortOrder sortOrder = new SortOrder.Builder()
                        .addSortDescending(SortableField.MODIFIED_DATE)
                        .build();
                Query query = new Query.Builder()
                        .setSortOrder(sortOrder)
                        .addFilter(Filters.and(filters))
                        .build();

                // fire the query
                DriveApi.MetadataBufferResult result = Drive.DriveApi
                        .query(mGoogleApiClient, query)
                        .await();
                if (result.getStatus().isSuccess()) {
                    MetadataBuffer mdb = null;
                    try {
                        mdb = result.getMetadataBuffer();
                        for (Metadata md : mdb) {
                            if (md == null || !md.isDataValid() || md.isTrashed()) {
                                continue;
                            }
                            return md.getDriveId().encodeToString();
                        }
                    } finally {
                        if (mdb != null) {
                            mdb.release();
                        }
                    }
                } else {
                    // TODO Query didn't run successfully.
                }
            } catch (Exception e) {
                // TODO Exception while searching.
            }
        }
        return null;
    }

    public boolean downloadFile(String driveId, File inFile) {
        if (connected()) {
            try {
                DriveFile df = DriveId.decodeFromString(driveId).asDriveFile();
                DriveApi.DriveContentsResult result = df
                        .open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                        .await();
                if (result.getStatus().isSuccess()) {
                    DriveContents cont = result.getDriveContents();
                    if (copyInputStreamToFile(cont.getInputStream(), inFile)) {
                        cont.discard(mGoogleApiClient);    // or cont.commit();  they are equiv if READONLY
                        return true;
                    }
                } else {
                    // TODO Couldn't open file with given id
                }
            } catch (Exception e) {
                // TODO Exception while downloading.
            }
        }
        return false;
    }

    private boolean copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}