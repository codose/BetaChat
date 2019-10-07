package com.codose.betachat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from_user_id = remoteMessage.getData().get("from_user_id");

        String notification_title = remoteMessage.getNotification().getTitle();

        String notification_body = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();

        Notification.Builder noti = new Notification.Builder(this)
                .setContentTitle(notification_title)
                .setContentText(notification_body)
                .setSmallIcon(R.drawable.notify_logo);

        int notificationId = (int)System.currentTimeMillis();

        Intent intent = new Intent(click_action);
        intent.putExtra("user_id",from_user_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        noti.setContentIntent(pendingIntent);




        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, noti.build());



    }
}
