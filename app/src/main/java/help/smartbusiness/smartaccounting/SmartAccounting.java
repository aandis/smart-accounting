package help.smartbusiness.smartaccounting;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by gamerboy on 5/3/17.
 */
public class SmartAccounting extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
