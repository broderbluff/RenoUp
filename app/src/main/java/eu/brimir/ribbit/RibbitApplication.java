package eu.brimir.ribbit;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Patrik on 2015-07-17.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "KXW0uzI8y7VBSViFWywxOTTLz1d7afE9du5CUs46", "9tOay1iOOo1wxjAmyiIpbgIUvwK6KDMjydq2A0tm");


    }
}
