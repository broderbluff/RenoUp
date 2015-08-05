package eu.brimir.renoup.Utils;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;
import com.parse.PushService;

import eu.brimir.renoup.UI.MainActivity;
import eu.brimir.ribbit.R;

/**
 * Created by Patrik on 2015-07-17.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "KXW0uzI8y7VBSViFWywxOTTLz1d7afE9du5CUs46", "9tOay1iOOo1wxjAmyiIpbgIUvwK6KDMjydq2A0tm");

        ParseInstallation.getCurrentInstallation().saveInBackground();



    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USERID, user.getObjectId());
        installation.saveInBackground();
    }





}
