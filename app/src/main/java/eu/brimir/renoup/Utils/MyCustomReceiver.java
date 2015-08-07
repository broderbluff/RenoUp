package eu.brimir.renoup.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import eu.brimir.renoup.UI.ShowPopUp;
import eu.brimir.ribbit.R;

/**
 * Created by brode on 2015-08-05.
 */
public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent == null)
            {
                Log.d(TAG, "Receiver intent null");
            }
            else
            {
                String action = intent.getAction();
                Log.d(TAG, "got action " + action );
                if (action.equals("eu.brimir.renoup.UPDATE_STATUS"))
                {
                    String channel = intent.getExtras().getString("com.parse.Channel");
                    JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                    String message = json.getString("customdata");
                    String sender = json.getString("sender");
                    Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
                    Iterator itr = json.keys();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();

                        if (key.equals("customdata"))
                        {



                            Intent i = new Intent(context, ShowPopUp.class);
                            i.putExtra(ParseConstants.KEY_MESSAGE, message);
                            i.putExtra(ParseConstants.KEY_SENDER_NAME, sender);


                            PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);





                            NotificationCompat.Builder noti = new NotificationCompat.Builder( context.getApplicationContext())
                                    .setSmallIcon(R.drawable.ic_stat_logo)
                                    .setContentTitle("RenoUp")
                                    .setContentText(message + " - " + sender)
                            .setLights(0x4CAF50, 200, 500)
                                    .setVibrate(new long[]{100,1000,500,1000,500,1000,300,300,300,300,300,1000,500,1000})
                            .setSound(Uri.parse("android.resource://" + "eu.brimir.renoup" + "/" + R.raw.sound2))

                                    .setContentIntent(pIntent);


                            noti.setAutoCancel(true);



                            NotificationManager mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(1, noti.build());


                        }
                        Log.d(TAG, "..." + key + " => " + json.getString(key));
                    }
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
}