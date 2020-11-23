package help.smartbusiness.smartaccounting.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by gamerboy on 5/6/16.
 */
public class FileUtils {
    /**
     * Creates the specified <code>toFile</code> as a byte for byte copy of the
     * <code>fromFile</code>. If <code>toFile</code> already exists, then it
     * will be replaced with a copy of <code>fromFile</code>. The name and path
     * of <code>toFile</code> will be that of <code>toFile</code>.<br/>
     * <br/>
     * <i> Note: <code>fromFile</code> and <code>toFile</code> will be closed by
     * this function.</i>
     *
     * @param fromFile - FileInputStream for the file to copy from.
     * @param toFile   - FileInputStream for the file to copy to.
     */
    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    /**
     * Returns the full path of the file on app's data folder where the file should
     * be read from or written to.
     *
     * @param context  Context
     * @param filename Filename
     * @return Full path.
     */
    public static String getFullPath(Context context, String filename) {
        return context.getFilesDir() + "/" + filename;
    }

}