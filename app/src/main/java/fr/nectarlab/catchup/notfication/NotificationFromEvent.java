package fr.nectarlab.catchup.notfication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import fr.nectarlab.catchup.Home;
import fr.nectarlab.catchup.Insights;
import fr.nectarlab.catchup.IntentUtils;
import fr.nectarlab.catchup.Invitation;
import fr.nectarlab.catchup.R;


/**
 * Created by ThomasBene on 6/4/2018.
 */

public class NotificationFromEvent extends Activity {
    private final String TAG = "NotificationFromEvent";
    private Context context;
    private String CHANNEL_ID = "CATCHUP";
    private String CHANNEL_NAME = "INVITATIONS";

    public NotificationFromEvent(Context context){
        this.context = context;
    }

    public void setNotificationContent(String eventID){
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Log.i(TAG, "setNotificationContent()");

        Intent i  = new Intent (context, Invitation.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra(IntentUtils.getPendingIntentEventKey(), eventID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, IntentUtils.getNOTIFICATION_SENDER_REQ_CODE(), i, PendingIntent.FLAG_CANCEL_CURRENT);

        String title = context.getString(R.string.contentTitle);
        String content = context.getString(R.string.contentText);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel (CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Invitations");
            nm.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, this.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        /*
         * sauvegarder le fait que la notif a eu lieu pour cet event (grace a son ID)
         */
        Notification n = mBuilder.build();
        nm.notify(0, n);
        Log.i(TAG, "setNotificationContent: Fin");
        Log.i(TAG, "setNotificationContent: Notifie avec eventID: "+eventID);

    }

}
