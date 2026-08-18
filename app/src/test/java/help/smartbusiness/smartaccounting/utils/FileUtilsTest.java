package help.smartbusiness.smartaccounting.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class FileUtilsTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void getFullPathJoinsFilesDirAndName() {
        Context context = mock(Context.class);
        when(context.getFilesDir()).thenReturn(new File("/data/data/help.smartbusiness.smartaccounting/files"));

        String path = FileUtils.getFullPath(context, "backup");

        assertEquals("/data/data/help.smartbusiness.smartaccounting/files/backup", path);
    }

    @Test
    public void copyFileCopiesBytesExactly() throws Exception {
        File src = tempFolder.newFile("src.bin");
        byte[] payload = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Files.write(src.toPath(), payload);

        File dst = tempFolder.newFile("dst.bin");
        FileUtils.copyFile(new FileInputStream(src), new FileOutputStream(dst));

        assertArrayEquals(payload, Files.readAllBytes(dst.toPath()));
    }

    @Test
    public void copyFileOverwritesExistingContent() throws Exception {
        File src = tempFolder.newFile("src.bin");
        Files.write(src.toPath(), new byte[]{9, 9, 9});

        File dst = tempFolder.newFile("dst.bin");
        Files.write(dst.toPath(), new byte[]{1, 1, 1, 1, 1, 1, 1, 1});

        FileUtils.copyFile(new FileInputStream(src), new FileOutputStream(dst));

        assertArrayEquals(new byte[]{9, 9, 9}, Files.readAllBytes(dst.toPath()));
    }

    @Test
    public void copyFileEmptyFileProducesEmptyDest() throws Exception {
        File src = tempFolder.newFile("src.bin");
        File dst = tempFolder.newFile("dst.bin");
        FileUtils.copyFile(new FileInputStream(src), new FileOutputStream(dst));
        assertEquals(0, Files.size(dst.toPath()));
    }
}
