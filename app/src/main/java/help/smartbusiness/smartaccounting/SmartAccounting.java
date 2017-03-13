package help.smartbusiness.smartaccounting;

import android.app.Application;

import com.facebook.stetho.Stetho;

import java.util.Locale;

/**
 * Created by gamerboy on 5/3/17.
 */
public class SmartAccounting extends Application {

    private static Locale appLocale = new Locale("hi", "IN");

    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    public static Locale getAppLocale() {
        return appLocale;
    }
}
