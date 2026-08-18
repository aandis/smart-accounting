package help.smartbusiness.smartaccounting;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    @Test
    public void packageName_isCorrect() {
        Context appContext = ApplicationProvider.getApplicationContext();
        assertEquals("help.smartbusiness.smartaccounting", appContext.getPackageName());
    }
}
