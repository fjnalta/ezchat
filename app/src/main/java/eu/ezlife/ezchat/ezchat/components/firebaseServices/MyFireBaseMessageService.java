package eu.ezlife.ezchat.ezchat.components.firebaseServices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.ContactListActivity;


/**
 * Created by ajo on 06.04.17.
 */
public class MyFireBaseMessageService extends FirebaseMessagingService {

    /**
     * Called when message is received.
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
/*        super.onMessageReceived(remoteMessage);
        createNotification(remoteMessage.getData().get("contact"));*/
    }

    @Override
    public void onNewToken(String s) {
/*        super.onNewToken(s);
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        UserPreferences prefs = new UserPreferences(getApplicationContext());
//        prefs.setPrefFireBaseToken(refreshedToken);

        // TODO - update token at REST Server

        Log.e("NEW_TOKEN",s);*/
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * @param messageBody FCM message body received.
     */
    private void createNotification(String messageBody) {
/*        Intent intent = new Intent(this, ContactListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_chat_send)
                // TODO - get contact Name from Database
                .setContentText("New Message from: " + messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());*/
    }
}