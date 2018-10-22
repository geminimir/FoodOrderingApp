package app.taxipizza.Utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by user on 24/03/2018.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
