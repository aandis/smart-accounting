package help.smartbusiness.smartaccounting.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by gamerboy on 5/11/16.
 * Base activity for all classes to inherit.
 */
public abstract class SmartAccountingActivity extends AppCompatActivity {
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public void logEvent(String name) {
        mFirebaseAnalytics.logEvent(name, null);
    }

    public void report(Exception e) {
        FirebaseCrash.report(e);
    }

    public void report(String message) {
        FirebaseCrash.report(new Exception(message));
    }
}
