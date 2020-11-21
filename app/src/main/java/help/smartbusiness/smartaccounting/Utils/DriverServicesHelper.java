package help.smartbusiness.smartaccounting.Utils;

import android.util.Log;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
public class DriverServicesHelper {
    public static final String TAG = DriverServicesHelper.class.getCanonicalName();
    private final Drive mDriveService;

    public DriverServicesHelper(Drive driveService) {
        mDriveService = driveService;
    }

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    public String uploadFile(java.io.File file, String mimeType) {
        FileContent fileContent = new FileContent("multipart/form-data", file);
        File metadata = new File()
                .setParents(Collections.singletonList("appDataFolder"))
                .setMimeType(mimeType)
                .setName(file.getName());

        try {
            File googleFile = mDriveService.files().create(metadata, fileContent).execute();
            return googleFile.getId();
        } catch (IOException ignored) {
            Log.d(TAG, ignored.toString());
        }
        return null;
    }

    public String searchLatest(String title, String mime) {
        FileList files1 = null;
        try {
            files1 = mDriveService.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name, modifiedTime)")
                    .setPageSize(10)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file : files1.getFiles()) {
            System.out.printf("Found file: %s (%s) %s\n",
                    file.getName(), file.getId(), file.getModifiedTime());
        }

        String query = String.format("name = '%s' and mimeType = '%s'", title, mime);
        Log.d(TAG, query);
        try {
            FileList result = mDriveService.files().list()
                    .setSpaces("appDataFolder")
                    .setQ(query)
                    .setOrderBy("modifiedTime desc")
                    .setPageToken(null)
                    .execute();
            List<File> files = result.getFiles();
            if (files.size() > 0) {
                return files.get(0).getId();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public boolean downloadFile(String driveId, String mime, java.io.File inFile) {
        try {
            OutputStream outputStream = new FileOutputStream(inFile);
            mDriveService.files().get(driveId).executeMediaAndDownloadTo(outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException ignored) {
            throw new RuntimeException(ignored);
        }
    }
}
